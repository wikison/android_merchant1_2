package com.zemult.merchant.im;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.flyco.roundview.RoundTextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MyFansActivity;
import com.zemult.merchant.activity.slash.ChooseReservationMerchantActivity;
import com.zemult.merchant.activity.slash.ServicePlanActivity;
import com.zemult.merchant.aip.mine.User2RemindIMInfoRequest;
import com.zemult.merchant.aip.mine.UserInfoOwnerRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.aip.slash.Merchant2SaveResOrderTmp;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.PMNumView;
import com.zemult.merchant.view.SharePopwindow;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
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
    @Bind(R.id.bespek_plan)
    TextView bespekPlan;
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
    @Bind(R.id.rl_plan)
    RelativeLayout rlPlan;
    @Bind(R.id.activity_bespeak)
    LinearLayout llRoot;
    @Bind(R.id.tv_dingjin_tpis)
    TextView tvDingjinTpis;

    @Bind(R.id.rl_orderuser)
    RelativeLayout rlOrderuser;
    @Bind(R.id.view_line1)
    View viewLine1;

    User2RemindIMInfoRequest user2RemindIMInfoRequest;

    UserInfoOwnerRequest userInfoOwnerRequest;
    UserReservationAddRequest userReservationAddRequest;
    int customerId;
    String shopname = "", ordertime = "",strdingjin="",strremark="", orderpeople, note,customerName,customerHead;
    String merchantId,reviewstatus;
    int CHOOSEMERCHANT = 100,planId,CHOOSEPLAN=101,remindIMId;
    boolean isFromMerchant;
    SharePopwindow popwindow;
    Merchant2SaveResOrderTmp merchant2SaveResOrderTmp;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeaknew);

    }


    @Override
    public void init() {
        registerReceiver(new String[]{Constants.BROCAST_DISABLE_PLAN});
        customerId = getIntent().getIntExtra("customerId", 0);

        remindIMId= getIntent().getIntExtra("remindIMId", 0);
        merchantId= getIntent().getStringExtra("merchantId");
        reviewstatus= getIntent().getStringExtra("reviewstatus");
        shopname= getIntent().getStringExtra("merchantName");
        if(0!=remindIMId){
            playBtn.setVisibility(View.VISIBLE);
            user2RemindIMInfoRequest();
        }
        else{
            playBtn.setVisibility(View.GONE                         );
        }

        if(customerId==0){
            rlOrderuser.setVisibility(View.GONE);
            viewLine1.setVisibility(View.GONE);
        }
        else{
            get_user_info_owner_request();
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

        pmnvSelectDeadline.setMinNum(0);
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
        lhTvTitle.setText("生成服务订单");


        popwindow = new SharePopwindow(CreateBespeakNewActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case SharePopwindow.WECHAT:
                        popwindow.dismiss();
                        merchant2_saveResOrderTmp();
                        break;
                    case SharePopwindow.LXR:



                        break;
                    case SharePopwindow.YUEFU:
                        popwindow.dismiss();
                        Intent    intent = new Intent(CreateBespeakNewActivity.this, MyFansActivity.class);
                        intent.putExtra(MyFansActivity.INTENT_USERID, SlashHelper.userManager().getUserId());
                        intent.putExtra("merchantId",merchantId);
                        intent.putExtra("ordertime",ordertime);
                        intent.putExtra("reservationMoney",etDingjin.getText().toString());
                        intent.putExtra("shopname",shopname);
                        intent.putExtra("note",note);
                        intent.putExtra("orderpeople",orderpeople);
                        intent.putExtra("planId",planId);
                        intent.putExtra("fromAct","CreateBespeakNewActivity");
                        startActivity(intent);

                        break;
                }
            }
        });


    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_DISABLE_PLAN.equals(intent.getAction())) {
            if(intent.getIntExtra("planId",0)==planId&&0==intent.getIntExtra("state",0)){
                planId=0;
                bespekPlan.setText("选择服务方案");
            }


        }
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
                        M_Reservation m_reservation= ((M_Reservation) response);
                        bespekTime.setText(StringUtils.isBlank(m_reservation.reservationTime)?"选择时间":m_reservation.reservationTime);
                        int ordernum=m_reservation.num;
                        if(ordernum!=0){
                            pmnvSelectDeadline.setDefaultNum(ordernum);
                            pmnvSelectDeadline.setText("" + ordernum);
                            orderpeople = "" +ordernum;
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

    private void user_reservation_add() {

        try {
            if (userReservationAddRequest != null) {
                userReservationAddRequest.cancel();
            }
            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
            input.merchantId = merchantId;
            input.saleUserId = SlashHelper.userManager().getUserId();
            if(ordertime.length()<17){
                input.reservationTime= ordertime+ ":00";
            }
            else{
                input.reservationTime= ordertime;
            }
            input.num = orderpeople;
            input.note = note;
            input.userId = customerId;
            input.reservationMoney =etDingjin.getText().toString() ;
            input.remindIMId=remindIMId;
            input.planId=planId;
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
                            object.put("taskTitle", "[服务订单] " + ordertime + "  " + shopname+"(商户)");
                            object.put("serviceId",  SlashHelper.userManager().getUserId());
                            object.put("reservationId", ((CommonResult) response).reservationId);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[服务订单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWMessage message2 = YWMessageChannel.createTextMessage("您好，已经按照你的要求订好了，你看一下，没问题就确认一下，谢谢~");
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(customerId+ "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);

                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message2, forwardCallBack);



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


    private void merchant2_saveResOrderTmp() {

        try {
            if (merchant2SaveResOrderTmp != null) {
                merchant2SaveResOrderTmp.cancel();
            }
            Merchant2SaveResOrderTmp.Input input = new Merchant2SaveResOrderTmp.Input();
            input.planId = planId;
            input.merchantId = merchantId;
            input.saleUserId = SlashHelper.userManager().getUserId();
            if(ordertime.length()<17){
                input.reservationTime= ordertime+ ":00";
            }
            else{
                input.reservationTime= ordertime;
            }
            input.num = orderpeople;
            input.note = note;
            input.reservationMoney =etDingjin.getText().toString() ;
            input.planId=planId;
            input.convertJosn();

            merchant2SaveResOrderTmp = new Merchant2SaveResOrderTmp(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        UMImage shareImage = new UMImage(CreateBespeakNewActivity.this, R.mipmap.icon_share);
                        new ShareAction(CreateBespeakNewActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("[服务订单] " + ordertime + "  " + shopname+"(商户)")
                                .withTargetUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx22ea2af5e7d47cb1&redirect_uri=http://www.yovoll.com/dzyx/app/wxsharepay_index.do?preId="+((CommonResult) response).tmpid+"&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect")
                                .withMedia(shareImage).withTitle("服务订单")
                                .share();

                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(merchant2SaveResOrderTmp);
        } catch (Exception e) {
        }
    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit, R.id.rl_ordershopname, R.id.rl_ordertime,R.id.play_btn,R.id.rl_plan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_plan:
                //选择方案
                if(null!=merchantId){
                    Intent planintent = new Intent(CreateBespeakNewActivity.this,ServicePlanActivity.class);
                    planintent.putExtra("saleUserId",SlashHelper.userManager().getUserId());
                    planintent.putExtra("merchantId",Integer.parseInt(merchantId));
                    startActivityForResult(planintent, CHOOSEPLAN);
                }
                else {
                    ToastUtil.showMessage("请先选择商户");
                }

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

                if(customerId==0){
                    popwindow.showType(true,false,false,false,true,false);
                    if (popwindow.isShowing())
                        popwindow.dismiss();
                    else
                        popwindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置

                }
                else{
                    user_reservation_add();
                }


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
                if(remindIMId!=0){
                    Intent intent =new Intent(CreateBespeakNewActivity.this, CustomerCreateBespeakDetailsActivity.class);
                    intent.putExtra("remindIMId",remindIMId);
                    intent.putExtra("userId",SlashHelper.userManager().getUserId());
                    startActivity(intent);
                }

                break;


        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSEMERCHANT && resultCode == RESULT_OK) {
            bespekShopname.setText(data.getStringExtra("shopName"));
            merchantId = data.getIntExtra("merchantId", 0)+"";
            reviewstatus= data.getIntExtra("reviewstatus", 0)+"";
            if(reviewstatus.equals("2")){
                rlCustomerphone.setVisibility(View.VISIBLE);
                tvDingjinTpis.setVisibility(View.VISIBLE);
            }
            else {
                rlCustomerphone.setVisibility(View.GONE);
                tvDingjinTpis.setVisibility(View.GONE);
            }


        }
        if (requestCode == CHOOSEPLAN && resultCode == RESULT_OK) {
            bespekPlan.setText(data.getStringExtra("planName"));
            planId = data.getIntExtra("planId", 0);
        }

    }


    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(CreateBespeakNewActivity.this, ShareText.shareMediaToCN(platform) + " 收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                user_reservation_add();
                Toast.makeText(CreateBespeakNewActivity.this, ShareText.shareMediaToCN(platform) + " 分享成功", Toast.LENGTH_SHORT).show();
            }
            popwindow.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(CreateBespeakNewActivity.this, ShareText.shareMediaToCN(platform) + " 分享失败", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(CreateBespeakNewActivity.this, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }
    };

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
