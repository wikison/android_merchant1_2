package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserReservationInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Reservation;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    public static String INTENT_RESERVATIONID = "intent";
    public static String INTENT_TYPE = "type";
    @Bind(R.id.v1)
    View v1;
    @Bind(R.id.yuyueresult_rl)
    RelativeLayout yuyueresultRl;
    @Bind(R.id.ordernum_rl)
    RelativeLayout ordernumRl;
    @Bind(R.id.lookorder_btn)
    Button lookorderBtn;
    @Bind(R.id.dinghaole_tv)
    TextView dinghaoleTv;
    int reservationId;
    int type;
    M_Reservation mReservation;
    UserReservationInfoRequest userReservationInfoRequest;

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
                    if (mReservation.state == 1) {

                        //状态(1:预约成功,2:已支付,3:预约结束)
                        tvState.setText("预约成功");
                        yuyueresultRl.setVisibility(View.VISIBLE);
                        appresultTv.setText(mReservation.replayNote);
                        if (type == 0) {
                            dinghaoleTv.setVisibility(View.VISIBLE);
                            inviteBtn.setVisibility(View.VISIBLE);
                            jiezhangBtn.setVisibility(View.VISIBLE);
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


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.head_iv, R.id.lookorder_btn, R.id.invite_btn, R.id.jiezhang_btn})
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
        }
    }

}
