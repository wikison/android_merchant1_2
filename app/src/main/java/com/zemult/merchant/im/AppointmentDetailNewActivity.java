package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ShareAppointmentActivity;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.SendRewardActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.aip.mine.UserReservationInfoRequest;
import com.zemult.merchant.aip.reservation.UserReservationEditRequest;
import com.zemult.merchant.alipay.taskpay.TaskPayResultActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.PMNumView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/19.
 */
//预约详情
public class AppointmentDetailNewActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.shop_tv)
    TextView shopTv;
    @Bind(R.id.pernumber_tv)
    TextView pernumberTv;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_extra)
    TextView tvExtra;
    @Bind(R.id.tv_dingjin)
    TextView tvDingjin;
    @Bind(R.id.appresult_tv)
    TextView appresultTv;
    @Bind(R.id.invite_btn)
    Button inviteBtn;
    @Bind(R.id.ordersuccess_btn_rl)
    RelativeLayout ordersuccessBtnRl;
    @Bind(R.id.others_ll)
    LinearLayout othersLl;
    @Bind(R.id.appresultcommit_et)
    EditText appresultcommitEt;
    @Bind(R.id.firstline)
    View firstline;
    @Bind(R.id.rlcustomer)
    RelativeLayout rlcustomer;
    @Bind(R.id.et_dingjin)
    EditText etDingjin;
    @Bind(R.id.et_customerremark)
    EditText etCustomerremark;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;

    public static String INTENT_RESERVATIONID = "intent";
    public static String INTENT_TYPE = "type";
    @Bind(R.id.v1)
    View v1;
    @Bind(R.id.yuyueresult_rl)
    RelativeLayout yuyueresultRl;
    @Bind(R.id.yuyueresultcommit_rl)
    RelativeLayout yuyueresultcommitRl;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.lookorder_btn)
    Button lookorderBtn;
    @Bind(R.id.jiezhang_btn)
    Button jiezhangBtn;
    @Bind(R.id.btn_modify)
    Button btnModify;
    @Bind(R.id.dinghaole_tv)
    TextView dinghaoleTv;
    String reservationId = "";
    int type;
    String replayNote;
    int userPayId, merchantReviewstatus;
    M_Reservation mReservation;
    UserReservationInfoRequest userReservationInfoRequest;
    UserReservationEditRequest userReservationEditRequest;
    public static String REFLASH_MYAPPOINT = "reflash_myappoint";
    @Bind(R.id.hongbao_tv)
    TextView hongbaoTv;
    int userId;
    String userName="";
    ImageManager mimageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_appointmentdetailnew);
    }

    @Override
    public void init() {
        lhTvTitle.setText("预约详情");
        reservationId = getIntent().getStringExtra(INTENT_RESERVATIONID);
        type = getIntent().getIntExtra(INTENT_TYPE, -1);
        EventBus.getDefault().register(this);
        hongbaoTv.setText(Html.fromHtml("<u>觉得服务不错给个赞赏吧</u>"));
        showPd();
        userReservationInfo();
        mimageManager = new ImageManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userReservationInfo();
    }

    private void userReservationInfo() {
        if (userReservationInfoRequest != null) {
            userReservationInfoRequest.cancel();
        }
        UserReservationInfoRequest.Input input = new UserReservationInfoRequest.Input();
        input.reservationId = reservationId;
        input.convertJosn();
        userReservationInfoRequest = new UserReservationInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((M_Reservation) response).status == 1) {
                    mReservation = (M_Reservation) response;
                    userPayId = mReservation.userPayId;
                    userId=mReservation.saleUserId;
                    userName=mReservation.saleUserName;

                    if (mReservation.saleUserId == SlashHelper.userManager().getUserId()) {
                        type = 1;
                    } else {
                        type = 0;
                    }

                    if (type == 1) {//客户
                        firstline.setVisibility(View.VISIBLE);
                        rlcustomer.setVisibility(View.VISIBLE);
                    } else if (type == 0) {//服务管家
                        firstline.setVisibility(View.GONE);
                        rlcustomer.setVisibility(View.GONE);
                    }

                    if (mReservation.state == 0) {
                        tvState.setText("待确认");
                        appresultTv.setText("暂无");
                        if (type == 0) {//我是客户的状态下
                            yuyueresultRl.setVisibility(View.GONE);
                        } else if (type == 1) {//我是管家的情况下
                            yuyueresultcommitRl.setVisibility(View.VISIBLE);
                        }
                    } else if (mReservation.state == 1) {

                        //状态(0:待确认,1:预约成功,2:已支付,3:预约结束)
                        tvState.setText("预约成功");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                        if (type == 0) {
                            dinghaoleTv.setVisibility(View.VISIBLE);
                            inviteBtn.setVisibility(View.VISIBLE);
                            jiezhangBtn.setVisibility(View.VISIBLE);
                            ordersuccessBtnRl.setVisibility(View.VISIBLE);
                        }
                        merchantReviewstatus = mReservation.merchantReviewstatus;
                        if (merchantReviewstatus == 2) {//商户审核状态(0未审核,1待审核,2审核通过)
                            jiezhangBtn.setVisibility(View.VISIBLE);
                        } else {
                            jiezhangBtn.setVisibility(View.GONE);
                        }

                    } else if (mReservation.state == 2) {
                        tvState.setText("已支付");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        v1.setVisibility(View.VISIBLE);
                        lookorderBtn.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                        //订单号
                    } else if (mReservation.state == 3||mReservation.state == 4) {
                        tvState.setText("已结束");
                        yuyueresultRl.setVisibility(View.GONE);
                        appresultTv.setText(mReservation.replayNote);
                    }

                    shopTv.setText(mReservation.merchantName);
                    pernumberTv.setText(mReservation.num + "人");
                    tvTime.setText(mReservation.reservationTime);
                    tvExtra.setText(mReservation.note);
                    tvDingjin.setText(mReservation.userPhone);

                    if (type == 0) {
                        //服务管家的头像和姓名
                        if (!TextUtils.isEmpty(mReservation.saleUserHead)) {
                            mimageManager.loadCircleImage(mReservation.saleUserHead, headIv);
                        }
                        nameTv.setText(mReservation.saleUserName);
                    } else if (type == 1) {
                        //客户的头像和姓名
                        if (!TextUtils.isEmpty(mReservation.head)) {
                            mimageManager.loadCircleImage(mReservation.head, headIv);
                        }
                        nameTv.setText(mReservation.name);
                    }
                } else {
                    ToastUtils.show(AppointmentDetailNewActivity.this, ((M_Reservation) response).info);
                }

            }
        });
        sendJsonRequest(userReservationInfoRequest);
    }


    //约客修改预约单(答复)
    private void user_reservation_edit() {

        try {
            if (userReservationEditRequest != null) {
                userReservationEditRequest.cancel();
            }
            UserReservationEditRequest.Input input = new UserReservationEditRequest.Input();
            input.reservationId = reservationId;
            input.replayNote = replayNote;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userReservationEditRequest = new UserReservationEditRequest(input, new ResponseListener() {
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
                            object.put("taskTitle", "[预约-已确认] 预约时间:" + mReservation.reservationTime + "预约地址:" + mReservation.merchantName);
                            object.put("serviceId", mReservation.saleUserId + "");
                            object.put("reservationId", reservationId + "");
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(mReservation.userId + "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);
//                        startActivity(imKit.getChattingActivityIntent(mReservation.saleUserId+""));
                        finish();

                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userReservationEditRequest);
        } catch (Exception e) {
        }
    }


    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(AppointmentDetailNewActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(AppointmentDetailNewActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.head_iv, R.id.lookorder_btn, R.id.invite_btn, R.id.jiezhang_btn, R.id.btn_cancel,R.id.btn_modify,R.id.hongbao_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                EventBus.getDefault().post(REFLASH_MYAPPOINT);
                onBackPressed();
                break;
            case R.id.head_iv:
                if (type == 0) {
                    //服务管家的头像和姓名
                    IntentUtil.intStart_activity(this, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, mReservation.saleUserId));
                } else if (type == 1) {
                    //客户的头像和姓名
                    IntentUtil.intStart_activity(this, UserDetailActivity.class, new Pair<String, Integer>(UserDetailActivity.USER_ID, mReservation.userId));
                }
                break;
            case R.id.lookorder_btn:
                //查看订单详情
                IntentUtil.intStart_activity(this,
                        PayInfoActivity.class, new Pair<String, Integer>("userPayId", userPayId));
                break;
            case R.id.invite_btn:
                //邀请好友
                Intent urlintent = new Intent(this, ShareAppointmentActivity.class);
                urlintent.putExtra("shareurl", Urls.BASIC_URL.replace("inter_json", "app") + "share_reservation_info.do?reservationId=" + reservationId);
                urlintent.putExtra("sharetitle", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约");
                urlintent.putExtra("sharecontent", "您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                        "，诚挚邀请，期待您的赴约。");
                startActivity(urlintent);


                break;
            case R.id.jiezhang_btn:
                //快速结账
                Intent intent = new Intent(this, FindPayActivity.class);
                intent.putExtra(FindPayActivity.M_RESERVATION, mReservation);
                intent.putExtra("merchantId", Integer.valueOf(mReservation.merchantId));
                intent.putExtra("userSaleId", Integer.valueOf(mReservation.saleUserId));
                if (!TextUtils.isEmpty(reservationId))
                    intent.putExtra("reservationId", Integer.valueOf(reservationId));

                startActivity(intent);

                break;
            case R.id.btn_cancel:

                break;
            case R.id.btn_modify:
                if(btnModify.getText().toString().equals("修改")){
                    etDingjin.setVisibility(View.VISIBLE);
                    etCustomerremark.setVisibility(View.VISIBLE);
                    pmnvSelectDeadline.setVisibility(View.VISIBLE);
                    Drawable dra= getResources().getDrawable(R.mipmap.right_btn);
                    dra.setBounds( 0, 0, dra.getMinimumWidth(),dra.getMinimumHeight());
                    tvTime.setCompoundDrawables(null, null, dra, null);

                    pernumberTv.setVisibility(View.GONE);
                    tvExtra.setVisibility(View.GONE);
                    tvDingjin.setVisibility(View.GONE);
                }
                if(btnModify.getText().toString().equals("保存")){
                    etDingjin.setVisibility(View.GONE);
                    etCustomerremark.setVisibility(View.GONE);
                    pmnvSelectDeadline.setVisibility(View.GONE);
                    tvTime.setCompoundDrawables(null, null, null, null);

                    pernumberTv.setVisibility(View.VISIBLE);
                    tvExtra.setVisibility(View.VISIBLE);
                    tvDingjin.setVisibility(View.VISIBLE);
                }


                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        if (TaskPayResultActivity.APPOINT_REFLASH.equals(s))
            userReservationInfo();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

}
