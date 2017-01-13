package com.zemult.merchant.alipay.taskpay;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class TaskPayResultActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_callphone)
    ImageView ivCallphone;
    @Bind(R.id.tv_orderno)
    TextView tvOrderno;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_paytime)
    TextView tvPaytime;
    @Bind(R.id.tv_zhifutip)
    TextView tvZhifutip;
    @Bind(R.id.tv_lab1)
    TextView tvLab1;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_sale_cover)
    ImageView ivSaleCover;
    @Bind(R.id.tv_sale_name)
    TextView tvSaleName;
    @Bind(R.id.rtv_communicate)
    RoundTextView rtvCommunicate;
    @Bind(R.id.btn_topinjia)
    Button btnTopinjia;
    @Bind(R.id.btn_toorder)
    Button btnToorder;

    UserPayInfoRequest userPayInfoRequest;
    MerchantInfoRequest merchantInfoRequest;
    int userPayId;
    M_Bill m_bill;
    String merchantTel;
    String payTime;
    int saleUserId = 0;
    String managerhead,managername,merchantName;
    double paymoney;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_task_pay_result);
    }

    @Override
    public void init() {
        lhTvTitle.setText("支付结果");
        payTime = getIntent().getStringExtra("payTime");
        userPayId = getIntent().getIntExtra("userPayId", 0);
        managerhead = getIntent().getStringExtra("managerhead");
        managername= getIntent().getStringExtra("managername");
        merchantName= getIntent().getStringExtra("merchantName");
        paymoney = getIntent().getDoubleExtra("paymoney", 0);
//        if (userPayId > 0)
//            user_pay_info();
        merchant_info(userPayId);

        if(paymoney<99){
            btnTopinjia.setVisibility(View.GONE);
        }
        else{
            btnTopinjia.setVisibility(View.VISIBLE);
        }

    }

    //订单详情
//    private void user_pay_info() {
//        showPd();
//        if (userPayInfoRequest != null) {
//            userPayInfoRequest.cancel();
//        }
//
//
//        UserPayInfoRequest.Input input = new UserPayInfoRequest.Input();
//        input.userPayId = userPayId;
//
//        input.convertJosn();
//        userPayInfoRequest = new UserPayInfoRequest(input, new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dismissPd();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                if (((APIM_UserBillInfo) response).status == 1) {
//                    m_bill = ((APIM_UserBillInfo) response).userPayInfo;
//                    tvOrderno.setText("订单号：" + m_bill.number);
//                    tvPayMoney.setText("交易金额：" + m_bill.payMoney);
//                    tvPaytime.setText("买单时间：" + payTime);
//                    saleUserId = m_bill.saleUserId;
//                    tvSaleName.setText(m_bill.saleUserName);
//                    imageManager.loadCircleImage(m_bill.saleUserHead, ivSaleCover);
//
//                    merchant_info(m_bill.merchantId);
//
//                } else {
//                    ToastUtils.show(TaskPayResultActivity.this, ((APIM_UserBillInfo) response).info);
//                }
//                dismissPd();
//            }
//        });
//        sendJsonRequest(userPayInfoRequest);
//    }
//
//    //商家详情
    private void merchant_info(int merchantId) {
        showPd();
        if (merchantInfoRequest != null) {
            merchantInfoRequest.cancel();
        }

        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.merchantId = merchantId;

        input.convertJosn();
        merchantInfoRequest = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    M_Merchant m = ((APIM_MerchantGetinfo) response).merchant;
                    tvZhifutip.setText(m.address);
                    tvLab1.setText(m.name);
                    merchantTel = m.tel;

                } else {
                    ToastUtils.show(TaskPayResultActivity.this, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantInfoRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_topinjia,R.id.btn_toorder, R.id.rtv_communicate, R.id.iv_callphone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_topinjia:
                Intent intent3 = new Intent(TaskPayResultActivity.this, AssessmentActivity.class);
                intent3.putExtra("userPayId",userPayId);
                intent3.putExtra("managerhead", managerhead);
                intent3.putExtra("managername", managername);
                intent3.putExtra("merchantName", merchantName);
                startActivity(intent3);
                onBackPressed();
                break;
            case R.id.btn_toorder:
                Intent intent2 = new Intent(TaskPayResultActivity.this, PayInfoActivity.class);
                intent2.putExtra("userPayId",userPayId);
                startActivity(intent2);
                onBackPressed();
                break;
            case R.id.rtv_communicate:
                if (saleUserId > 0) {
                    Intent IMkitintent = LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(saleUserId + "", LoginSampleHelper.APP_KEY);
                    startActivity(IMkitintent);
                }
                break;
            case R.id.iv_callphone:
                if (StringUtils.isEmpty(merchantTel)) {
                    ToastUtil.showMessage("商家暂时没有联系电话");
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    Uri data = Uri.parse("tel:" + merchantTel);
                    intent.setData(data);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Constants.BROCAST_REFRESH_ORDER);
        intent.putExtra("userPayId", userPayId);
        setResult(RESULT_OK, intent);
        sendBroadcast(intent);
        super.onBackPressed();
    }

}
