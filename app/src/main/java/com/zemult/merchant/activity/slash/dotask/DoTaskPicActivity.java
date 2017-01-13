package com.zemult.merchant.activity.slash.dotask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.aip.task.TaskIndustryCompleteImgRequest;
import com.zemult.merchant.app.BaseImageChooseActivity;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.imagepicker.ImagePickerAdapter;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.oss.OssService;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/1.
 */
public class DoTaskPicActivity extends BaseImageChooseActivity implements
        ImagePickerAdapter.ChooseImageAdapterCallBack {

    public final static String INTENT_TASK = "task";
    protected List<String> ossImgnameList = new ArrayList<String>();
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_task_info)
    TextView tvTaskInfo;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.tv_voucher)
    TextView tvVoucher;
    @Bind(R.id.iv_publish_icon)
    ImageView ivPublishIcon;
    @Bind(R.id.tv_publish_name)
    TextView tvPublishName;
    @Bind(R.id.iv_complaints)
    ImageView ivComplaints;
    @Bind(R.id.etContent)
    EditText etContent;
    @Bind(R.id.editnum)
    TextView tvNumber;
    @Bind(R.id.gvImgs)
    MeasuredGridView gvImgs;
    @Bind(R.id.viewImgs)
    LinearLayout viewImgs;
    String content;
    int num = 100, callbackCount = 0;
    TaskIndustryCompleteImgRequest taskIndustryCompleteImgRequest;
    int taskIndustryRecordId;
    M_Task intent_task;
    ImageManager mImageManager;
    private OssService ossService;

    protected void beforeInit() {
        intent_task = (M_Task) getIntent().getSerializableExtra(INTENT_TASK);
        taskIndustryRecordId = intent_task.taskIndustryRecordId;
        if (taskIndustryRecordId <= 0) {
            taskIndustryRecordId = getIntent().getIntExtra("task_industry_record_id", -1);
        }

    }

    @Override
    protected void initView() {
        mImageManager = new ImageManager(this);

        setTitleText("图文任务");
        setTitleLeftButton("");
        setTitleRightButton("完成");


        View appView = getLayoutInflater().inflate(
                R.layout.activity_do_task_pic, null);
        ButterKnife.bind(this, appView);
        appMainView.addView(appView, layoutParams);
        tvNote.setText(intent_task.note);
        tvTitle.setText(intent_task.title);
        tvExp.setText(String.format("经验X%s", String.valueOf(intent_task.experience)));
        String remainder = "";

        switch (intent_task.cashType) {
            case 0:
                tvBonuses.setVisibility(View.GONE);
                tvVoucher.setVisibility(View.GONE);
                break;
            case 1:
                tvBonuses.setVisibility(View.VISIBLE);
                tvVoucher.setVisibility(View.GONE);
                remainder = "红包余量" + (intent_task.bonuseNum - intent_task.outNum) + "/" + intent_task.bonuseNum;
                break;
            case 2:
                tvBonuses.setVisibility(View.GONE);
                tvVoucher.setVisibility(View.VISIBLE);
                tvVoucher.setText(String.format("代金券%s元", intent_task.voucherMoney));
                remainder = "代金券余量" + (intent_task.voucherNum - intent_task.outVoucherNum) + "/" + intent_task.voucherNum;
                break;
        }

        if (!StringUtils.isBlank(remainder)) {
            remainder = "| " + remainder;
        }
        tvTaskInfo.setText(String.format("%sde任务 %s结束", intent_task.industryName,
                "| " + DateTimeUtil.strPubEndDiffTime(intent_task.endtime)));
        tvNote.setText(intent_task.note);

        mImageManager.loadCircleHasBorderImage(intent_task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
        tvPublishName.setText(String.format("%s 发布", intent_task.userName));
        ivComplaints.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        //注册上传图片广播
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE});

        initListener();
    }

    private void initListener() {
        EditFilter.WordFilter(etContent, 500, tvNumber);
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
                ++callbackCount;
                if (ossImgnameList.size() == callbackCount) {
                    doPicTask();
                }
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }

        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.top_right_btn:
                uploadImage();
                break;
            case R.id.iv_complaints:
                Intent intent=new Intent(DoTaskPicActivity.this,ReportActivity.class);
                intent.putExtra("infoId",intent_task.taskIndustryId);
                intent.putExtra("infoType",1);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 上传图片
     */
    private void uploadImage() {
        content = etContent.getText().toString().trim();
        if ((content == null || "".equals(content)) && (photos.isEmpty())) {
            Toast.makeText(this, "不能发布空的内容", Toast.LENGTH_SHORT).show();
            return;
        }
        showPd();
        if (photos.size() > 0) {
            //有图片
            ossService = OssHelper.initOSS(this);
            for (int i = 0; i < photos.size(); i++) {
                if (SlashHelper.userManager().getUserinfo() != null) {
                    String ossImgname = "app/android_" + SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".jpg";
                    ossService.asyncPutImage(ossImgname, photos.get(i));
                    ossImgnameList.add(Constants.OSSENDPOINT + ossImgname);
                    Log.d(getClass().getName(), ossImgname);
                }

            }
        } else {
            //没图片
            doPicTask();
        }
    }

    //用户做图文任务
    private void doPicTask() {
        if (taskIndustryCompleteImgRequest != null) {
            taskIndustryCompleteImgRequest.cancel();
        }

        TaskIndustryCompleteImgRequest.Input input = new TaskIndustryCompleteImgRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.note = content;
        for (int i = 0; i < ossImgnameList.size(); i++) {
            if (i != ossImgnameList.size() - 1) {
                input.pic += ossImgnameList.get(i) + ",";
            } else {
                input.pic += ossImgnameList.get(i);
            }
        }
        input.convertJosn();

        ossImgnameList.clear();
        callbackCount = 0;
        taskIndustryCompleteImgRequest = new TaskIndustryCompleteImgRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.toString());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("发布成功");
                    Intent intent = new Intent(Constants.BROCAST_FRESHSLASH);
                    sendBroadcast(intent);
                    sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryCompleteImgRequest);
    }
}

