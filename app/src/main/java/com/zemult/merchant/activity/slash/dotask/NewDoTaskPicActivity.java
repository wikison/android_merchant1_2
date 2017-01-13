package com.zemult.merchant.activity.slash.dotask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.PublishTaskFriendActivity;
import com.zemult.merchant.aip.task.TaskIndustryComplete_1_2Request;
import com.zemult.merchant.app.BaseImageChooseActivity;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;
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
public class NewDoTaskPicActivity extends BaseImageChooseActivity implements
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

    @Bind(R.id.rl_remindwho)
    RelativeLayout rlRemindwho;
    @Bind(R.id.rl_putvoice)
    RelativeLayout rlPutvoice;

    @Bind(R.id.tv_remindwho)
    TextView tvRemindwho;
    @Bind(R.id.tv_putvoice)
    TextView tvPutvoice;

    private String friendIds, fileUrl, voiceSeconds;//好友id
    String content;
    int num = 100, callbackCount = 0;
    TaskIndustryComplete_1_2Request taskIndustryComplete_1_2Request;
    int taskIndustryId;
    M_Task intent_task;
    ImageManager mImageManager;
    private OssService ossService;
    String voteIds;

    protected void beforeInit() {
        intent_task = (M_Task) getIntent().getSerializableExtra(INTENT_TASK);
        taskIndustryId = intent_task.taskIndustryId;
        voteIds = getIntent().getStringExtra("voteIds");
    }

    @Override
    protected void initView() {

        setTitleText("参与探索");
        setTitleLeftButton("");
        setTitleRightButton("完成");

        View appView = getLayoutInflater().inflate(
                R.layout.activity_newdo_task_pic, null);
        ButterKnife.bind(this, appView);
        appMainView.addView(appView, layoutParams);

        rlRemindwho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(NewDoTaskPicActivity.this, PublishTaskFriendActivity.class,
                        new Pair<String, String>("requesttype", Constants.BROCAST_PUBLISH_TASK_PERSON));
            }
        });
        rlPutvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewDoTaskPicActivity.this, NewDoTaskVoiceActivity.class);
                intent_task.audio = fileUrl;
                intent_task.audioTime = voiceSeconds;
                intent.putExtra(INTENT_TASK, intent_task);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initData() {
        //注册上传图片广播
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADIMAGE, Constants.BROCAST_PUBLISH_TASK_VOICE, Constants.BROCAST_PUBLISH_TASK_PERSON});
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
        if (Constants.BROCAST_PUBLISH_TASK_PERSON.equals(intent.getAction())) {
            friendIds = intent.getStringExtra("friend_ids");
            int friendNum = intent.getIntExtra("friend_num", 0);
            if (friendNum > 0) {
                tvRemindwho.setText(friendNum + "人");
            }
        }
        if (Constants.BROCAST_PUBLISH_TASK_VOICE.equals(intent.getAction())) {
            voiceSeconds = intent.getStringExtra("voiceSeconds");
            fileUrl = intent.getStringExtra("fileUrl");
            tvPutvoice.setText(voiceSeconds + "s语音");
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
        if ((content == null || "".equals(content)) && (photos.isEmpty()) && StringUtils.isEmpty(voteIds) && (StringUtils.isEmpty(tvPutvoice.getText().toString()))) {
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
        if (taskIndustryComplete_1_2Request != null) {
            taskIndustryComplete_1_2Request.cancel();
        }

        TaskIndustryComplete_1_2Request.Input input = new TaskIndustryComplete_1_2Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryId = taskIndustryId;
        input.note = StringUtils.isEmpty(content) ? "" : content;
        for (int i = 0; i < ossImgnameList.size(); i++) {
            if (i != ossImgnameList.size() - 1) {
                input.pic += ossImgnameList.get(i) + ",";
            } else {
                input.pic += ossImgnameList.get(i);
            }
        }
        input.audio = StringUtils.isEmpty(fileUrl) ? "" : fileUrl;
        input.audioTime = StringUtils.isEmpty(voiceSeconds) ? "" : voiceSeconds;
        input.friends = StringUtils.isEmpty(friendIds) ? "" : friendIds;
        input.voteIds = StringUtils.isEmpty(voteIds) ? "" : voteIds;

        input.convertJosn();

        ossImgnameList.clear();
        callbackCount = 0;
        taskIndustryComplete_1_2Request = new TaskIndustryComplete_1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.toString());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("发布成功");
                    sendBroadcast(new Intent(Constants.BROCAST_FRESHSLASH));
                    sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                    sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKCOMPLETE));
                    Intent intent = new Intent(NewDoTaskPicActivity.this, NewDoTaskFinishActivity.class);
                    intent.putExtra("experience", ((CommonResult) response).experience);
                    intent.putExtra("bonuse_money", ((CommonResult) response).bonuseMoney);
                    intent.putExtra("tag_name", ((CommonResult) response).tagName);
                    startActivityForResult(intent, 1001);
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryComplete_1_2Request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

}

