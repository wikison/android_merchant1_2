package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.activity.mine.TabManageSecondActivity;
import com.zemult.merchant.aip.mine.User2RemindIMInfoRequest;
import com.zemult.merchant.aip.reservation.User2RemindIMReadRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.sound.HttpOperateUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.PMNumView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.OSSENDPOINT;

public class CustomerCreateBespeakDetailsActivity extends BaseActivity {

    User2RemindIMInfoRequest user2RemindIMInfoRequest;
    User2RemindIMReadRequest user2RemindIMReadRequest;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.v_user)
    ImageView vUser;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_shopname)
    TextView tvShopname;
    @Bind(R.id.tv_shijian)
    TextView tvShijian;
    @Bind(R.id.tv_title_select_deadline)
    TextView tvTitleSelectDeadline;
    @Bind(R.id.tv_renshu)
    TextView tvRenshu;
    @Bind(R.id.tv_renjun)
    TextView tvRenjun;
    @Bind(R.id.fn_my_service)
    FNRadioGroup fnMyService;
    @Bind(R.id.tv_my_voice)
    TextView tvMyVoice;
    @Bind(R.id.tv_length)
    TextView tvLength;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_my_voice)
    RelativeLayout rlMyVoice;
    @Bind(R.id.iv_voice)
    ImageView voiceImageBtn;

    AnimationDrawable voiceAnimation;
    int remindIMId, userId;
    String tags, fileUrl, name;
    String selectTag = "";
    private MediaPlayer mMediaPlayer;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_customer_bespeak_details);
    }


    @Override
    public void init() {
        lhTvTitle.setText("线上约服需求");
        remindIMId = getIntent().getIntExtra("remindIMId", 0);
        userId = getIntent().getIntExtra("userId", 0);
        if (userId != SlashHelper.userManager().getUserId()) {
            user2RemindIMReadRequest();
        }
        user2RemindIMInfoRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void user2RemindIMReadRequest() {

        try {
            if (user2RemindIMReadRequest != null) {
                user2RemindIMReadRequest.cancel();
            }
            User2RemindIMReadRequest.Input input = new User2RemindIMReadRequest.Input();
            input.remindIMId = remindIMId;
            input.convertJosn();

            user2RemindIMReadRequest = new User2RemindIMReadRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((M_Reservation) response).status == 1) {
                        ToastUtil.showMessage("已读消息");
                    } else {
                        ToastUtil.showMessage(((M_Reservation) response).info);
                    }
                }
            });
            sendJsonRequest(user2RemindIMReadRequest);
        } catch (Exception e) {
        }
    }

    private void user2RemindIMInfoRequest() {

        try {
            if (user2RemindIMInfoRequest != null) {
                user2RemindIMInfoRequest.cancel();
            }
            User2RemindIMInfoRequest.Input input = new User2RemindIMInfoRequest.Input();
            input.remindIMId = remindIMId;
            input.convertJosn();

            user2RemindIMInfoRequest = new User2RemindIMInfoRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((M_Reservation) response).status == 1) {
                        M_Reservation m_reservation = ((M_Reservation) response);
                        initTags(m_reservation.tags);
                        tvShopname.setText(m_reservation.merchantName);
                        tvName.setText(m_reservation.userName);
                        tags = m_reservation.tags;
                        name = m_reservation.userName;
                        imageManager.loadCircleImage(m_reservation.userHead, vUser);
                        tvShijian.setText(m_reservation.reservationTime);
                        tvRenshu.setText(m_reservation.num + "人");
                        tvRenjun.setText(m_reservation.perMoney + "元");
                        fileUrl = m_reservation.replayNote;
                        if (StringUtils.isBlank(m_reservation.replayNote)) {
                            rlMyVoice.setVisibility(View.GONE);
                        } else {
                            rlMyVoice.setVisibility(View.VISIBLE);
                            tvLength.setText(m_reservation.timeNum + "''");
                        }
                    } else {
                        ToastUtil.showMessage(((M_Reservation) response).info);
                    }
                }
            });
            sendJsonRequest(user2RemindIMInfoRequest);
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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rll_voice, R.id.rl_my_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.rll_voice:
                if (!StringUtils.isEmpty(fileUrl))
                    startPlay();
                break;
            case R.id.rl_my_service:
                Intent intent = new Intent(this, TabManageSecondActivity.class);
                intent.putExtra(TabManageSecondActivity.COMEFROM, 2);
                intent.putExtra(TabManageSecondActivity.NAME, name);
                intent.putExtra(TabManageSecondActivity.SELECTEDTAGS, selectTag);
                intent.putExtra(TabManageActivity.TAGS, tags);
                startActivityForResult(intent, 111);
                break;

        }
    }


    public void stopPlay() {

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
        if (voiceAnimation != null) {
            voiceImageBtn.setImageResource(R.mipmap.luying_icon);
            voiceAnimation.stop();
        }

    }

    public void startPlay() {
        stopPlay();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                // TODO Auto-generated method stub
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
                try {
                    mMediaPlayer = MediaPlayer.create(CustomerCreateBespeakDetailsActivity.this,
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
                } catch (Exception e) {
                    Message message1 = new Message();
                    message1.what = 1;
                }
                Looper.loop();
            }
        }).start();

    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
    protected void onDestroy() {
        stopPlay();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111&&resultCode==RESULT_OK){
            selectTag = data.getExtras().getString("result");

            initTags(selectTag);

        }


    }
}
