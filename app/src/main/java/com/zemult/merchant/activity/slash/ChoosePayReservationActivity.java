package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.AppointmentDetailActivity;
import com.zemult.merchant.adapter.slash.ChooseReservationAdapter;
import com.zemult.merchant.aip.reservation.UserReservationSaleListRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/1/22.
 */

public class ChoosePayReservationActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_merchant)
    TextView tvMerchant;
    @Bind(R.id.flv)
    FixedListView flv;
    @Bind(R.id.bsv_container)
    BounceScrollView bsvContainer;
    @Bind(R.id.btn_next)
    Button btnNext;

    private int saleUserId = 0;
    private int merchantId = 0;
    private String merchantName = "";
    private M_Merchant merchant;
    private M_Userinfo userinfo;
    public static final String MERCHANT_NAME = "merchant_name";

    UserInfoRequest userInfoRequest;
    UserReservationSaleListRequest userReservationSaleListRequest;
    List<M_Reservation> reservationList = new ArrayList<>();
    ChooseReservationAdapter adapter;

    String reservationIds = "";

    private Context mContext;
    private Activity mActivity;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_reservation);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getNetworkData();
    }

    private void initData() {
        mContext = this;
        mActivity = this;

        saleUserId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        merchantId = getIntent().getIntExtra(FindPayActivity.MERCHANT_ID, -1);
        merchantName = getIntent().getStringExtra(MERCHANT_NAME);

        adapter = new ChooseReservationAdapter(mContext, reservationList, false);
        flv.setAdapter(adapter);
    }

    private void initView() {
        lhTvTitle.setText("关联预约单");
        tvMerchant.setText("消费商户: " + merchantName);

    }

    private void initListener() {
        adapter.setOnAllClickListener(new ChooseReservationAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                Intent intent = new Intent(mContext, AppointmentDetailActivity.class);
                intent.putExtra(AppointmentDetailActivity.INTENT_TYPE, 0);
                intent.putExtra(AppointmentDetailActivity.INTENT_RESERVATIONID, adapter.getItem(position).reservationId + "");
                startActivity(intent);
            }
        });
    }

    private void getNetworkData() {
        getUserInfo(saleUserId);
        getReservationSaleList();

    }

    private void getUserInfo(int userSaleId) {
        if (userInfoRequest != null) {
            userInfoRequest.cancel();
        }
        UserInfoRequest.Input input = new UserInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userSaleId;
        input.convertJosn();
        userInfoRequest = new UserInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    userinfo = ((APIM_UserLogin) response).UserInfo;
                    tvName.setText("服务管家: " + userinfo.getName());
                    imageManager.loadCircleImage(userinfo.getHead(), ivHead);
                } else {
                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userInfoRequest);
    }

    private void getReservationSaleList() {
        if (userReservationSaleListRequest != null) {
            userReservationSaleListRequest.cancel();
        }
        UserReservationSaleListRequest.Input input = new UserReservationSaleListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.saleUserId = saleUserId;
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = Constants.ROWS;
        input.convertJosn();
        userReservationSaleListRequest = new UserReservationSaleListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserReservationList) response).status == 1) {
                    reservationList = ((APIM_UserReservationList) response).reservationList;

                    fillAdapter(reservationList, false);
                } else {
                    ToastUtil.showMessage(((APIM_UserReservationList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userReservationSaleListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Reservation> list, boolean isLoadMore) {
        if (list == null || list.size() == 0) {

        } else {
            adapter.setData(list, isLoadMore);
        }
        bsvContainer.post(new Runnable() {
            @Override
            public void run() {

                bsvContainer.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_next})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_next:
                reservationIds = "";
                for (int i = 0; i < flv.getChildCount(); i++) {
                    LinearLayout ll = (LinearLayout) flv.getChildAt(i);// 获得子级
                    CheckBox cb = (CheckBox) ll.findViewById(R.id.cb);// 从子级中获得控件
                    if (cb.isChecked()) {
                        reservationIds = reservationIds + adapter.getItem(i).reservationId + ",";
                    }
                }
                if (reservationIds.length() > 0) {
                    reservationIds = reservationIds.substring(0, reservationIds.length() - 1);
                }
                intent = new Intent(this, FindPayActivity.class);
                intent.putExtra("userSaleId", saleUserId);
                intent.putExtra("merchantId", merchantId);
                intent.putExtra("reservationIds", reservationIds);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
        }
    }

}
