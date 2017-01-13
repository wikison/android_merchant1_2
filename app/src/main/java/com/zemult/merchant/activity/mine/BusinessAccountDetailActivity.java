package com.zemult.merchant.activity.mine;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantBillInfoPayRequest;
import com.zemult.merchant.aip.mine.MerchantBillInfoWithdrawRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/10/24.
 */

public class BusinessAccountDetailActivity extends BaseActivity {

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
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.tv11)
    TextView tv11;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.object_tv)
    TextView objectTv;
    @Bind(R.id.tv22)
    TextView tv22;
    @Bind(R.id.xiaofei_tv)
    TextView xiaofeiTv;
    @Bind(R.id.tv55)
    TextView tv55;
    @Bind(R.id.realpay_tv)
    TextView realpayTv;
    @Bind(R.id.tv1111)
    TextView tv1111;
    @Bind(R.id.managerhead_iv)
    ImageView managerheadIv;
    @Bind(R.id.manager_tv)
    TextView managerTv;
    @Bind(R.id.tv2222)
    TextView tv2222;
    @Bind(R.id.yongjin_tv)
    TextView yongjinTv;
    @Bind(R.id.yingxiao_ll)
    LinearLayout yingxiaoLl;
    @Bind(R.id.lastmoney_tv)
    TextView lastmoneyTv;
    @Bind(R.id.t_tv)
    TextView tTv;
    @Bind(R.id.jytime_tv)
    TextView jytimeTv;
    @Bind(R.id.ding_tv)
    TextView dingTv;
    @Bind(R.id.dingdan_tv)
    TextView dingdanTv;
    @Bind(R.id.jiaoyi_ll)
    LinearLayout jiaoyiLl;
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
    @Bind(R.id.tv_commission_money)
    TextView tvCommissionMoney;


    public static final String INTENT_TYPE = "type";
    public static final String INTENT_BILLID = "billid";
    MerchantBillInfoPayRequest merchantBillInfoPayRequest;
    MerchantBillInfoWithdrawRequest merchantBillInfoWithdrawRequest;
    int billId, type;

    @Override
    public void setContentView() {
        setContentView(R.layout.businessaccountdetail_activity);
    }

    @Override
    public void init() {
        lhTvTitle.setText("账单详情");
        billId = getIntent().getIntExtra(INTENT_BILLID, 0);
        type = getIntent().getIntExtra(INTENT_TYPE, -1);
        showPd();
        if (type == 0) {
            jiaoyiLl.setVisibility(View.VISIBLE);
            llWithdraw.setVisibility(View.GONE);
            merchantBillInfoPay();
        } else if (type == 1) {
            jiaoyiLl.setVisibility(View.GONE);
            llWithdraw.setVisibility(View.VISIBLE);
            merchantBillInfoWithdraw();
        }
    }

    private void merchantBillInfoPay() {

        if (merchantBillInfoPayRequest != null) {
            merchantBillInfoPayRequest.cancel();
        }
        MerchantBillInfoPayRequest.Input input = new MerchantBillInfoPayRequest.Input();
        input.billId = billId;//商户的账单id
        input.convertJosn();
        merchantBillInfoPayRequest = new MerchantBillInfoPayRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserBillInfo) response).status == 1) {
                    M_Bill m_bill = ((APIM_UserBillInfo) response).billInfo;
                    tvType.setText("交易");
                    imageManager.loadCircleImage(m_bill.userHead, headIv);
                    objectTv.setText(m_bill.userName);
                    xiaofeiTv.setText("" + Convert.getMoneyString(m_bill.payMoney));
                    realpayTv.setText("" + m_bill.payMoney);
                    lastmoneyTv.setText("" + m_bill.merchantMoney);
                    dingdanTv.setText(m_bill.number);
                    jytimeTv.setText(m_bill.createtime);

                    imageManager.loadCircleImage(m_bill.saleUserHead, managerheadIv);
                    managerTv.setText(m_bill.saleUserName);
                    yongjinTv.setText("" + m_bill.saleUserMoney);
                    tvCommissionMoney.setText("" + m_bill.getCommissionMoney());

                    if (((APIM_UserBillInfo) response).billInfo.inCome == 0) {//(0:收入,1:支出)
                        moneyTv.setText("+" + Convert.getMoneyString(m_bill.merchantMoney));
                    } else {
                        moneyTv.setText("-" + Convert.getMoneyString(m_bill.merchantMoney));
                    }
                }
            }
        });
        sendJsonRequest(merchantBillInfoPayRequest);
    }

    private void merchantBillInfoWithdraw() {

        if (merchantBillInfoWithdrawRequest != null) {
            merchantBillInfoWithdrawRequest.cancel();
        }
        MerchantBillInfoWithdrawRequest.Input input = new MerchantBillInfoWithdrawRequest.Input();
        input.billId = billId;//提现账单id
        input.convertJosn();
        merchantBillInfoWithdrawRequest = new MerchantBillInfoWithdrawRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserBillInfo) response).status == 1) {
                    M_Bill m_bill = ((APIM_UserBillInfo) response).billInfo;
                    tvType.setText("提现");
                    tvRealMoney.setText(Convert.getMoneyString(m_bill.money));
                    tvMoneyWithdraw.setText(Convert.getMoneyString(m_bill.money));
                    tvServiceMoney.setText("" + 0.00f);

                    tvBankWithdraw.setText(m_bill.moneyType == 0 ? "银行卡" : "支付宝");
                    moneyTv.setText((m_bill.inCome == 0 ? "+" : "-") + Convert.getMoneyString(m_bill.money));
                    tvNumWithdraw.setText(m_bill.number);
                    tvCreateTimeWithdraw.setText(m_bill.createtime);
                    tvTimeWithdraw.setText(m_bill.completeTime);
                }
            }
        });
        sendJsonRequest(merchantBillInfoWithdrawRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }


}