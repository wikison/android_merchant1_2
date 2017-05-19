package com.zemult.merchant.im;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
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
import com.bigkoo.pickerview.TimePickerView;
import com.czt.mp3recorder.MP3Recorder;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.activity.mine.TabManageSecondActivity;
import com.zemult.merchant.aip.reservation.User2RemindIMAddRequest;
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
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.oss.OssFileService;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.sound.HttpOperateUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.PMNumView;
import com.zemult.merchant.view.RecordDialog;
import com.zemult.merchant.view.common.CommonDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
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
    @Bind(R.id.iv_voice)
    ImageView voiceImageBtn;
    AnimationDrawable voiceAnimation;
    @Bind(R.id.rel_voice)
    RelativeLayout relVoice;
    @Bind(R.id.tv_length)
    TextView tvLength;

    String selectTag = "";
    private MediaPlayer mMediaPlayer;

    int serviceId;
    String renjun,orderpeople,servicetag,ordertime;
    private static final int REQ_ALBUM = 0x110;
    private static final int REQ_REMARK_NAME = 0x120;

    private static final int STATE_NORMAL = 1;// 默认的状态
    private static final int STATE_RECORDING = 2;// 正在录音
    private static final int STATE_WANT_TO_CANCEL = 3;// 希望取消

    private int mCurrentState = STATE_NORMAL; // 当前的状态

    private static final int DISTANCE_Y_CANCEL = 50;

    private static final int MSG_AUDIO_PREPARED = 0x110;
    private static final int MSG_VOICE_CHANGED = 0x111;
    private static final int MSG_DIALOG_DISMISS = 0x112;
    private static final int MSG_VOICE_FINISH = 0x113;
    String tags="";
    int CHOOSESERVICE = 100,MY_PERMISSIONS_REQUEST_CALL_AUDIO=199;
    M_Merchant m_merchant;
    User2RemindIMAddRequest user2RemindIMAddRequest;  //用户发送语音预约消息

    RecordDialog mDialogManager;
    String ossFilename = "", filename = "";
    String URL_UPLOAD_FILEPATH = "";
    private OssFileService ossFileService;
    private MP3Recorder mRecorder = null;
    String fileUrl = "",serviceTags="";
    private MyTimerTask timerTask;
    private boolean isStartRecord;
    ImageButton imageButtonDial;
    private Timer timer;
    private int recordTime = 60;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    // 开始录音后显示对话框
                    mDialogManager.showRecordingDialog();
                    isStartRecord = true;
                    break;
                case MSG_VOICE_CHANGED:
                    if (isStartRecord) {
                        recordTime--;
                        System.out.println("recordTime left" + recordTime + "");
                    } else {
                        if (timerTask != null) {
                            timerTask.cancel();
                            timer.cancel();
                        }
                    }
                    break;

                case MSG_DIALOG_DISMISS:
                    mDialogManager.dismissDialog();
                    break;
                case MSG_VOICE_FINISH:
                    if (timerTask != null) {
                        timerTask.cancel();
                        timer.cancel();
                    }
                    recordVoice();
                    break;

                case 1:
                    voiceImageBtn.setImageResource(R.mipmap.luying_icon);
                    voiceAnimation.stop();
                    break;
                case 2:
                    voiceImageBtn.setImageResource(R.drawable.voice_from_icon);
                    voiceAnimation = (AnimationDrawable) voiceImageBtn.getDrawable();
                    voiceAnimation.start();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_customer_bespeak);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_AUDIO)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
//                startRecord();
            } else
            {
                // Permission Denied
                Toast.makeText(CustomerCreateBespeakActivity.this, "需要开启录音权限", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void init() {

        serviceId = getIntent().getIntExtra("userSaleId", 0);
        m_merchant = (M_Merchant) getIntent().getExtras().getSerializable("m_merchant");
        pmnvSelectDeadline.setMinNum(0);
        pmnvSelectDeadline.setMaxNum(99);
        pmnvSelectDeadline.setDefaultNum(1);
        pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
        orderpeople = "" + pmnvSelectDeadline.getDefaultNum();
        pmnvSelectDeadline.setFilter();
        tags=m_merchant.tags;
        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                orderpeople = num + "";
                pmnvSelectDeadline.setDefaultNum(num);
            }
        });
        lhTvTitle.setText("线上约服");

        ordertime= DateTimeUtil.getOrderTime().replace("(当天) ","")+":00";
        bespekTime.setText(DateTimeUtil.getOrderTime());

        imageButtonDial=(ImageButton)findViewById(R.id.imageButtonDial);

        imageButtonDial.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                imageButtonDial.setBackgroundResource(R.mipmap.btn_speak_pressed);

                if (ContextCompat.checkSelfPermission(CustomerCreateBespeakActivity.this,
                        Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(CustomerCreateBespeakActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            MY_PERMISSIONS_REQUEST_CALL_AUDIO);
                } else
                {
                    mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
                    changeState(STATE_RECORDING);
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
                }




                return true;
            }
        });

        imageButtonDial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                int x = (int) event.getX();// 获得x轴坐标
                int y = (int) event.getY();// 获得y轴坐标
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        imageButtonDial.setBackgroundResource(R.mipmap.btn_speak_pressed);
                        if (noLogin(CustomerCreateBespeakActivity.this)) {
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        imageButtonDial.setBackgroundResource(R.mipmap.btn_speak_normal);
                        if (!isStartRecord || recordTime > 57) {
                            mDialogManager.tooShort();
                            stopRecord();
                            mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1000);// 延迟显示对话框
                        } else if (mCurrentState == STATE_RECORDING) { // 正在录音的时候，结束
                            mDialogManager.dismissDialog();
                            recordVoice();

                        } else if (mCurrentState == STATE_WANT_TO_CANCEL) { // 想要取消
                            mDialogManager.dismissDialog();
                        }
                        reset();
                        break;
                    case MotionEvent.ACTION_CANCEL: // 首次开权限时会走这里，录音取消
                        imageButtonDial.setBackgroundResource(R.mipmap.btn_speak_normal);
                        mDialogManager.wantToCancel();
                        stopRecord();
                        break;

                    case MotionEvent.ACTION_MOVE: // 滑动手指
                        imageButtonDial.setBackgroundResource(R.mipmap.btn_speak_pressed);
                        if (isStartRecord) {
                            // 如果想要取消，根据x,y的坐标看是否需要取消
                            if (wantToCancel(x, y)) {
                                changeState(STATE_WANT_TO_CANCEL);
                            } else {
                                changeState(STATE_RECORDING);
                            }
                        }
                        break;
                    default:
                        break;

                }
                return false;
            }
        });

        mDialogManager = new RecordDialog(this);
        ossFileService = OssHelper.initFileOSS(this);
        filename = SlashHelper.userManager().getUserId() + System.currentTimeMillis() + ".mp3";
        File downloadFile = new File(Constants.SOUND_CACHE_DIR);
        AppUtils.deleteAllFiles(downloadFile);
        if (!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        URL_UPLOAD_FILEPATH = Constants.SOUND_CACHE_DIR + filename;
        mRecorder = new MP3Recorder(new File(URL_UPLOAD_FILEPATH));
        registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADSOUND});
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    private void showTimePicker() {
        Date now = new Date();
        Calendar selectedDate = new GregorianCalendar();
        if (!StringUtils.isBlank(ordertime)) {
            selectedDate.setTime(DateTimeUtil.getDate(ordertime, "yyyy-MM-dd HH:mm:ss"));
        }
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(now);
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(DateTimeUtil.getDateAdd(now, 365));
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                Date now = new Date();
                if (date.getTime() - now.getTime() < 0) {
                    ToastUtil.showMessage("选择时间不能晚于当前时间");
                } else {
                    ordertime = DateTimeUtil.getFormatTime(date);
                    bespekTime.setText(getTime(date));
                }

            }
        }).setType(TimePickerView.Type.YEAR_MONTH_DAY_HOUR_MIN)//默认全部显示
                .setCancelText("取消")//取消按钮文字
                .setSubmitText("确定")//确认按钮文字
                .setContentSize(18)//滚轮文字大小
                .setTitleSize(20)//标题文字大小
                .setTitleText("选择时间")//标题文字
                .setOutSideCancelable(false)//点击屏幕，点在控件外部范围时，是否取消显示
                .isCyclic(false)//是否循环滚动
                .setTitleColor(Color.BLACK)//标题文字颜色
                .setSubmitColor(getResources().getColor(R.color.font_main))//确定按钮文字颜色
                .setCancelColor(Color.BLACK)//取消按钮文字颜色
                .setTitleBgColor(Color.WHITE)//标题背景颜色 Night mode
                .setBgColor(Color.WHITE)//滚轮背景颜色 Night mode
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .isDialog(false)//是否显示为对话框样式
                .build();
        pvTime.show();
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd (*) HH:mm");
        return format.format(date).replace("*", DateTimeUtil.getWeekDayOfWeekisToday(date));
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.btn_delete, R.id.rl_my_service,R.id.btn_bespeak_commit, R.id.rl_ordertime,R.id.rll_voice})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                if (noLogin(CustomerCreateBespeakActivity.this))
                    return;
                renjun=etCustomerrenjun.getText().toString();
                if(StringUtils.isBlank(fileUrl)){
                    if (StringUtils.isEmpty(orderpeople)) {
                        ToastUtil.showMessage("请选择预约人数");
                        return;
                    }

                    if ("请选择预约时间".equals(bespekTime.getText().toString())) {
                        ToastUtil.showMessage("请选择预约时间");
                        return;
                    }
//                    if (StringUtils.isEmpty(renjun)) {
//                        ToastUtil.showMessage("请输入人均预算");
//                        return;
//                    }


                }
                addRemindIM();
                break;

            case R.id.rl_ordertime:
//                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//                        this, bespekTime.getText().toString(), "预约时间必须大于当前时间", 1);
//                dateTimePicKDialog.dateTimePicKDialog(bespekTime);

                showTimePicker();
                break;

            case R.id.rll_voice:
                startPlay();

                break;
            case R.id.rl_my_service:
                Intent intent = new Intent(this, TabManageSecondActivity.class);
                intent.putExtra(TabManageSecondActivity.COMEFROM, 2);
                intent.putExtra(TabManageSecondActivity.SELECTEDTAGS, selectTag);
                intent.putExtra(TabManageActivity.TAGS, tags);
                startActivityForResult(intent, 111);
                break;

            case R.id.btn_delete:

                CommonDialog.showDialogListener(CustomerCreateBespeakActivity.this,null, "否", "是", "是否删除语音消息", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        fileUrl="";
                        recordTime=60;
                        relVoice.setVisibility(View.INVISIBLE);
                    }
                });

                break;
        }
    }


    public void startPlay() {
        stopPlay();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub
                if(StringUtils.isEmpty(fileUrl)){
                    fileUrl = OSSENDPOINT + ossFilename;
                }
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
                mHandler.sendMessage(message);
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
                            mHandler.sendMessage(message);
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
            voiceImageBtn.setImageResource(R.mipmap.luying_icon);
            voiceAnimation.stop();
        }

    }

    @Override
    protected void onDestroy() {
        stopPlay();
        stopRecord();
        super.onDestroy();
    }

    private void addRemindIM() {
        if (user2RemindIMAddRequest != null) {
            user2RemindIMAddRequest.cancel();
        }
        final User2RemindIMAddRequest.Input input = new User2RemindIMAddRequest.Input();

        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = m_merchant.merchantId; //服务商户id
        if(ordertime.length()<17){
            input.reservationTime= ordertime+ ":00";
        }
        else{
            input.reservationTime= ordertime;
        }
        input.num = orderpeople;//人数
        input.perMoney =renjun ;//人均预算
        input.tags =StringUtils.isBlank(selectTag)?"":selectTag ;//服务管家id
        input.saleUserId = serviceId;//服务管家id
        input.replayNote = fileUrl;//否	语音地址
        input.timeNum = (60-recordTime)+"";//否	时间
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
                        object.put("taskTitle", "【线上约服】我发起了一个线上约服需求，快来看一看吧！");
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
                    messageBody.setSummary("[线上约服]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                    YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                    YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                    IYWContact appContact = YWContactFactory.createAPPContact(serviceId+"", imKit.getIMCore().getAppKey());
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
        sendJsonRequest(user2RemindIMAddRequest);
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
                tvLength.setText((60-recordTime)+"''");
            } else {
                ToastUtils.show(this, intent.getStringExtra("info"));
            }
        }
    }


    /**
     * 恢复状态及标志位
     */
    private void reset() {
        isStartRecord = false;
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > imageButtonDial.getWidth()) { // 超过按钮的宽度
            return true;
        }
        // 超过按钮的高度
        if (y < -DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    private void startRecord() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            ToastUtil.showMessage("请插入SD卡！");
            return;
        }
        isStartRecord = true;

        try {
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if (null != mRecorder) {
            mRecorder.stop();
        }
        isStartRecord = false;
    }

    private void recordVoice() {
        //结束计时器
        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
        }

        if (isStartRecord) {
            // 停止录音
            stopRecord();
            File file = new File(URL_UPLOAD_FILEPATH);
            if (file.exists() && recordTime <= 57) {
                showPd();
                if (SlashHelper.userManager().getUserinfo() != null) {
                    ossFilename = "aduio/android_" + filename;
                    ossFileService.asyncPutFile(ossFilename, URL_UPLOAD_FILEPATH);
                }
            } else {
                mDialogManager.tooShort();
                // 延迟显示对话框
                mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 500);
            }
        }

    }

    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (state) {
                case STATE_NORMAL:
                    break;

                case STATE_RECORDING:
                    if (isStartRecord) {
                        mDialogManager.recording();
                    }
                    break;

                case STATE_WANT_TO_CANCEL:
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }
    /* 秒表计时器-Task */
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            if (recordTime != 0) {
                mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
            } else {
                mHandler.sendEmptyMessage(MSG_VOICE_FINISH);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&resultCode==RESULT_OK){
            selectTag = data.getStringExtra("result");
            initTags(selectTag);
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
