package com.zemult.merchant.alipay.taskpay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.SendPresentActivity;
import com.zemult.merchant.activity.slash.SendPresentSuccessActivity;
import com.zemult.merchant.aip.common.CommonSignNumberRequest;
import com.zemult.merchant.aip.common.WxPayApplyPayRequest;
import com.zemult.merchant.aip.mine.UserMerchantPayRequest;
import com.zemult.merchant.aip.mine.UserPaySetWxRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.alipay.PayResult;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.model.M_WxData;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BalancePayAlertView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ChoosePayTypeActivity extends BaseActivity {
    public static final String MERCHANT_INFO = "merchantInfo";
    public static final String USER_INFO = "userInfo";
    private static final int SDK_PAY_FLAG = 1;
    // 商户订单号
    public String ORDER_SN = "";
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
    @Bind(R.id.tv_name1)
    TextView tvName1;
    @Bind(R.id.tv_buy_money1)
    TextView tvBuyMoney1;
    @Bind(R.id.tv_num1)
    TextView tvNum1;
    @Bind(R.id.ll_reward)
    LinearLayout llReward;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_distance)
    TextView tvDistance;
    @Bind(R.id.iv_qianyue)
    ImageView ivQianyue;
    @Bind(R.id.card_view)
    CardView cardView;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.tv_buy_money)
    TextView tvBuyMoney;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.ll_buy)
    LinearLayout llBuy;
    @Bind(R.id.iv_img2)
    ImageView ivImg2;
    @Bind(R.id.tv_lab1)
    TextView tvLab1;
    @Bind(R.id.cb_zhifubaopay)
    CheckBox cbZhifubaopay;
    @Bind(R.id.iv_wx)
    ImageView ivWx;
    @Bind(R.id.cb_wx)
    CheckBox cbWx;
    @Bind(R.id.pay)
    Button pay;
    private int userPayId = 0;
    UserMerchantPayRequest userMerchantPayRequest;
    CommonSignNumberRequest commonSignNumberRequest;
    UserPaySetWxRequest userPaySetWxRequest;
    WxPayApplyPayRequest wxPayApplyPayRequest;
    double paymoney, truepaymoney;
    String merchantName = "", merchantHead = "", managerhead = "", managername = "";
    int payType, toUserId;
    MerchantInfoRequest merchantInfoRequest;
    int merchantId;
    private IWXAPI api;

    M_Present m_present;
    M_Merchant m_merchant;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_type);
    }

    @Override
    public void init() {
        paymoney = getIntent().getDoubleExtra("consumeMoney", 0);
        ORDER_SN = getIntent().getStringExtra("order_sn");
        userPayId = getIntent().getIntExtra("userPayId", 0);
        toUserId = getIntent().getIntExtra("toUserId", 0);
        merchantId = getIntent().getIntExtra("merchantId", 0);
        merchantName = getIntent().getStringExtra("merchantName");
        merchantHead = getIntent().getStringExtra("merchantHead");
        managerhead = getIntent().getStringExtra("managerhead");
        managername = getIntent().getStringExtra("managername");
        m_present = (M_Present) getIntent().getSerializableExtra(SendPresentActivity.PRESENT);
        m_merchant = (M_Merchant) getIntent().getSerializableExtra(FindPayActivity.MERCHANT_INFO);
        truepaymoney = paymoney;

        pay.setText("确认支付  ￥" + Convert.getMoneyString(truepaymoney));
        lhTvTitle.setText("支付订单");

        if ("赞赏".equals(merchantName)) {
            llReward.setVisibility(View.VISIBLE);
            llBuy.setVisibility(View.GONE);
            tvBuyMoney1.setText(" ￥" + Convert.getMoneyString(truepaymoney));
            tvName1.setText("" + merchantName);
            tvNum1.setText(ORDER_SN);
            imageManager.loadCircleResImage(R.mipmap.chart_hongbao_icon, ivHead);
        } else {
            llReward.setVisibility(View.GONE);
            llBuy.setVisibility(View.VISIBLE);

            if (m_merchant == null) {
                merchant_info(merchantId);
            } else {
                initMerchantInfo();
            }

        }

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        payType = 1;
        cbZhifubaopay.setChecked(true);

    }

    private void initMerchantInfo() {
        llReward.setVisibility(View.GONE);
        llBuy.setVisibility(View.VISIBLE);
        // 商家封面
        if (!TextUtils.isEmpty(m_merchant.pic))
            imageManager.loadUrlImageWithDefaultImg(m_merchant.pic, ivCover, "@300h", R.mipmap.merchant_default_cover);
        else
            ivCover.setImageResource(R.mipmap.merchant_default_cover);
        // 商家名称
        if (!TextUtils.isEmpty(m_merchant.name))
            tvName.setText(m_merchant.name);
        // 人均消费
        tvMoney.setText("人均￥" + (int) (m_merchant.perMoney));
        // 距中心点距离(米)
        if (!StringUtils.isEmpty(m_merchant.distance)) {
            if (m_merchant.distance.length() > 3) {
                double d = Double.valueOf(m_merchant.distance);
                tvDistance.setText(d / 1000 + "km");
            } else
                tvDistance.setText(m_merchant.distance + "m");
        }

        tvBuyMoney.setText(" ￥" + Convert.getMoneyString(truepaymoney));
        tvMerchantName.setText("向" + m_merchant.name);
        tvNum.setText(ORDER_SN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void sendPayGiftMsg() {
        YWCustomMessageBody messageBody = new YWCustomMessageBody();
        JSONObject object = new JSONObject();
        try {
            object.put("customizeMessageType", "Task");
            object.put("tasktype", "GIFT");
            object.put("taskTitle", AppUtils.giftDescription(m_present.name));
            object.put("reservationId", m_present.presentId + "");
            object.put("giftName", m_present.name);
            object.put("serviceId", toUserId + "");
            object.put("userId", SlashHelper.userManager().getUserId() + "");
            object.put("giftPrice", m_present.price + "");
        } catch (JSONException e) {

        }
        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
        messageBody.setSummary("[送礼物]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
        IYWContact appContact = YWContactFactory.createAPPContact(toUserId + "", imKit.getIMCore().getAppKey());
        imKit.getConversationService()
                .forwardMsgToContact(appContact
                        , message, forwardCallBack);
//        startActivity(imKit.getChattingActivityIntent(userPayId+""));
        Intent intent = new Intent(ChoosePayTypeActivity.this, SendPresentSuccessActivity.class);
        intent.putExtra("giftName", m_present.name);
        intent.putExtra("giftPrice", m_present.price + "");
        startActivity(intent);
        finish();
    }

    //商家详情
    private void merchant_info(int merchantId) {
        if (merchantInfoRequest != null) {
            merchantInfoRequest.cancel();
        }


        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.merchantId = merchantId;

        input.convertJosn();
        merchantInfoRequest = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    m_merchant = ((APIM_MerchantGetinfo) response).merchant;
                    initMerchantInfo();
                } else {
                    ToastUtils.show(ChoosePayTypeActivity.this, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantInfoRequest);
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
        startActivity(imKit.getChattingActivityIntent(toUserId + "", Urls.APP_KEY));
//        Intent intent = new Intent(ChoosePayTypeActivity.this, SendAppreciateRedActivity.class);
//        intent.putExtra("billId", userPayId);
//        startActivity(intent);
        finish();


    }


    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(ChoosePayTypeActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(ChoosePayTypeActivity.this, "forward fail!");

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
                setResult(RESULT_OK);
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

    //显示输入安全密码对话框
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
                        ToastUtil.showMessage("安全密码验证失败");
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
                            if (null != m_present) {
                                sendPayGiftMsg();
                            } else {
                                if ("赞赏".equals(merchantName)) {
                                    sendPayMoneyMsg();
                                } else {
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

                            }
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
                    System.out.print(error);
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

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void alipay(final String orderStr) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    PayTask alipay = new PayTask(ChoosePayTypeActivity.this);
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
                        Toast.makeText(ChoosePayTypeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if (null != m_present) {
                            sendPayGiftMsg();
                        } else {
                            if ("赞赏".equals(merchantName)) {
                                sendPayMoneyMsg();
                            } else {
                                Intent intent = new Intent(ChoosePayTypeActivity.this, TaskPayResultActivity.class);
                                intent.putExtra("managerhead", managerhead);
                                intent.putExtra("paymoney", paymoney);
                                intent.putExtra("managername", managername);
                                intent.putExtra("merchantName", merchantName);
                                intent.putExtra("userPayId", userPayId);
                                intent.putExtra("imMessageTitle", getIntent().getStringExtra("imMessageTitle"));
                                intent.putExtra("imMessageContent", getIntent().getStringExtra("imMessageContent"));
                                startActivityForResult(intent, 1000);
                            }

                        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }
}
