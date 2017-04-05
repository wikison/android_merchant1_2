package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserPayDelRequest;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ServiceHistoryDetailActivity extends BaseActivity {

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
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.iv_user_head)
    ImageView ivUserHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.iv_merchant_head)
    ImageView ivMerchantHead;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.tv_sale_money)
    TextView tvSaleMoney;
    @Bind(R.id.ratingbar)
    RatingBar ratingbar;
    @Bind(R.id.tv_pay_num)
    TextView tvPayNum;
    @Bind(R.id.tv_trade_time)
    TextView tvTradeTime;
    @Bind(R.id.tv_commission_right)
    TextView tvCommissionRight;
    @Bind(R.id.rl_my_service)
    RelativeLayout rlMyService;
    @Bind(R.id.comment_ll)
    LinearLayout commentLl;
    private Context mContext;
    private Activity mActivity;
    UserPayInfoRequest userPayInfoRequest;
    UserPayDelRequest userPayDelRequest;
    M_Bill m;
    int userPayId;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_service_detail);
    }

    @Override
    public void init() {
        initData();

    }

    private void initData() {
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        userPayId = getIntent().getIntExtra("userPayId", 0);
        lhTvTitle.setText("记录详情");
        if (userPayId > 0)
            user_pay_info();
    }


    //订单详情
    private void user_pay_info() {
        showPd();
        if (userPayInfoRequest != null) {
            userPayInfoRequest.cancel();
        }


        UserPayInfoRequest.Input input = new UserPayInfoRequest.Input();
        input.userPayId = userPayId;

        input.convertJosn();
        userPayInfoRequest = new UserPayInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m = ((APIM_UserBillInfo) response).userPayInfo;
                    if (!TextUtils.isEmpty(m.userHead)) {
                        mImageManager.loadCircleImage(m.userHead, ivUserHead);
                    }
                    tvMoney.setText(Convert.getMoneyString(m.payMoney));
                    tvUserName.setText(m.userName);
                    tvMerchantName.setText(m.merchantName);
                    tvSaleMoney.setText("" + (m.allMoney == 0 ? "0.00" : Convert.getMoneyString(m.allMoney)));
                    tvPayNum.setText(m.number);
                    tvTradeTime.setText(m.createtime);
                    tvCommissionRight.setText(Convert.getMoneyString(m.saleUserMoney));
                    if (m.payMoney >= 50) {
                        commentLl.setVisibility(View.VISIBLE);
                        if (m.isComment == 1) {
                            ratingbar.setStar(m.comment);
                        } else {
                            ratingbar.setStar(0);
                        }
                    } else {
                        commentLl.setVisibility(View.GONE);

                    }

                } else {
                    ToastUtils.show(ServiceHistoryDetailActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
