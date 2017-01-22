package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.PresentExchangeAdapter;
import com.zemult.merchant.aip.mine.UserBillInfoBandRequest;
import com.zemult.merchant.aip.mine.UserBillInfoCommissionRequest;
import com.zemult.merchant.aip.mine.UserBillInfoPayRequest;
import com.zemult.merchant.aip.mine.UserBillInfoPresentExchangeRequest;
import com.zemult.merchant.aip.mine.UserBillInfoPresentRequest;
import com.zemult.merchant.aip.mine.UserBillInfoWithdrawRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class BillInfoActivity extends BaseActivity {

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
    @Bind(R.id.tv_bill_name)
    TextView tvBillName;
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
    @Bind(R.id.ll_pay_type)
    LinearLayout llPayType;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.tv_pay_band_num)
    TextView tvPayBandNum;
    @Bind(R.id.tv_pay_type_band)
    TextView tvPayTypeBand;
    @Bind(R.id.tv_pay_time_band)
    TextView tvPayTimeBand;
    @Bind(R.id.ll_band)
    LinearLayout llBand;
    @Bind(R.id.tv_real_money)
    TextView tvRealMoney;
    @Bind(R.id.tv_service_money)
    TextView tvServiceMoney;
    @Bind(R.id.tv_money_withdraw)
    TextView tvMoneyWithdraw;
    @Bind(R.id.tv_bank_withdraw)
    TextView tvBankWithdraw;
    @Bind(R.id.tv_num_withdraw)
    TextView tvNumWithdraw;
    @Bind(R.id.tv_create_time_withdraw)
    TextView tvCreateTimeWithdraw;
    @Bind(R.id.tv_text_time_withdraw)
    TextView tvTextTimeWithdraw;
    @Bind(R.id.tv_time_withdraw)
    TextView tvTimeWithdraw;
    @Bind(R.id.ll_withdraw)
    LinearLayout llWithdraw;
    @Bind(R.id.iv_merchant_head_commission)
    ImageView ivMerchantHeadCommission;
    @Bind(R.id.tv_merchant_name_commission)
    TextView tvMerchantNameCommission;
    @Bind(R.id.iv_user_head)
    ImageView ivUserHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_sale_money_commission)
    TextView tvSaleMoneyCommission;
    @Bind(R.id.tv_pay_num_commission)
    TextView tvPayNumCommission;
    @Bind(R.id.tv_trade_time_commission)
    TextView tvTradeTimeCommission;
    @Bind(R.id.ll_commission)
    LinearLayout llCommission;
    @Bind(R.id.iv_user_head_present)
    ImageView ivUserHeadPresent;
    @Bind(R.id.tv_user_name_present)
    TextView tvUserNamePresent;
    @Bind(R.id.tv_trade_time_present)
    TextView tvTradeTimePresent;
    @Bind(R.id.tv_pay_num_present)
    TextView tvPayNumPresent;
    @Bind(R.id.tv_persent_name)
    TextView tvPersentName;
    @Bind(R.id.tv_persent_price)
    TextView tvPersentPrice;
    @Bind(R.id.ll_present)
    LinearLayout llPresent;
    @Bind(R.id.tv_trade_time_present_exchange)
    TextView tvTradeTimePresentExchange;
    @Bind(R.id.tv_pay_num_present_exchange)
    TextView tvPayNumPresentExchange;
    @Bind(R.id.lv)
    ListView lv;
    @Bind(R.id.ll_present_exchange)
    LinearLayout llPresentExchange;

    UserBillInfoPayRequest userBillInfoPayRequest;
    UserBillInfoBandRequest userBillInfoBandRequest;
    UserBillInfoWithdrawRequest userBillInfoWithdrawRequest;
    UserBillInfoCommissionRequest userBillInfoCommissionRequest;
    int billId, type;
    M_Bill m_bill;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bill_info);
    }

    @Override
    public void init() {

        initData();

    }

    private void initData() {
        billId = getIntent().getIntExtra("billId", 0);
        type = getIntent().getIntExtra("type", -1);
        lhTvTitle.setText("消费单详情");
        //类型(0:支付买单,2:支付绑定支付宝账户,3:取现,6:佣金)
        if (type == 0) {
            tvBillName.setText("交易");
            tvState.setText("支付成功");
            llPay.setVisibility(View.VISIBLE);
            user_bill_info_pay();
        }

        if (type == 2) {
            tvBillName.setText("绑定费用");
            tvState.setText("支付成功");
            llBand.setVisibility(View.VISIBLE);
            user_bill_info_band();
        }
        if (type == 3) {
            tvBillName.setText("提现");
            llWithdraw.setVisibility(View.VISIBLE);
            user_bill_info_withdraw();
        }

        if (type == 6) {
            tvBillName.setText("红包");
            tvState.setText("");
            llCommission.setVisibility(View.VISIBLE);
            user_bill_info_commission();
        }
        if (type == 7) {
            tvBillName.setText("礼物消费");
            tvState.setText("");
            llPresent.setVisibility(View.VISIBLE);
            user_bill_info_present();
        }
        if (type == 8) {
            tvBillName.setText("礼物兑换金额");
            tvState.setText("");
            llPresentExchange.setVisibility(View.VISIBLE);
            user_bill_info_present_exchange();
        }
    }

    //type=0
    private void user_bill_info_pay() {
        showPd();
        if (userBillInfoPayRequest != null) {
            userBillInfoPayRequest.cancel();
        }


        UserBillInfoPayRequest.Input input = new UserBillInfoPayRequest.Input();
        input.billId = billId;

        input.convertJosn();
        userBillInfoPayRequest = new UserBillInfoPayRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.payMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.payMoney)));
                    } else {
                        tvMoney.setText("-" + (m_bill.payMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.payMoney)));
                    }
                    if (!TextUtils.isEmpty(m_bill.merchantHead)) {
                        imageManager.loadCircleImage(m_bill.merchantHead, ivMerchantHead);
                    }
                    tvMerchantName.setText(m_bill.merchantName);
                    if (!TextUtils.isEmpty(m_bill.saleUserHead)) {
                        imageManager.loadCircleImage(m_bill.saleUserHead, ivSaleHead);
                    }
                    tvPayType.setText(m_bill.moneyType == 0 ? "账户余额" : "支付宝");
                    tvSaleName.setText(m_bill.saleUserName);
                    tvSaleMoney.setText("" + (m_bill.allMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.allMoney)));
                    tvPayMoney.setText("" + (m_bill.payMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.payMoney)));
                    tvPayNum.setText(m_bill.number);
                    tvTradeTime.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userBillInfoPayRequest);
    }

    //type=2
    private void user_bill_info_band() {
        showPd();
        if (userBillInfoBandRequest != null) {
            userBillInfoBandRequest.cancel();
        }


        UserBillInfoBandRequest.Input input = new UserBillInfoBandRequest.Input();
        input.billId = billId;

        input.convertJosn();
        userBillInfoBandRequest = new UserBillInfoBandRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.money == 0 ? "0.00" : Convert.getMoneyString(m_bill.money)));
                    } else {
                        tvMoney.setText("-" + (m_bill.money == 0 ? "0.00" : Convert.getMoneyString(m_bill.money)));
                    }
                    tvPayTypeBand.setText(m_bill.bankCard);
                    tvPayBandNum.setText(m_bill.number);
                    tvPayTimeBand.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userBillInfoBandRequest);
    }

    //type=3
    private void user_bill_info_withdraw() {
        showPd();
        if (userBillInfoWithdrawRequest != null) {
            userBillInfoWithdrawRequest.cancel();
        }


        UserBillInfoWithdrawRequest.Input input = new UserBillInfoWithdrawRequest.Input();
        input.billId = billId;

        input.convertJosn();
        userBillInfoWithdrawRequest = new UserBillInfoWithdrawRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.money == 0 ? "0.00" : Convert.getMoneyString(m_bill.money)));
                    } else {
                        tvMoney.setText("-" + (m_bill.money == 0 ? "0.00" : Convert.getMoneyString(m_bill.money)));
                    }
                    tvRealMoney.setText("" + (m_bill.realMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.realMoney)));
                    tvServiceMoney.setText("" + (m_bill.serviceMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.serviceMoney)));
                    tvMoneyWithdraw.setText("" + (m_bill.money == 0 ? "0.00" : Convert.getMoneyString(m_bill.money)));
                    tvBankWithdraw.setText(m_bill.bankCard);
                    if (m_bill.withdrawState == 0) {
                        tvTextTimeWithdraw.setText("预计到账时间");
                        tvState.setText("提现中");
                        tvTimeWithdraw.setText(m_bill.expectTime);
                    } else if (m_bill.withdrawState == 1) {
                        tvTextTimeWithdraw.setText("提现到账时间");
                        tvState.setText("提现成功");
                        tvTimeWithdraw.setText(m_bill.completeTime);
                    }
                    tvNumWithdraw.setText(m_bill.number);
                    tvCreateTimeWithdraw.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userBillInfoWithdrawRequest);
    }

    //type=6
    private void user_bill_info_commission() {
        showPd();
        if (userBillInfoCommissionRequest != null) {
            userBillInfoCommissionRequest.cancel();
        }

        UserBillInfoCommissionRequest.Input input = new UserBillInfoCommissionRequest.Input();
        input.billId = billId;

        input.convertJosn();
        userBillInfoCommissionRequest = new UserBillInfoCommissionRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.commissionMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.commissionMoney)));
                    } else {
                        tvMoney.setText("-" + (m_bill.commissionMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.commissionMoney)));
                    }
                    if (!TextUtils.isEmpty(m_bill.merchantHead)) {
                        imageManager.loadCircleImage(m_bill.merchantHead, ivMerchantHeadCommission);
                    }
                    tvMerchantNameCommission.setText(m_bill.merchantName);
                    if (!TextUtils.isEmpty(m_bill.userHead)) {
                        imageManager.loadCircleImage(m_bill.userHead, ivUserHead);
                    }
                    tvUserName.setText(m_bill.userName);
                    tvSaleMoneyCommission.setText("" + (m_bill.allMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.allMoney)));
                    tvPayNumCommission.setText(m_bill.number);
                    tvTradeTimeCommission.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userBillInfoCommissionRequest);
    }

    //type=7
    private UserBillInfoPresentRequest presentRequest;

    private void user_bill_info_present() {
        showPd();
        if (presentRequest != null) {
            presentRequest.cancel();
        }

        UserBillInfoPresentRequest.Input input = new UserBillInfoPresentRequest.Input();
        input.billId = billId;

        input.convertJosn();
        presentRequest = new UserBillInfoPresentRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.payMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.payMoney)));
                    } else {
                        tvMoney.setText("-" + (m_bill.payMoney == 0 ? "0.00" : Convert.getMoneyString(m_bill.payMoney)));
                    }
                    if (!TextUtils.isEmpty(m_bill.toUserHead)) {
                        imageManager.loadCircleImage(m_bill.toUserHead, ivUserHeadPresent);
                    }
                    tvUserNamePresent.setText(m_bill.toUserName);

                    tvPersentPrice.setText(m_bill.payMoney + "");
                    tvPersentName.setText(m_bill.presentName + "x1");
                    tvPayNumPresent.setText(m_bill.number);
                    tvTradeTimePresent.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(presentRequest);
    }

    //type=8
    private UserBillInfoPresentExchangeRequest exchangeRequest;

    private void user_bill_info_present_exchange() {
        showPd();
        if (exchangeRequest != null) {
            exchangeRequest.cancel();
        }

        UserBillInfoPresentExchangeRequest.Input input = new UserBillInfoPresentExchangeRequest.Input();
        input.billId = billId;

        input.convertJosn();
        exchangeRequest = new UserBillInfoPresentExchangeRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m_bill = ((APIM_UserBillInfo) response).billInfo;
                    if (m_bill.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + (m_bill.allPrice == 0 ? "0.00" : Convert.getMoneyString(m_bill.allPrice)));
                    } else {
                        tvMoney.setText("-" + (m_bill.allPrice == 0 ? "0.00" : Convert.getMoneyString(m_bill.allPrice)));
                    }

                    if(m_bill.presentList != null && !m_bill.presentList.isEmpty()){
                        lv.setAdapter(new PresentExchangeAdapter(BillInfoActivity.this, m_bill.presentList));
                    }
                    tvPayNumPresentExchange.setText(m_bill.number);
                    tvTradeTimePresentExchange.setText(m_bill.createtime);

                } else {
                    ToastUtils.show(BillInfoActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(exchangeRequest);
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
