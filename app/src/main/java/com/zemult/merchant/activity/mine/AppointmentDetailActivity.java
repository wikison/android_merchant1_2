package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.zemult.merchant.aip.mine.UserReservationInfoRequest;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.aip.reservation.UserReservationEditRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.CreateBespeakActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/19.
 */
//预约详情
public class AppointmentDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.object_tv)
    TextView objectTv;
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
    @Bind(R.id.tv_contacter)
    TextView tvContacter;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.appresult_tv)
    TextView appresultTv;
    @Bind(R.id.ordernum_tv)
    TextView ordernumTv;

    @Bind(R.id.invite_btn)
    Button inviteBtn;
    @Bind(R.id.jiezhang_btn)
    Button jiezhangBtn;
    @Bind(R.id.ordersuccess_btn_rl)
    RelativeLayout ordersuccessBtnRl;
    @Bind(R.id.others_ll)
    LinearLayout othersLl;
    @Bind(R.id.appresultcommit_et)
    EditText appresultcommitEt;



    public static String INTENT_RESERVATIONID = "intent";
    public static String INTENT_TYPE = "type";
    @Bind(R.id.v1)
    View v1;
    @Bind(R.id.yuyueresult_rl)
    RelativeLayout yuyueresultRl;
    @Bind(R.id.ordernum_rl)
    RelativeLayout ordernumRl;
    @Bind(R.id.yuyueresultcommit_rl)
    RelativeLayout yuyueresultcommitRl;

    @Bind(R.id.lookorder_btn)
    Button lookorderBtn;
    @Bind(R.id.dinghaole_tv)
    TextView dinghaoleTv;
    int reservationId;
    int type;
    String replayNote;
    M_Reservation mReservation;
    UserReservationInfoRequest userReservationInfoRequest;
    UserReservationEditRequest userReservationEditRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_appointmentdetail);
    }

    @Override
    public void init() {
        lhTvTitle.setText("预约详情");
        reservationId = getIntent().getIntExtra(INTENT_RESERVATIONID, 0);
        type = getIntent().getIntExtra(INTENT_TYPE, -1);
        if (type == 1) {
            objectTv.setText("客户");
        } else if (type == 0) {
            objectTv.setText("服务管家");
        }
        showPd();
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
                    if (mReservation.state == 0) {
                        tvState.setText("待确认");
                        appresultTv.setText("暂无");
                        if(type==1){//客户
                            yuyueresultRl.setVisibility(View.VISIBLE);
                        }
                       else if(type==0){//管家
                            yuyueresultcommitRl.setVisibility(View.VISIBLE);
                        }
                    }

                   else if (mReservation.state == 1) {

                        //状态(0:待确认,1:预约成功,2:已支付,3:预约结束)
                        tvState.setText("预约成功");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                        if (type == 1) {
                            dinghaoleTv.setVisibility(View.VISIBLE);
                            inviteBtn.setVisibility(View.VISIBLE);
                            jiezhangBtn.setVisibility(View.VISIBLE);
                            ordersuccessBtnRl.setVisibility(View.VISIBLE);
                        }

                    } else if (mReservation.state == 2) {
                        tvState.setText("已支付");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        ordernumRl.setVisibility(View.VISIBLE);
                        v1.setVisibility(View.VISIBLE);
                        lookorderBtn.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                        //订单号
                        ordernumTv.setText(mReservation.userPayNumber);
                    } else if (mReservation.state == 3) {
                        tvState.setText("已结束");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                    }

                    shopTv.setText(mReservation.merchantName);
                    pernumberTv.setText(mReservation.num + "");
                    tvTime.setText(mReservation.reservationTime);
                    tvExtra.setText(mReservation.note);
                    tvContacter.setText(mReservation.userName);
                    tvPhone.setText(mReservation.userPhone);

                    if (type == 0) {
                        //服务管家的头像和姓名
                        imageManager.loadCircleImage(mReservation.saleUserHead, headIv);
                        nameTv.setText(mReservation.saleUserName);
                    } else if (type == 1) {
                        //客户的头像和姓名
                        imageManager.loadCircleImage(mReservation.head, headIv);
                        nameTv.setText(mReservation.name);
                    }
                } else {
                    ToastUtils.show(AppointmentDetailActivity.this, ((M_Reservation) response).info);
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
                    if (((CommonResult) response).status==1) {

                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                        JSONObject object = new JSONObject();
                        try {
                            object.put("customizeMessageType", "Task");
                            object.put("tasktype", "ORDER");
                            object.put("taskTitle", "[预约-已确认] 预约时间:"+mReservation.reservationTime+"预约地址:"+mReservation.merchantName);
                            object.put("serviceId", mReservation.saleUserId);
                            object.put("reservationId",reservationId);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit  imKit= LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(mReservation.userId+"", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        ,message,forwardCallBack);
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
            Notification.showToastMsg(AppointmentDetailActivity.this,"forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(AppointmentDetailActivity.this,"forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.head_iv, R.id.lookorder_btn, R.id.invite_btn, R.id.jiezhang_btn,R.id.btn_service})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.head_iv:
                break;
            case R.id.lookorder_btn:
                //查看订单详情

                break;
            case R.id.invite_btn:
                //邀请好友
                break;
            case R.id.jiezhang_btn:
                //快速结账
                break;
            case R.id.btn_service:
                replayNote=appresultcommitEt.getText().toString();
                if(StringUtils.isEmpty(replayNote)){
                  ToastUtil.showMessage("请输入反馈信息");
                    return;
                }
                user_reservation_edit();
                break;
        }
    }

}
