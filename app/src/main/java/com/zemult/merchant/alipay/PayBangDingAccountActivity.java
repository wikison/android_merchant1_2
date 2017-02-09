//package com.zemult.merchant.alipay;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alipay.sdk.app.PayTask;
//import com.android.volley.VolleyError;
//import com.zemult.merchant.R;
//import com.zemult.merchant.aip.common.CommonSignNumberRequest;
//import com.zemult.merchant.aip.mine.UserBandcardPayRequest;
//import com.zemult.merchant.app.base.MBaseActivity;
//import com.zemult.merchant.model.CommonResult;
//import com.zemult.merchant.util.SlashHelper;
//import com.zemult.merchant.util.ToastUtil;
//
//import java.util.Map;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import zema.volley.network.ResponseListener;
//
///**
// * 绑定账号支付验证
// */
//
//public class PayBangDingAccountActivity extends MBaseActivity {
//    private static final int SDK_PAY_FLAG = 1;
//    // 商户订单号
//    public String ORDER_SN = "";
//    UserBandcardPayRequest userBandcardPayRequest;
//    CommonSignNumberRequest commonSignNumberRequest;
//    @Bind(R.id.lh_btn_back)
//    Button lhBtnBack;
//    @Bind(R.id.ll_back)
//    LinearLayout llBack;
//    @Bind(R.id.lh_tv_title)
//    TextView lhTvTitle;
//    @Bind(R.id.product_subject)
//    TextView productSubject;
//    @Bind(R.id.pro_info)
//    TextView proInfo;
//    @Bind(R.id.product_price)
//    TextView productPrice;
//
//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//        @SuppressWarnings("unused")
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case SDK_PAY_FLAG: {
//                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    String resultInfo = payResult.getResult();
//                    String resultStatus = payResult.getResultStatus();
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(PayBangDingAccountActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(PayBangDingAccountActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
//
//                        } else if (TextUtils.equals(resultStatus, "4000")) {
//                            Toast.makeText(PayBangDingAccountActivity.this, "支付宝调用失败", Toast.LENGTH_SHORT).show();
//                        }else {
//
//                            Toast.makeText(PayBangDingAccountActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                    break;
//                }
//                default:
//                    break;
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_paybangdingaccount);
//        ButterKnife.bind(this);
//        lhTvTitle.setText("账号绑定");
//        productSubject.setText("支付宝绑定");
//        proInfo.setText("绑定支付宝账号0.01元");
//        productPrice.setText("0.01元");
//        pd.setCancelable(false);
//        pd.setCanceledOnTouchOutside(false);
//    }
//
//
//    private void commonSignNumber() {
//        try {
//            showPd();
//
//            if (commonSignNumberRequest != null) {
//                commonSignNumberRequest.cancel();
//            }
//            CommonSignNumberRequest.Input input = new CommonSignNumberRequest.Input();
//            input.number = ORDER_SN;
//
//            input.convertJosn();
//
//            commonSignNumberRequest = new CommonSignNumberRequest(input, new ResponseListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    dismissPd();
//                    System.out.print(error);
//                }
//
//                @Override
//                public void onResponse(Object response) {
//                    int status = ((CommonResult) response).status;
//                    if (status == 1) {
//                        alipay(((CommonResult) response).orderStr);
//                    } else {
//                        ToastUtil.showMessage(((CommonResult) response).info);
//                    }
//                    dismissPd();
//                }
//            });
//            sendJsonRequest(commonSignNumberRequest);
//        } catch (Exception e) {
//            dismissPd();
//        }
//    }
//
//    /**
//     * call alipay sdk pay. 调用SDK支付
//     */
//    public void alipay(final String orderStr) {
//        Runnable payRunnable = new Runnable() {
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(PayBangDingAccountActivity.this);
//                Map<String, String> result = alipay.payV2(orderStr, true);
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
//
//    private void user_bandcard_pay() {
//        try {
//            showPd();
//            if (userBandcardPayRequest != null) {
//                userBandcardPayRequest.cancel();
//            }
//            UserBandcardPayRequest.Input input = new UserBandcardPayRequest.Input();
//            input.userId = SlashHelper.userManager().getUserId();
//            input.money = 0.01;
//            input.convertJosn();
//
//            userBandcardPayRequest = new UserBandcardPayRequest(input, new ResponseListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    dismissPd();
//                    System.out.print(error);
//                }
//
//                @Override
//                public void onResponse(Object response) {
//                    int status = ((CommonResult) response).status;
//                    if (status == 1) {
//                        dismissPd();
//                        ORDER_SN = ((CommonResult) response).number;
//                        commonSignNumber();
//                    }
//                }
//            });
//            sendJsonRequest(userBandcardPayRequest);
//        } catch (Exception e) {
//        }
//
//    }
//
//    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.pay})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.lh_btn_back:
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.pay:
//                user_bandcard_pay();
//                break;
//        }
//    }
//}
