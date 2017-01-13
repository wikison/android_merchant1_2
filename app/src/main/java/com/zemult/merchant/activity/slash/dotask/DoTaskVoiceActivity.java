package com.zemult.merchant.activity.slash.dotask;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.czt.mp3recorder.MP3Recorder;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.aip.task.TaskIndustryCompleteAudioRequest;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.oss.OssFileService;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.sound.HttpOperateUtil;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.OSSENDPOINT;

/**
 * Created by Wikison on 2016/8/1.
 */
public class DoTaskVoiceActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_task_info)
    TextView tvTaskInfo;
    @Bind(R.id.tv_title_task_describe)
    TextView tvTitleTaskDescribe;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.tv_title_task_reward)
    TextView tvTitleTaskReward;
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
    @Bind(R.id.iv_voice)
    ImageView ivVoice;
    @Bind(R.id.tv_length)
    TextView tvLength;
    @Bind(R.id.imageButtonDial)
    ImageButton imageButtonDial;
    @Bind(R.id.btn_recordagain)
    Button btnRecordagain;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.rel_voice)
    RelativeLayout relVoice;
    String ossFilename = "", filename = "";
    String URL_UPLOAD_FILEPATH = "";
    AnimationDrawable voiceAnimation;
    ImageView voiceImageBtn;
    long lastDownTime;
    long thisEventTime;
    TaskIndustryCompleteAudioRequest taskIndustryCompleteAudioRequest;
    TaskIndustryInfoRequest taskIndustryInfoRequest;
    String fileUrl = "";
    int taskIndustryRecordId, taskIndustryId;
    @Bind(R.id.tv_leftsecond)
    TextView tvLeftsecond;
    @Bind(R.id.tv_right)
    TextView tvRight;
    private boolean isStartRecord;
    private ImageButton imageButton = null;
    private MediaPlayer mMediaPlayer;
    private OssFileService ossFileService;
    private MP3Recorder mRecorder = null;
    //计时器
    private MyTimerTask timerTask;
    private Timer timer;
    private int recordTime = 60;

    {
        filename= SlashHelper.userManager().getUserId()+System.currentTimeMillis()+".mp3";
        File downloadFile = new File(Constants.SOUND_CACHE_DIR);
        AppUtils.deleteAllFiles(downloadFile);
        if (!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        URL_UPLOAD_FILEPATH= Constants.SOUND_CACHE_DIR+filename;
        mRecorder=new MP3Recorder(new File(URL_UPLOAD_FILEPATH));
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    voiceImageBtn.setImageResource(R.mipmap.chatfrom_voice_playing_f3);
                    voiceAnimation.stop();
                    break;
                case 2:
                    voiceImageBtn.setImageResource(R.drawable.voice_from_icon);
                    voiceAnimation = (AnimationDrawable) voiceImageBtn.getDrawable();
                    voiceAnimation.start();
                    break;
                case 3:
                    if(imageButton.isPressed()){
                        recordTime--;
                        tvLeftsecond.setText("剩余秒数"+recordTime+"''");
                    }
                    else{
                        if(timerTask != null){
                            timerTask.cancel();
                            timer.cancel();
                            tvLeftsecond.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
                case 4:
                    if (timerTask != null) {
                        timerTask.cancel();
                        timer.cancel();
                    }
                    recordVoice();
                    break;
            }
            super.handleMessage(msg);
        }

    };
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rel_voice:
                    if (isStartRecord) {
                        stopRecord();
                    }
                    startPlay();
                    break;


                default:
                    break;
            }
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_do_task_voice);
    }

    @Override
    public void init() {
        imageManager.loadResImage(R.mipmap.yuying_icon_big, ivTypeIcon);
        tvRight.setVisibility(View.VISIBLE);
        lhTvTitle.setText("任务");
        tvRight.setText("完成");


        taskIndustryRecordId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryRecordId;
        if (taskIndustryRecordId <= 0) {
            taskIndustryRecordId = getIntent().getIntExtra("task_industry_record_id", -1);
        }
        taskIndustryId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryId;
        task_industry_info();


        ossFileService = OssHelper.initFileOSS(this);
        // initView
        imageButton = (ImageButton) findViewById(R.id.imageButtonDial);
        imageButton.setBackgroundResource(R.mipmap.btn_speak_normal);

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                relVoice.setVisibility(View.INVISIBLE);
                Log.i("keanbin", "onLongClick()");
                lastDownTime = System.currentTimeMillis();
                setTalkBtnBackground(true);
                // 停止播放声音
                stopPlay();
                // 开始录音
                startRecord();

                //开始计时
                if (timerTask != null) {
                    timerTask.cancel();
                    timer.cancel();
                }


                recordTime = 60;
                timer = new Timer(true);
                timerTask = new MyTimerTask();
                timer.scheduleAtFixedRate(timerTask, 0, 1000);
                tvLeftsecond.setVisibility(View.VISIBLE);
                return true;
            }
        });
        imageButton.setOnTouchListener(new MyClickListener());

        relVoice.setOnClickListener(mOnClickListener);

        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADSOUND});

        voiceImageBtn = ((ImageView) findViewById(R.id.iv_voice));

    }

    public void startRecord() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "请插入SD卡！", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        isStartRecord = true;

        try {
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        if (null != mRecorder) {
            mRecorder.stop();
        }
        isStartRecord = false;
    }

    public void startPlay() {
        stopPlay();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub
                fileUrl = OSSENDPOINT + ossFilename;

                String fileName = HttpOperateUtil.downLoadFile(fileUrl,
                        fileUrl.substring(fileUrl.lastIndexOf("/") + 1));

                Log.i("keanbin", "fileName = " + fileName);
                File file = new File(fileName);

                if (!file.exists()) {
//                    Toast.makeText(DoTaskVoiceActivity.this, "没有语音文件！", Toast.LENGTH_SHORT)
//                            .show();
                    return;
                }
                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
                try{
                mMediaPlayer = MediaPlayer.create(DoTaskVoiceActivity.this,
                        Uri.parse(fileName));
                mMediaPlayer.setLooping(false);
                mMediaPlayer.start();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                });
                }catch (Exception e){
                    Message message1 = new Message();
                    message1.what = 1;
                }
                Looper.loop();
            }
        }).start();

    }

    ;

    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (voiceAnimation != null) {
            voiceImageBtn.setImageResource(R.mipmap.chatfrom_voice_playing_f3);
            voiceAnimation.stop();
        }

    }

    @Override
    protected void onDestroy() {
        stopPlay();
        stopRecord();
        super.onDestroy();
    }

    void recordVoice() {

        setTalkBtnBackground(false);
        thisEventTime = System.currentTimeMillis();
        tvLength.setText((60 - recordTime) + "''");

        //结束计时器
        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }


        tvLeftsecond.setVisibility(View.INVISIBLE);
        if (isStartRecord) {
            // 停止录音
            showPd();
            stopRecord();
            File file = new File(URL_UPLOAD_FILEPATH);
            if (file.exists() && recordTime < 55) {
                if (SlashHelper.userManager().getUserinfo() != null) {
                    ossFilename = "aduio/android_" + filename;
                    ossFileService.asyncPutFile(ossFilename, URL_UPLOAD_FILEPATH);
                    Log.d(getClass().getName(), ossFilename);
                }
            } else {
                dismissPd();
                Toast.makeText(DoTaskVoiceActivity.this, "您的语音时间太短！",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_OSS_UPLOADSOUND.equals(intent.getAction())) {
            if (intent.getStringExtra("status").equals("ok")) {
                Log.d(getClass().getName(), ossFilename);
//                ToastUtils.show(this, intent.getStringExtra("info"));
                fileUrl = OSSENDPOINT + ossFilename;
                dismissPd();
                relVoice.setVisibility(View.VISIBLE);
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }
        }
    }

    public void setTalkBtnBackground(boolean isTalk) {
        if (isTalk) {
            imageButton.setBackgroundResource(R.mipmap.btn_speak_pressed);
        } else {
            imageButton.setBackgroundResource(R.mipmap.btn_speak_normal);
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.iv_complaints})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_right:

                if (StringUtils.isEmpty(fileUrl)) {
                    ToastUtil.showMessage("您还没有完成任务");
                } else {
                    task_industry_complete_audio();
                }

                break;
            case R.id.iv_complaints:
                Intent intent = new Intent(DoTaskVoiceActivity.this, ReportActivity.class);
                intent.putExtra("infoId", taskIndustryId);
                intent.putExtra("infoType", 1);
                startActivity(intent);
                break;

        }
    }

    private void task_industry_complete_audio() {

        try {
            if (taskIndustryCompleteAudioRequest != null) {
                taskIndustryCompleteAudioRequest.cancel();
            }
            TaskIndustryCompleteAudioRequest.Input input = new TaskIndustryCompleteAudioRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.taskIndustryRecordId = taskIndustryRecordId;
            input.audio = fileUrl;
            input.audioTime = (60 - recordTime) + "";
            input.convertJosn();
            taskIndustryCompleteAudioRequest = new TaskIndustryCompleteAudioRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("完成任务");
                        Intent intent = new Intent(Constants.BROCAST_FRESHSLASH);
                        sendBroadcast(intent);
                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryCompleteAudioRequest);
        } catch (Exception e) {
        }
    }

    private void task_industry_info() {

        try {
            if (taskIndustryInfoRequest != null) {
                taskIndustryInfoRequest.cancel();
            }
            TaskIndustryInfoRequest.Input input = new TaskIndustryInfoRequest.Input();
            input.taskIndustryId = taskIndustryId;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            taskIndustryInfoRequest = new TaskIndustryInfoRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((APIM_TaskIndustryInfo) response).status;
                    if (status == 1) {
                        M_Task m_task = ((APIM_TaskIndustryInfo) response).taskIndustryInfo;
                        tvTitle.setText(m_task.title);

                        tvExp.setText(String.format("经验X%s", String.valueOf(m_task.experience)));
                        String remainder = "";
                        switch (m_task.cashType) {
                            case 0:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.GONE);
                                break;
                            case 1:
                                tvBonuses.setVisibility(View.VISIBLE);
                                tvVoucher.setVisibility(View.GONE);
                                remainder = "红包余量" + (m_task.bonuseNum - m_task.outNum) + "/" + m_task.bonuseNum;
                                break;
                            case 2:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.VISIBLE);
                                tvVoucher.setText(String.format("代金券%s元", m_task.voucherMoney));
                                remainder = "代金券余量" + (m_task.voucherNum - m_task.outVoucherNum) + "/" + m_task.voucherNum;
                                break;
                        }

                        if (!StringUtils.isBlank(remainder)) {
                            remainder = "| " + remainder;
                        }
                        tvTaskInfo.setText(String.format("%sde任务 %s结束", m_task.industryName,
                                "| " + DateTimeUtil.strPubEndDiffTime(m_task.endtime)));
                        tvNote.setText(m_task.note);
                        imageManager.loadCircleHasBorderImage(m_task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
                        tvPublishName.setText(String.format("%s 发布", m_task.userName));


                    } else {
                        ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryInfoRequest);
        } catch (Exception e) {
        }
    }

    /* 秒表计时器-Task */
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            Message msg = new Message();
            if (recordTime != 0) {
                msg.what = 3;
            } else {
                msg.what = 4;
            }
            handler.sendMessage(msg);
        }

    }

    class MyClickListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            Log.i("keanbin", "event.getAction() = " + event.getAction());
            switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//
//					break;
                case MotionEvent.ACTION_UP:
                    recordVoice();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    setTalkBtnBackground(false);
                    // TODO 异常放开，接下来一般做以下事情：删除录音文件

                    // 停止录音
//					stopRecord();
                    break;

                default:
                    break;
            }
            return false;
        }

    }


}
