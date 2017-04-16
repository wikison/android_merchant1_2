package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.czt.mp3recorder.MP3Recorder;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoiceActivity;
import com.zemult.merchant.aip.reservation.User2RemindIMAddRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.oss.OssFileService;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.sound.HttpOperateUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.PMNumView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.OSSENDPOINT;

public class CustomerCreateBespeakActivity extends BaseActivity {

    @Bind(R.id.bespek_time)
    TextView bespekTime;
    @Bind(R.id.btn_bespeak_commit)
    RoundTextView btnBespeakCommit;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;
    @Bind(R.id.et_customerrenjun)
    EditText etCustomerrenjun;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.fn_my_service)
    FNRadioGroup fnMyService;
    ImageView voiceImageBtn;
    AnimationDrawable voiceAnimation;
    @Bind(R.id.tv_leftsecond)
    TextView tvLeftsecond;
    @Bind(R.id.rel_voice)
    RelativeLayout relVoice;
    @Bind(R.id.tv_length)
    TextView tvLength;
    UserReservationAddRequest userReservationAddRequest;
    int serviceId,num;
    String renjun,orderpeople,servicetag,ordertime,fileUrl;

    String reservationTime,tags;
    double perMoney;
    int merchantId,remindIMId;
    int CHOOSESERVICE = 100;
    M_Merchant m_merchant;
    User2RemindIMAddRequest user2RemindIMAddRequest;  //用户发送语音预约消息
    String ossFilename = "", filename = "";
    String URL_UPLOAD_FILEPATH = "";
    long lastDownTime;
    long thisEventTime;


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
        setContentView(R.layout.activity_customer_bespeak);

    }


    @Override
    public void init() {
        serviceId = getIntent().getIntExtra("serviceId", 0);
        m_merchant = (M_Merchant) getIntent().getExtras().getSerializable("m_merchant");
        pmnvSelectDeadline.setMinNum(1);
        pmnvSelectDeadline.setMaxNum(99);
        pmnvSelectDeadline.setDefaultNum(1);
        pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
        orderpeople = "" + pmnvSelectDeadline.getDefaultNum();
        pmnvSelectDeadline.setFilter();


        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                orderpeople = num + "";
                pmnvSelectDeadline.setDefaultNum(num);
            }
        });
        lhTvTitle.setText("线上约服");



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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void user_reservation_add() {

        try {
            if (userReservationAddRequest != null) {
                userReservationAddRequest.cancel();
            }
            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
            input.merchantId = merchantId+"";
            input.saleUserId = serviceId;
            input.reservationTime = ordertime + ":00";
            input.num = orderpeople;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userReservationAddRequest = new UserReservationAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                        JSONObject object = new JSONObject();
                        try {
                            object.put("customizeMessageType", "Task");
                            object.put("tasktype", "VOICE");
                            object.put("taskTitle", "发了一个约服需求" + DateTimeUtil.getCurrentTime2() + "如管家2分钟未回复，约服将帮您联系管家并在5分钟内给您回复，请稍等...");
                            object.put("merchantId", m_merchant.merchantId);
                            object.put("reviewstatus", m_merchant.reviewstatus);
                            object.put("merchantName", m_merchant.merchantName);
                            object.put("userId", SlashHelper.userManager().getUserId());
                            object.put("fromuserName", SlashHelper.userManager().getUserinfo().name);
                            object.put("fromuserHead", SlashHelper.userManager().getUserinfo().head);
                            object.put("recordPath", fileUrl);
                            object.put("remindIMId", orderpeople);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[预约服务]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(serviceId + "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);
                        startActivity(imKit.getChattingActivityIntent(serviceId + ""));
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userReservationAddRequest);
        } catch (Exception e) {
        }
    }


    private void initTags(String tags) {
        fnMyService.setChildMargin(0, 24, 24, 0);
        fnMyService.removeAllViews();
        if (!StringUtils.isBlank(tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                fnMyService.setVisibility(View.VISIBLE);

                RadioButton rbTitle = new RadioButton(this);
                rbTitle.setTextSize(15);
                rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                rbTitle.setPadding(0, 0, 8, 0);
                rbTitle.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rbTitle.setTextColor(0xff282828);
//                rbTitle.setText("TA的服务");
                fnMyService.addView(rbTitle);

                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(this);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    fnMyService.addView(rbTag);

                }
            } else {
                fnMyService.setVisibility(View.GONE);
            }
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit, R.id.rl_ordershopname, R.id.rl_ordertime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                if (noLogin(CustomerCreateBespeakActivity.this))
                    return;
                ordertime = bespekTime.getText().toString();
                renjun=etCustomerrenjun.getText().toString();
                if (StringUtils.isEmpty(orderpeople)) {
                    ToastUtil.showMessage("请选择预约人数");
                    return;
                }

                if (StringUtils.isEmpty(ordertime) || "请选择预约时间".equals(ordertime)) {
                    ToastUtil.showMessage("请选择预约时间");
                    return;
                }
                if (!StringMatchUtils.isMobileNO(renjun)) {
                    ToastUtil.showMessage("请输入人均");
                    return;
                }

                user_reservation_add();
                break;

            case R.id.rl_ordertime:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, bespekTime.getText().toString(), "预约时间必须大于当前时间", 1);
                dateTimePicKDialog.dateTimePicKDialog(bespekTime);
                break;

        }
    }


    private void addRemindIM() {
        if (user2RemindIMAddRequest != null) {
            user2RemindIMAddRequest.cancel();
        }
        final User2RemindIMAddRequest.Input input = new User2RemindIMAddRequest.Input();

        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId; //服务商户id
        input.reservationTime = reservationTime;//预约时间
        input.num = num;//人数
        input.perMoney = perMoney;//人均预算
        input.tags = tags;//服务管家id
        input.saleUserId = serviceId;//服务管家id
        input.replayNote = fileUrl;//否	语音地址
        input.convertJosn();



        user2RemindIMAddRequest = new User2RemindIMAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    int remindIMId = ((CommonResult) response).remindIMId;
                    YWCustomMessageBody messageBody = new YWCustomMessageBody();
                    //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                    JSONObject object = new JSONObject();
                    try {
                        object.put("customizeMessageType", "Task");
                        object.put("tasktype", "VOICE");
                        object.put("taskTitle", "发了一个约服需求" + DateTimeUtil.getCurrentTime2() + "如管家2分钟未回复，约服将帮您联系管家并在5分钟内给您回复，请稍等...");
                        object.put("merchantId", m_merchant.merchantId);
                        object.put("reviewstatus", m_merchant.reviewstatus);
                        object.put("merchantName", m_merchant.merchantName);
                        object.put("userId", SlashHelper.userManager().getUserId());
                        object.put("fromuserName", SlashHelper.userManager().getUserinfo().name);
                        object.put("fromuserHead", SlashHelper.userManager().getUserinfo().head);
                        object.put("recordPath", fileUrl);
                        object.put("remindIMId", remindIMId);
                    } catch (JSONException e) {

                    }
                    messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                    messageBody.setSummary("[预约服务]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                    YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                    YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                    IYWContact appContact = YWContactFactory.createAPPContact(serviceId+"", imKit.getIMCore().getAppKey());
                    imKit.getConversationService()
                            .forwardMsgToContact(appContact
                                    , message, forwardCallBack);
                    startActivity(imKit.getChattingActivityIntent(serviceId + ""));

                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(user2RemindIMAddRequest);
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



    public void stopPlay() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (voiceAnimation != null) {
            voiceImageBtn.setImageResource(R.mipmap.chatfrom_voice_playing_f3);
            voiceAnimation.stop();
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
                Toast.makeText(CustomerCreateBespeakActivity.this, "您的语音时间太短！",
                        Toast.LENGTH_SHORT).show();
            }
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
                    mMediaPlayer = MediaPlayer.create(CustomerCreateBespeakActivity.this,
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




    public void setTalkBtnBackground(boolean isTalk) {
        if (isTalk) {
            imageButton.setBackgroundResource(R.mipmap.btn_speak_pressed);
        } else {
            imageButton.setBackgroundResource(R.mipmap.btn_speak_normal);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSESERVICE && resultCode == RESULT_OK) {
            initTags(data.getStringExtra("tags"));
            merchantId = data.getIntExtra("merchantId", 0);
        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(CustomerCreateBespeakActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(CustomerCreateBespeakActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
