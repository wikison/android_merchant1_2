//package com.zemult.merchant.alipay.taskpay;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alipay.sdk.app.PayTask;
//import com.android.volley.VolleyError;
//import com.zemult.merchant.R;
//import com.zemult.merchant.activity.mine.PayPasswordManagerActivity;
//import com.zemult.merchant.aip.slash.UserBonusePayRequest;
//import com.zemult.merchant.alipay.PayResult;
//import com.zemult.merchant.alipay.PayUtils;
//import com.zemult.merchant.app.BaseActivity;
//import com.zemult.merchant.config.Constants;
//import com.zemult.merchant.model.CommonResult;
//import com.zemult.merchant.util.SlashHelper;
//import com.zemult.merchant.util.ToastUtil;
//import com.zemult.merchant.view.BalancePayAlertView;
//
//import java.util.Map;
//
//import butterknife.Bind;
//import butterknife.OnClick;
//import cn.trinea.android.common.util.ToastUtils;
//import zema.volley.network.ResponseListener;
//
//public class BonuseChoosePayTypeActivity extends BaseActivity {
//
//    @Bind(R.id.lh_btn_back)
//    Button lhBtnBack;
//    @Bind(R.id.ll_back)
//    LinearLayout llBack;
//    @Bind(R.id.iv_right)
//    ImageView ivRight;
//    @Bind(R.id.ll_right)
//    LinearLayout llRight;
//    @Bind(R.id.tv_right)
//    TextView tvRight;
//    @Bind(R.id.lh_tv_title)
//    TextView lhTvTitle;
//    @Bind(R.id.lh_btn_right)
//    Button lhBtnRight;
//    @Bind(R.id.lh_btn_rightiamge)
//    Button lhBtnRightiamge;
//    @Bind(R.id.iv_head)
//    ImageView ivHead;
//    @Bind(R.id.tv_name)
//    TextView tvName;
//    @Bind(R.id.tv_buy_money)
//    TextView tvBuyMoney;
//    @Bind(R.id.tv_num)
//    TextView tvNum;
//    @Bind(R.id.iv_img1)
//    ImageView ivImg1;
//    @Bind(R.id.tv_account_money)
//    TextView tvAccountMoney;
//    @Bind(R.id.cb_accountpay)
//    CheckBox cbAccountpay;
//    @Bind(R.id.iv_img2)
//    ImageView ivImg2;
//    @Bind(R.id.tv_lab1)
//    TextView tvLab1;
//    @Bind(R.id.cb_zhifubaopay)
//    CheckBox cbZhifubaopay;
//    @Bind(R.id.pay)
//    Button btnpay;
//
//    // 商户订单号
//    private static final int SDK_PAY_FLAG = 1;
//    // 商户PID
//    public String PARTNER = "";
//    // 商户收款账号
//    public String SELLER = "";
//    // 商户私钥，pkcs8格式
//    public String RSA_PRIVATE = "";
//    // 商户订单号
//    public String ORDER_SN = "";
//    double paymoney;
//    int payType, taskIndustryId;
//    String title, payNo;
//    UserBonusePayRequest userBonusePayRequest;
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @SuppressWarnings("unused")
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SDK_PAY_FLAG: {
//                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    /**
//                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
//                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
//                     * docType=1) 建议商户依赖异步通知
//                     */
//                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        setResult(RESULT_OK);
//                        ToastUtil.showMessage("任务发布成功");
//                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
//                        BonuseChoosePayTypeActivity.this.finish();
//                    } else {
//                        // 判断resultStatus 为非"9000"则代表可能支付失败
//                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(BonuseChoosePayTypeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
//
//                        } else if (TextUtils.equals(resultStatus, "4000")) {
//                            Toast.makeText(BonuseChoosePayTypeActivity.this, "支付宝调用失败", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
//                            Toast.makeText(BonuseChoosePayTypeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//
//        ;
//    };
//
//    @Override
//    public void setContentView() {
//        setContentView(R.layout.activity_choose_pay_type);
//    }
//
//    @Override
//    public void init() {
//        paymoney = getIntent().getDoubleExtra("bonuseMoney", 0);
//        taskIndustryId = getIntent().getIntExtra("taskIndustryId", 0);
//        title = getIntent().getStringExtra("title");
//        tvBuyMoney.setText("￥" + paymoney);
//        lhTvTitle.setText(title);
//        tvAccountMoney.setText(SlashHelper.userManager().getUserinfo().getMoney() + "");
//        lhTvTitle.setText("支付订单");
//        btnpay.setText("确定");
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.cb_accountpay, R.id.cb_zhifubaopay, R.id.pay})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.lh_btn_back:
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.cb_accountpay:
//                if (paymoney > SlashHelper.userManager().getUserinfo().getMoney()) {
//                    ToastUtil.showMessage("您的余额不够支出，请选择其他支付方式");
//                    cbAccountpay.setChecked(false);
//                    return;
//                }
//                if (cbAccountpay.isChecked()) {
//                    payType = 0;
//                    cbZhifubaopay.setChecked(false);
//                }
//                break;
//            case R.id.cb_zhifubaopay:
//
//                if (cbZhifubaopay.isChecked()) {
//                    payType = 1;
//                    cbAccountpay.setChecked(false);
//
//                }
//                break;
//
//            case R.id.pay:
//                if (cbZhifubaopay.isChecked() == false && cbAccountpay.isChecked() == false) {
//                    ToastUtil.showMessage("请选择一种支付方式");
//                    return;
//                }
//                if (cbAccountpay.isChecked() && SlashHelper.userManager().getUserinfo().isSetPaypwd == 0) {
//                    ToastUtil.showMessage("请设置安全密码");
//                    Intent intentpaypassword = new Intent(BonuseChoosePayTypeActivity.this, PayPasswordManagerActivity.class);
//                    startActivity(intentpaypassword);
//                    return;
//                }
//
//                if (cbAccountpay.isChecked() && SlashHelper.userManager().getUserinfo().isSetPaypwd == 1) {
//                    showInputPwdDialog();
//                } else {
//                    userBonusePayRequest();
//                }
//
//                break;
//        }
//    }
//
//    //显示输入安全密码对话框
//    private void showInputPwdDialog() {
//
//        BalancePayAlertView payAlertView = new BalancePayAlertView(
//                BonuseChoosePayTypeActivity.this);
//        payAlertView.setAmount(paymoney + "");
//
//        payAlertView
//                .setValidatePasswordListener(new BalancePayAlertView.OnValidatePasswordListener() {
//                    public void onValidateSuccessed(String pwd) {
//                        userBonusePayRequest();
//                    }
//
//                    public void onValidateFailed() {
//                        ToastUtil.showMessage("安全密码验证失败");
//                    }
//                });
//    }
//
//    private void userBonusePayRequest() {
//        if (userBonusePayRequest != null) {
//            userBonusePayRequest.cancel();
//        }
//        UserBonusePayRequest.Input input = new UserBonusePayRequest.Input();
//        if (SlashHelper.userManager().getUserinfo() != null) {
//            input.userId = SlashHelper.userManager().getUserId();
//        }
//        input.taskIndustryId = taskIndustryId;
//        input.name = title;    //		任务名称
//        input.payType = payType;    //		支付类型(0:账户余额,1:支付宝)
//        input.money = paymoney;//红包金额
//        input.convertJosn();
//        userBonusePayRequest = new UserBonusePayRequest(input, new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dismissPd();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                if (((CommonResult) response).status == 1) {
//                    PARTNER = ((CommonResult) response).PARTNER;
//                    SELLER = ((CommonResult) response).SELLER;
//                    RSA_PRIVATE = ((CommonResult) response).RSA_PRIVATE;
//                    ORDER_SN = ((CommonResult) response).number;
//                    if (payType == 1) {
//                        alipay();
//                    } else {
//                        setResult(RESULT_OK);
//                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
//                        ToastUtil.showMessage("任务发布成功");
//                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
//                        sendBroadcast(updateintent);
//                        finish();
//                    }
//                } else {
//                    ToastUtils.show(BonuseChoosePayTypeActivity.this, ((CommonResult) response).info);
//                }
//                dismissPd();
//            }
//        });
//        sendJsonRequest(userBonusePayRequest);
//    }
//
//    /**
//     * call alipay sdk pay. 调用SDK支付
//     */
//    public void alipay() {
//        Map<String, String> params = PayUtils.buildOrderParamMap(SELLER, "发红包", "发布探索红包", paymoney + "", ORDER_SN);
//        String orderParam = PayUtils.buildOrderParam(params);
//        String sign = PayUtils.getSign(params, RSA_PRIVATE);
//        final String orderInfo = orderParam + "&" + sign;
//
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(BonuseChoosePayTypeActivity.this);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Message msg = new Message();
//                msg.what = SDK_PAY_FLAG;
//                msg.obj = result;
//                mHandler.sendMessage(msg);
//            }
//        };
//
//        // 必须异步调用
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }
//}
