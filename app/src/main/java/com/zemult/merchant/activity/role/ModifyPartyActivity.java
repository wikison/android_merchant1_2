package com.zemult.merchant.activity.role;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.task.TaskIndustryCompleteImgRequest;
import com.zemult.merchant.app.BaseImageChooseActivity;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
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
public class ModifyPartyActivity extends BaseImageChooseActivity implements
        ImagePickerAdapter.ChooseImageAdapterCallBack {

    public final static String INTENT_TASK = "task";
    protected List<String> ossImgnameList = new ArrayList<String>();
    @Bind(R.id.etContent)
    EditText etContent;
    @Bind(R.id.editnum)
    TextView tvNumber;
    @Bind(R.id.gvImgs)
    MeasuredGridView gvImgs;
    @Bind(R.id.viewImgs)
    LinearLayout viewImgs;
    String content;
    int callbackCount = 0;
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

        setTitleText("修改活动");
        setTitleLeftButton("");
        setTitleRightButton("完成");


        View appView = getLayoutInflater().inflate(
                R.layout.activity_modify_party, null);
        ButterKnife.bind(this, appView);
        appMainView.addView(appView, layoutParams);
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

