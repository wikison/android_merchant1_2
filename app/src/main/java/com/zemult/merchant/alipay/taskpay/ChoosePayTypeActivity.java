package com.zemult.merchant.alipay.taskpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.PayPasswordManagerActivity;
import com.zemult.merchant.aip.common.CommonSignNumberRequest;
import com.zemult.merchant.aip.mine.UserMerchantPayRequest;
import com.zemult.merchant.alipay.PayResult;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BalancePayAlertView;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class ChoosePayTypeActivity extends BaseActivity {
    public static final String MERCHANT_INFO = "merchantInfo";
    public static final String USER_INFO = "userInfo";
    private static final int SDK_PAY_FLAG = 1;
    // 商户订单号
    public String ORDER_SN = "";
    private int userPayId = 0;
    UserMerchantPayRequest userMerchantPayRequest;
    CommonSignNumberRequest commonSignNumberRequest;
    double paymoney, truepaymoney;
    String merchantName = "", merchantHead = "",managerhead="",managername="";
    int payType;

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;

    @Bind(R.id.ll_back)
    LinearLayout llBack;

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_buy_money)
    TextView tvBuyMoney;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.cb_accountpay)
    CheckBox cbAccountpay;
    @Bind(R.id.tv_lab1)
    TextView tvLab1;
    @Bind(R.id.cb_zhifubaopay)
    CheckBox cbZhifubaopay;
    @Bind(R.id.pay)
    Button pay;
    @Bind(R.id.tv_account_money)
    TextView tvAccountMoney;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();
                    // 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(ChoosePayTypeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChoosePayTypeActivity.this, TaskPayResultActivity.class);
                        intent.putExtra("managerhead", managerhead);
                        intent.putExtra("paymoney", paymoney);
                        intent.putExtra("managername", managername);
                        intent.putExtra("merchantName", merchantName);
                        intent.putExtra("userPayId", userPayId);
                        startActivityForResult(intent, 1000);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(ChoosePayTypeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(ChoosePayTypeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_type);
    }

    @Override
    public void init() {
        paymoney = getIntent().getDoubleExtra("consumeMoney", 0);
        ORDER_SN = getIntent().getStringExtra("order_sn");
        userPayId = getIntent().getIntExtra("userPayId", 0);
        merchantName = getIntent().getStringExtra("merchantName");
        merchantHead = getIntent().getStringExtra("merchantHead");
        managerhead = getIntent().getStringExtra("managerhead");
        managername= getIntent().getStringExtra("managername");

        truepaymoney = paymoney;

        tvBuyMoney.setText(" ￥" + Convert.getMoneyString(truepaymoney));
        pay.setText("确认支付  ￥" + Convert.getMoneyString(truepaymoney));
        tvAccountMoney.setText("余额 "+SlashHelper.userManager().getUserinfo().getMoney() + "");
        lhTvTitle.setText("支付订单");
        tvName.setText(""+merchantName);
        tvNum.setText(ORDER_SN);
        imageManager.loadCircleImage(merchantHead, ivHead);
        if (SlashHelper.userManager().getUserinfo().getMoney() >= truepaymoney) {
            cbAccountpay.setChecked(true);
        } else {
            cbZhifubaopay.setChecked(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.cb_accountpay, R.id.cb_zhifubaopay, R.id.pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.cb_accountpay:
                if (truepaymoney > SlashHelper.userManager().getUserinfo().getMoney()) {
                    ToastUtil.showMessage("您的余额不够支出，请选择其他支付方式");
                    cbAccountpay.setChecked(false);
                    return;
                }
                if (cbAccountpay.isChecked()) {
                    payType = 0;
                    cbZhifubaopay.setChecked(false);
                }
                break;
            case R.id.cb_zhifubaopay:

                if (cbZhifubaopay.isChecked()) {
                    payType = 1;
                    cbAccountpay.setChecked(false);

                }
                break;

            case R.id.pay:
                if (cbZhifubaopay.isChecked() == false && cbAccountpay.isChecked() == false) {
                    ToastUtil.showMessage("请选择一种支付方式");
                    return;
                }
                if (cbAccountpay.isChecked() && SlashHelper.userManager().getUserinfo().isSetPaypwd == 0) {
                    ToastUtil.showMessage("请设置支付密码");
                    Intent intentpaypassword = new Intent(ChoosePayTypeActivity.this, PayPasswordManagerActivity.class);
                    startActivity(intentpaypassword);
                    return;
                }

                if (cbAccountpay.isChecked() && SlashHelper.userManager().getUserinfo().isSetPaypwd == 1) {
                    showInputPwdDialog();
                } else {
                    userTaskPayRequest();
                }


                break;
        }
    }

    //显示输入支付密码对话框
    private void showInputPwdDialog() {

        BalancePayAlertView payAlertView = new BalancePayAlertView(
                ChoosePayTypeActivity.this);
        payAlertView.setAmount(truepaymoney + "");

        payAlertView
                .setValidatePasswordListener(new BalancePayAlertView.OnValidatePasswordListener() {
                    public void onValidateSuccessed(String pwd) {
                        userTaskPayRequest();
                    }

                    public void onValidateFailed() {
                        ToastUtil.showMessage("支付密码验证失败");
                    }
                });
    }

    private void userTaskPayRequest() {
        try {
            showPd();

            if (userMerchantPayRequest != null) {
                userMerchantPayRequest.cancel();
            }
            UserMerchantPayRequest.Input input = new UserMerchantPayRequest.Input();
            input.number = ORDER_SN;
            input.payType = payType;

            input.convertJosn();

            userMerchantPayRequest = new UserMerchantPayRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        if (payType == 1) {
                            commonSignNumber();
                        } else {
                            Toast.makeText(ChoosePayTypeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChoosePayTypeActivity.this, TaskPayResultActivity.class);
                            intent.putExtra("userPayId", userPayId);
                            intent.putExtra("payTime", ((CommonResult) response).payTime);
                            intent.putExtra("paymoney", paymoney);
                            intent.putExtra("managerhead", managerhead);
                            intent.putExtra("managername", managername);
                            intent.putExtra("merchantName", merchantName);
                            startActivityForResult(intent, 1000);
                            Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                            sendBroadcast(updateintent);
                            setResult(RESULT_OK);
                            finish();
                        }
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userMerchantPayRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }


    private void commonSignNumber() {
        try {
            showPd();

            if (commonSignNumberRequest != null) {
                commonSignNumberRequest.cancel();
            }
            CommonSignNumberRequest.Input input = new CommonSignNumberRequest.Input();
            input.number = ORDER_SN;

            input.convertJosn();

            commonSignNumberRequest = new CommonSignNumberRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        alipay(((CommonResult) response).orderStr);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(commonSignNumberRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void alipay(final String orderStr) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(ChoosePayTypeActivity.this);
                Map<String, String> result = alipay.payV2(orderStr, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
