package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.ChooseReservationMerchantActivity;
import com.zemult.merchant.aip.mine.UserInfoOwnerRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;
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
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class CreateBespeakNewActivity extends BaseActivity {

    @Bind(R.id.bespek_time)
    TextView bespekTime;
    @Bind(R.id.bespek_shopname)
    TextView bespekShopname;
    @Bind(R.id.btn_bespeak_commit)
    RoundTextView btnBespeakCommit;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;
    @Bind(R.id.et_dingjin)
    EditText etDingjin;
    @Bind(R.id.et_customerremark)
    EditText etCustomerRemark;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.play_btn)
    Button playBtn;
    @Bind(R.id.tv_customername)
    TextView tvCustomername;
    @Bind(R.id.v_user)
    ImageView vUser;
    @Bind(R.id.rl_customerphone)
    RelativeLayout rlCustomerphone;
    @Bind(R.id.tv_dingjin_tpis)
    TextView tvDingjinTpis;
    private MediaPlayer mMediaPlayer;


    UserInfoOwnerRequest userInfoOwnerRequest;
    UserReservationAddRequest userReservationAddRequest;
    int customerId;
    String shopname = "", ordertime = "",strdingjin="",strremark="", orderpeople, note,customerName,customerHead,customerVoice;
    String merchantId,reviewstatus;
    int CHOOSEMERCHANT = 100;
    boolean isFromMerchant;

    int showOrderState;//1 生成预约单(有语音)  2 生成预约单（无语音）
                       // 3 预约单 （待确定  服务管家） 4 预约单 （待确定  客户）
                       //5  已确认（服务管家）  6  已确认（客户）


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeaknew);

    }


    @Override
    public void init() {

        customerId = getIntent().getIntExtra("customerId", 0);

        customerVoice= getIntent().getStringExtra("customerVoice");
        merchantId= getIntent().getStringExtra("merchantId");
        reviewstatus= getIntent().getStringExtra("reviewstatus");

        if(null!=customerVoice){
            playBtn.setVisibility(View.VISIBLE);
        }
        else{
            playBtn.setVisibility(View.GONE                         );
        }

        if (null!=merchantId&&null!=reviewstatus) {

            if(reviewstatus.equals("2")){
                rlCustomerphone.setVisibility(View.VISIBLE);
                tvDingjinTpis.setVisibility(View.VISIBLE);
            }
            else {
                rlCustomerphone.setVisibility(View.GONE);
                tvDingjinTpis.setVisibility(View.GONE);
            }
            shopname =getIntent().getStringExtra("merchantName");
            bespekShopname.setText(shopname);
            bespekShopname.setCompoundDrawables(null, null, null, null);
        } else {
            bespekShopname.setText("请选择商户");
        }

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
        lhTvTitle.setText("找TA约服");

        get_user_info_owner_request();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //获取用户自身的资料（包含关注数/粉丝数）
    private void get_user_info_owner_request() {
        if (userInfoOwnerRequest != null) {
            userInfoOwnerRequest.cancel();
        }
        showPd();
        UserInfoOwnerRequest.Input input = new UserInfoOwnerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId =customerId+"";
            input.convertJosn();
        }

        userInfoOwnerRequest = new UserInfoOwnerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    tvCustomername.setText(((APIM_UserLogin) response).userInfo.getName());
                    imageManager.loadCircleHead(((APIM_UserLogin) response).userInfo.getHead(), vUser);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userInfoOwnerRequest);
    }


    private void user_reservation_add() {

        try {
            if (userReservationAddRequest != null) {
                userReservationAddRequest.cancel();
            }
            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
            input.merchantId = merchantId;
            input.saleUserId = SlashHelper.userManager().getUserId();
            input.reservationTime = ordertime + ":00";
            input.num = orderpeople;
            input.note = note;
            input.userId = customerId;
            input.reservationMoney =etDingjin.getText().toString() ;
            input.replayNote=customerVoice;
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
                            object.put("tasktype", "ORDER");
                            object.put("taskTitle", "[服务订单-修改] " + ordertime + "  " + shopname+"(商户)");
                            object.put("serviceId",  SlashHelper.userManager().getUserId());
                            object.put("reservationId", ((CommonResult) response).reservationId);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[服务订单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(customerId+ "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);
//                        startActivity(imKit.getChattingActivityIntent(customerId + ""));
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



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit, R.id.rl_ordershopname, R.id.rl_ordertime,R.id.play_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                if (noLogin(CreateBespeakNewActivity.this))
                    return;
                shopname = bespekShopname.getText().toString();
                ordertime = bespekTime.getText().toString();
                note = AppUtils.replaceBlank(etCustomerRemark.getText().toString());
                strdingjin = etDingjin.getText().toString();
                strremark = etCustomerRemark.getText().toString();
                if ("请选择商户".equals(shopname)) {
                    ToastUtil.showMessage("请选择商户");
                    return;
                }
                if ( StringUtils.isBlank(merchantId)) {
                    ToastUtil.showMessage("请选择商户");
                    return;
                }
                if (StringUtils.isEmpty(orderpeople)) {
                    ToastUtil.showMessage("请选择预约人数");
                    return;
                }

                if (StringUtils.isEmpty(ordertime) || "选择时间".equals(ordertime)) {
                    ToastUtil.showMessage("请选择预约时间");
                    return;
                }
                if (StringUtils.isEmpty(strremark)) {
                    ToastUtil.showMessage("请输入包厢或房间号");
                    return;
                }

                user_reservation_add();

                break;

            case R.id.rl_ordershopname:
                if (!isFromMerchant) {
                    Intent intent = new Intent(CreateBespeakNewActivity.this, ChooseReservationMerchantActivity.class);
                    intent.putExtra("userId", SlashHelper.userManager().getUserId());// 管家id
                    intent.putExtra("actionFrom", "CreateBespeakActivity");
                    startActivityForResult(intent, CHOOSEMERCHANT);
                }

                break;
            case R.id.rl_ordertime:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, bespekTime.getText().toString(), "预约时间必须大于当前时间", 1);
                dateTimePicKDialog.dateTimePicKDialog(bespekTime);
                break;
            case R.id.play_btn:

                if(!StringUtils.isBlank(customerVoice)){
                    startPlay();
                }

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

                String fileName = HttpOperateUtil.downLoadFile(customerVoice,
                        customerVoice.substring(customerVoice.lastIndexOf("/") + 1));

                Log.i("keanbin", "fileName = " + fileName);
                File file = new File(fileName);

                if (!file.exists()) {
//                    Toast.makeText(DoTaskVoiceActivity.this, "没有语音文件！", Toast.LENGTH_SHORT)
//                            .show();
                    return;
                }
                try{
                    mMediaPlayer = MediaPlayer.create(CreateBespeakNewActivity.this,
                            Uri.parse(fileName));
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.start();
                }catch (Exception e){
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSEMERCHANT && resultCode == RESULT_OK) {
            bespekShopname.setText(data.getStringExtra("shopName"));
            merchantId = data.getIntExtra("merchantId", 0)+"";
        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(CreateBespeakNewActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(CreateBespeakNewActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
