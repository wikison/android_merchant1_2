package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserPayDelRequest;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.alipay.taskpay.AssessmentActivity;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.common.CommonDialog;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class PayInfoActivity extends BaseActivity {

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
    @Bind(R.id.iv_merchant_head)
    ImageView ivMerchantHead;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.iv_sale_head)
    ImageView ivSaleHead;
    @Bind(R.id.tv_sale_name)
    TextView tvSaleName;
    @Bind(R.id.tv_sale_money)
    TextView tvSaleMoney;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_pay_num)
    TextView tvPayNum;
    @Bind(R.id.tv_trade_time)
    TextView tvTradeTime;
    @Bind(R.id.tv_pay_type)
    TextView tvPayType;
    @Bind(R.id.rtv_cancel)
    RoundTextView rtvCancel;
    @Bind(R.id.rtv_to_pay)
    RoundTextView rtvToPay;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.ll_pay_type)
    LinearLayout llPayType;

    private Context mContext;
    private Activity mActivity;
    UserPayInfoRequest userPayInfoRequest;
    UserPayDelRequest userPayDelRequest;
    M_Bill m;
    int userPayId;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pay_info);
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
        lhTvTitle.setText("订单详情");
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
                    //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))
                    tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                    switch (m.state) {
                        case 0:
                            tvState.setText("待付款");
                            llPayType.setVisibility(View.GONE);
                            llPay.setVisibility(View.VISIBLE);
                            rtvToPay.setText("立即付款");
                            break;
                        case 1:
                            tvState.setText("交易成功");
                            llPayType.setVisibility(View.VISIBLE);
                            if (m.isComment == 0 & m.payMoney >= 100) {
                                llPay.setVisibility(View.VISIBLE);
                                rtvToPay.setText("立即评价");
                            } else {
                                llPay.setVisibility(View.GONE);
                            }
                            break;
                        case 2:
                            tvState.setText("交易失效");
                            llPayType.setVisibility(View.GONE);
                            llPay.setVisibility(View.GONE);
                            break;
                        case 3:
                            tvState.setText("订单取消");
                            llPayType.setVisibility(View.GONE);
                            llPay.setVisibility(View.GONE);
                            break;
                    }
                    switch (m.moneyType) {
                        case 0:
                            tvPayType.setText("账户余额");
                            break;
                        case 1:
                            tvPayType.setText("支付宝");
                            break;
                    }
                    tvMerchantName.setText(m.merchantName);
                    if (!TextUtils.isEmpty(m.saleUserHead)) {
                        mImageManager.loadCircleImage(m.saleUserHead, ivSaleHead);
                    }
                    tvSaleName.setText(m.saleUserName);
                    tvSaleMoney.setText("" + (m.allMoney == 0 ? "0.00" : Convert.getMoneyString(m.allMoney)));
                    tvPayMoney.setText("" + (m.payMoney == 0 ? "0.00" : Convert.getMoneyString(m.payMoney)));
                    tvPayNum.setText(m.number);
                    tvTradeTime.setText(m.createtime);

                } else {
                    ToastUtils.show(PayInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rtv_cancel, R.id.rtv_to_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.rtv_cancel:
                CommonDialog.showDialogListener(mContext, null, "否", "是", "是否取消订单", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        cancelPay();
                    }
                });
                break;
            case R.id.rtv_to_pay:
                if (m.state == 0) {
                    goPay();
                } else if (m.state == 1) {
                    goAssessment();
                }
                break;
        }
    }

    private void cancelPay() {
        showPd();
        if (userPayDelRequest != null) {
            userPayDelRequest.cancel();
        }


        UserPayDelRequest.Input input = new UserPayDelRequest.Input();
        input.userPayId = userPayId;

        input.convertJosn();
        userPayDelRequest = new UserPayDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    Intent intent = new Intent(Constants.BROCAST_REFRESH_ORDER);
                    sendBroadcast(intent);
                    user_pay_info();
                } else {
                    ToastUtils.show(PayInfoActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayDelRequest);
    }

    private void goAssessment() {
        Intent intent = new Intent(mActivity, AssessmentActivity.class);
        intent.putExtra("userPayId", userPayId);
        intent.putExtra("managerhead", m.saleUserHead);
        intent.putExtra("managername", m.saleUserName);
        intent.putExtra("merchantName", m.merchantName);
        startActivityForResult(intent, 2000);
    }

    private void goPay() {
        Intent intent = new Intent(mActivity, ChoosePayTypeActivity.class);
        intent.putExtra("consumeMoney", m.payMoney);
        intent.putExtra("order_sn", m.number);
        intent.putExtra("userPayId", userPayId);
        intent.putExtra("merchantName", m.merchantName);
        intent.putExtra("merchantHead", m.merchantHead);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                setResult(RESULT_OK);
                onBackPressed();

            } else if (requestCode == 2000) {
                user_pay_info();
                Intent intent = new Intent(Constants.BROCAST_REFRESH_ORDER);
                intent.putExtra("userPayId", userPayId);
                setResult(RESULT_OK, intent);
                sendBroadcast(intent);
            }
        }
    }

}
