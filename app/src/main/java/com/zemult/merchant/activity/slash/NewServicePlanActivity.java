package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.slash.User2PlanAddRequest;
import com.zemult.merchant.aip.slash.User2PlanEditRequest;
import com.zemult.merchant.aip.slash.User2PlanInfoRequest;
import com.zemult.merchant.app.BaseImageChooseActivity;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Plan;
import com.zemult.merchant.model.apimodel.APIM_PlanInfo;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.ImagePickerAdapter;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 服务方案
 *
 * @author djy
 * @time 2017/4/26 13:50
 */
public class NewServicePlanActivity extends BaseImageChooseActivity implements
        ImagePickerAdapter.ChooseImageAdapterCallBack {
    public static final String INTENT_MERCHANT_ID = "merchantId";
    public static final String INTENT_PLAN_ID = "planId";
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.tv_title_length)
    TextView tvTitleLength;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.tv_content_length)
    TextView tvContentLength;
    @Bind(R.id.gvImgs)
    MeasuredGridView gvImgs;
    @Bind(R.id.viewImgs)
    LinearLayout viewImgs;
    @Bind(R.id.sc_open)
    SwitchCompat scOpen;

    private int callbackcount;
    private OssService ossService;
    private List<String> ossImgnameList = new ArrayList<String>();

    private int merchantId, planId;
    private Context mContext;

    @Override
    protected void beforeInit() {

    }

    @Override
    protected void initView() {
        if(planId > 0)
            setTitleText("编辑方案");
        else
            setTitleText("发布新方案");

        setTitleLeftButton("");
        setTitleRightButton("发布");
        View appView = getLayoutInflater().inflate(
                R.layout.activity_new_service_plan, null);
        ButterKnife.bind(this, appView);
        appMainView.addView(appView, layoutParams);

        EditFilter.WordFilter(etContent, 100, tvContentLength);
        EditFilter.WordFilter(etTitle, 15, tvTitleLength);
    }

    @Override
    protected void initData() {
        mContext = this;
        merchantId = getIntent().getIntExtra(INTENT_MERCHANT_ID, -1);
        planId = getIntent().getIntExtra(INTENT_PLAN_ID, -1);

        //注册上传图片广播
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE});

        if (planId > 0)
            use2_plan_info();

    }

    // 服务方案详情
    private User2PlanInfoRequest planInfoRequest;

    private void use2_plan_info() {
        showPd();
        if (planInfoRequest != null) {
            planInfoRequest.cancel();
        }

        User2PlanInfoRequest.Input input = new User2PlanInfoRequest.Input();
        input.planId = planId;//	角色行业
        input.convertJosn();
        planInfoRequest = new User2PlanInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PlanInfo) response).status == 1) {
                    M_Plan plan = ((APIM_PlanInfo) response).planInfo;
                    etTitle.setText(plan.name);
                    etContent.setText(plan.note);
                    if (plan.state == 0)
                        scOpen.setChecked(false);
                    else
                        scOpen.setChecked(true);

                    if(!StringUtils.isBlank(plan.pics)){
                        photos.clear();
                        photos.addAll(Arrays.asList(plan.pics.split(",")));
                        imagePickerAdapter.setDataChanged(photos);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_PlanInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(planInfoRequest);
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_OSS_UPLOADIMAGE.equals(intent.getAction())) {
            if (intent.getStringExtra("status").equals("ok")) {
                ++callbackcount;
                if (ossImgnameList.size() == callbackcount) {
                    doSth();
                }
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }

        }
    }

    private void doSth(){
        if (planId > 0)
            user2_plan_edit();
        else
            user2_plan_add();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.top_right_btn:
                uploadImage();
                break;
            default:
                break;
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        if (StringUtils.isBlank(etTitle.getText().toString())) {
            Toast.makeText(this, "请填写方案标题", Toast.LENGTH_SHORT).show();
            return;
        }
        if (StringUtils.isBlank(etContent.getText().toString())
                && photos.isEmpty()) {
            Toast.makeText(this, "请添加方案内容或者图片", Toast.LENGTH_SHORT).show();
            return;
        }
        showPd();
        if (!photos.isEmpty()) {
            //有图片
            ossService = OssHelper.initOSS(this);
            for (int i = 0; i < photos.size(); i++) {
                if (SlashHelper.userManager().getUserinfo() != null
                        && !photos.get(i).startsWith("http://")) {
                    String ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
                    ossService.asyncPutImage(ossImgname, photos.get(i));
                    ossImgnameList.add(Constants.OSSENDPOINT + ossImgname);
                    Log.d(getClass().getName(), ossImgname);
                }
            }
            if(ossImgnameList.isEmpty())
                doSth();
        } else {
            //没图片
            doSth();
        }
    }

    private User2PlanAddRequest planAddRequest;

    //发布服务方案
    void user2_plan_add() {
        if (planAddRequest != null) {
            planAddRequest.cancel();
        }

        User2PlanAddRequest.Input input = new User2PlanAddRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.saleUserId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.state = scOpen.isChecked() ? 1 : 0;
        input.name = etTitle.getText().toString().trim();
        input.note = etContent.getText().toString().trim();

        for (int i = 0; i < ossImgnameList.size(); i++) {
            input.pics += ossImgnameList.get(i) + ",";
        }
        input.convertJosn();

        ossImgnameList.clear();
        callbackcount = 0;

        planAddRequest = new User2PlanAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.toString());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("发布成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(planAddRequest);
    }
    //编辑服务方案
    private User2PlanEditRequest planEditRequest;

    void user2_plan_edit() {
        if (planEditRequest != null) {
            planEditRequest.cancel();
        }

        User2PlanEditRequest.Input input = new User2PlanEditRequest.Input();
        input.planId = planId;
        input.state = scOpen.isChecked() ? 1 : 0;
        input.name = etTitle.getText().toString().trim();
        input.note = etContent.getText().toString().trim();

        for (int i = 0; i < photos.size(); i++) {
            if(photos.get(i).startsWith("http://")){
                input.pics += photos.get(i) + ",";
            }
        }
        for (int i = 0; i < ossImgnameList.size(); i++) {
            input.pics += ossImgnameList.get(i) + ",";
        }
        input.convertJosn();

        ossImgnameList.clear();
        callbackcount = 0;

        planEditRequest = new User2PlanEditRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.toString());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("修改成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(planEditRequest);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
