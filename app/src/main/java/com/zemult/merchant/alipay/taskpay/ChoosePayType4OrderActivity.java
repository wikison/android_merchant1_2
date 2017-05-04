package com.zemult.merchant.alipay.taskpay;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonSignNumberRequest;
import com.zemult.merchant.aip.common.WxPayApplyPayRequest;
import com.zemult.merchant.aip.mine.UserMerchantPayRequest;
import com.zemult.merchant.aip.mine.UserPaySetWxRequest;
import com.zemult.merchant.alipay.PayResult;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.AppointmentDetailNewActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_WxData;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class ChoosePayType4OrderActivity extends BaseActivity {
    private static final int SDK_PAY_FLAG = 1;
    // 商户订单号
    public String ORDER_SN = "";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_buy_money1)
    TextView tvBuyMoney1;
    @Bind(R.id.tv_buy_money)
    TextView tvBuyMoney;
    @Bind(R.id.tv_num1)
    TextView tvNum1;
    @Bind(R.id.ll_reward)
    LinearLayout llReward;
    @Bind(R.id.ll_buy)
    LinearLayout llBuy;
    @Bind(R.id.cb_zhifubaopay)
    CheckBox cbZhifubaopay;
    @Bind(R.id.rl_wx)
    RelativeLayout rlWx;
    @Bind(R.id.iv_service_head)
    ImageView ivServiceHead;
    @Bind(R.id.tv_servicename)
    TextView tvServicename;


    @Bind(R.id.cb_wx)
    CheckBox cbWx;
    @Bind(R.id.pay)
    Button pay;
    private int userPayId = 0;
    UserMerchantPayRequest userMerchantPayRequest;
    CommonSignNumberRequest commonSignNumberRequest;
    UserPaySetWxRequest userPaySetWxRequest;
    WxPayApplyPayRequest wxPayApplyPayRequest;
    double paymoney, money, rewardMoney;
    String saleName = "", saleHead = "", reservationId = "", reservationTime, merchantName;
    int payType, toUserId;
    private IWXAPI api;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_type_order);
        registerReceiver(new String[]{Constants.BROCAST_WX_PAY_SUCCESS});
    }

    @Override
    public void init() {
        paymoney = getIntent().getDoubleExtra("consumeMoney", 0);//总额
        ORDER_SN = getIntent().getStringExtra("order_sn");
        userPayId = getIntent().getIntExtra("userPayId", 0);
        toUserId = getIntent().getIntExtra("toUserId", 0);
        rewardMoney = getIntent().getDoubleExtra("rewardMoney", 0);//打赏金额
        money = getIntent().getDoubleExtra("money", 0);//订金
        saleHead = getIntent().getStringExtra("saleHead");
        saleName = getIntent().getStringExtra("saleName");
        reservationId = getIntent().getStringExtra("reservationId");
        reservationTime = getIntent().getStringExtra("reservationTime");
        merchantName = getIntent().getStringExtra("merchantName");

        pay.setText("确认支付  ￥" + Convert.getMoneyString(paymoney));
        lhTvTitle.setText("支付订单");

        if (money != 0) {
            llReward.setVisibility(View.VISIBLE);
            tvBuyMoney1.setText("￥" + Convert.getMoneyString(money));
            tvNum1.setText(ORDER_SN + "");
        } else {
            llReward.setVisibility(View.GONE);
        }
        if (rewardMoney != 0) {
            llBuy.setVisibility(View.VISIBLE);
            tvBuyMoney.setText("赞赏 ￥" + Convert.getMoneyString(rewardMoney));
            imageManager.loadCircleImage(saleHead, ivServiceHead);
            tvServicename.setText(saleName);
        } else {
            llBuy.setVisibility(View.GONE);
        }


        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        payType = 1;
        cbZhifubaopay.setChecked(true);
        if (!AppUtils.isWeixinAvailable(this)) {
            rlWx.setVisibility(View.GONE);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private void sendPayMoneyMsg() {
        YWCustomMessageBody messageBody = new YWCustomMessageBody();
        JSONObject object = new JSONObject();
        try {
            object.put("customizeMessageType", "Task");
            object.put("tasktype", "MONEY");//GIFT
            object.put("taskTitle", getIntent().getStringExtra("imMessageTitle"));
            object.put("taskContent", getIntent().getStringExtra("imMessageContent"));
            object.put("billId", userPayId + "");
            object.put("serviceId", toUserId + "");
            object.put("userId", SlashHelper.userManager().getUserId() + "");
        } catch (JSONException e) {

        }
        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
        messageBody.setSummary("[送赞赏]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWContact appContact = YWContactFactory.createAPPContact(toUserId + "", imKit.getIMCore().getAppKey());
        imKit.getConversationService()
                .forwardMsgToContact(appContact
                        , message, forwardCallBack);
//        startActivity(imKit.getChattingActivityIntent(toUserId + "", Urls.APP_KEY));
//        Intent intent = new Intent(ChoosePayTypeActivity.this, SendAppreciateRedActivity.class);
//        intent.putExtra("billId", userPayId);
//        startActivity(intent);
    }


    private void sendOrderSureMessage() {

        YWCustomMessageBody messageBody = new YWCustomMessageBody();
        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
        JSONObject object = new JSONObject();
        try {
            object.put("customizeMessageType", "Task");
            object.put("tasktype", "ORDER");
            object.put("taskTitle", "[服务定单] " + reservationTime + "  " + merchantName + "(商户)");
            object.put("serviceId", toUserId + "");
            object.put("reservationId", reservationId);
        } catch (JSONException e) {

        }
        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
        messageBody.setSummary("[服务定单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWContact appContact = YWContactFactory.createAPPContact(toUserId + "", imKit.getIMCore().getAppKey());
        imKit.getConversationService()
                .forwardMsgToContact(appContact
                        , message, forwardCallBack);



        setResult(RESULT_OK);
        finish();
//        Intent intent1 = new Intent(ChoosePayType4OrderActivity.this, AppointmentDetailNewActivity.class);
//        intent1.putExtra("reservationId", reservationId);
//        startActivity(intent1);
    }


    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(ChoosePayType4OrderActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(ChoosePayType4OrderActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.cb_wx, R.id.cb_zhifubaopay, R.id.pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
//                setResult(RESULT_OK);
                finish();
                break;
            case R.id.cb_zhifubaopay:
                if (cbZhifubaopay.isChecked()) {
                    payType = 1;
                    cbWx.setChecked(false);
                }
                break;
            case R.id.cb_wx:
                if (cbWx.isChecked()) {
                    payType = 2;
                    cbZhifubaopay.setChecked(false);
                }
                break;


            case R.id.pay:
                if (cbZhifubaopay.isChecked() == false && cbWx.isChecked() == false) {
                    ToastUtil.showMessage("请选择一种支付方式");
                    return;
                }
                if (payType == 1) {
                    userTaskPayRequest();
                } else if (payType == 2) {
                    userPaySetWx();
                }

                break;
        }
    }

//    //显示输入安全密码对话框
//    private void showInputPwdDialog() {
//
//        BalancePayAlertView payAlertView = new BalancePayAlertView(
//                ChoosePayType4OrderActivity.this);
//        payAlertView.setAmount(paymoney + "");
//
//        payAlertView
//                .setValidatePasswordListener(new BalancePayAlertView.OnValidatePasswordListener() {
//                    public void onValidateSuccessed(String pwd) {
//                        userTaskPayRequest();
//                    }
//
//                    public void onValidateFailed() {
//                        ToastUtil.showMessage("安全密码验证失败");
//                    }
//                });
//    }

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
                        commonSignNumber();
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

    //设置支付单为微信支付
    private void userPaySetWx() {
        try {
            if (userPaySetWxRequest != null) {
                userPaySetWxRequest.cancel();
            }
            UserPaySetWxRequest.Input input = new UserPaySetWxRequest.Input();
            input.userPayId = userPayId;
            input.convertJosn();

            userPaySetWxRequest = new UserPaySetWxRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        //调用微信支付
                        wxPayApplyPay();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userPaySetWxRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    //获取微信支付参数
    private void wxPayApplyPay() {
        try {
            if (wxPayApplyPayRequest != null) {
                wxPayApplyPayRequest.cancel();
            }
            WxPayApplyPayRequest.Input input = new WxPayApplyPayRequest.Input();
            input.userPayId = userPayId;
            input.convertJosn();

            wxPayApplyPayRequest = new WxPayApplyPayRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    ToastUtil.showMessage(error.toString());
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        M_WxData m_wxData = ((CommonResult) response).wxdata;
                        //调用微信支付
                        PayReq req = new PayReq();
                        req.appId = m_wxData.appid;
                        req.partnerId = m_wxData.partnerid;
                        req.prepayId = m_wxData.prepayid;
                        req.nonceStr = m_wxData.noncestr;
                        req.timeStamp = m_wxData.timestamp;
                        req.packageValue = "Sign=WXPay";
                        req.sign = m_wxData.sign;
                        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                        api.sendReq(req);

                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(wxPayApplyPayRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        if (Constants.BROCAST_WX_PAY_SUCCESS.equals(intent.getAction())) {
            sendOrderSureMessage();
            if (rewardMoney != 0) {
                sendPayMoneyMsg();
            }

        }

    }


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void alipay(final String orderStr) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    PayTask alipay = new PayTask(ChoosePayType4OrderActivity.this);
                    Map<String, String> result = alipay.payV2(orderStr, true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    ToastUtil.showMessage("支付宝调用失败,请再试一次");
                }


            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

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
                        Toast.makeText(ChoosePayType4OrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if (rewardMoney != 0) {
                            sendPayMoneyMsg();
                        }
                        sendOrderSureMessage();

                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(ChoosePayType4OrderActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(ChoosePayType4OrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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

}
