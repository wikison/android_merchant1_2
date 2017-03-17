package com.zemult.merchant.alipay.taskpay;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWCustomMessageBody;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.activity.slash.SendPresentSuccessActivity;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
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
    String managerhead, managername, merchantName;
    double paymoney;
    public static String APPOINT_REFLASH = "appointreflash";

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
        managername = getIntent().getStringExtra("managername");
        merchantName = getIntent().getStringExtra("merchantName");
        paymoney = getIntent().getDoubleExtra("paymoney", 0);
//        if (userPayId > 0)
//            user_pay_info();
//        merchant_info(userPayId);

        user_pay_info();

        if (paymoney < 50) {
            btnTopinjia.setVisibility(View.GONE);
        } else {
            btnTopinjia.setVisibility(View.VISIBLE);
        }

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
                  double  rewardMoney=((APIM_UserBillInfo) response).userPayInfo.rewardMoney;
                    if(rewardMoney!=0){
                        sendPayMoneyMsg(((APIM_UserBillInfo) response).userPayInfo.saleUserId);

                    }


//                    m_bill = ((APIM_UserBillInfo) response).userPayInfo;
//                    tvOrderno.setText("订单号：" + m_bill.number);
//                    tvPayMoney.setText("交易金额：" + m_bill.payMoney);
//                    tvPaytime.setText("买单时间：" + payTime);
//                    saleUserId = m_bill.saleUserId;
//                    tvSaleName.setText(m_bill.saleUserName);
//                    imageManager.loadCircleImage(m_bill.saleUserHead, ivSaleCover);

//                    merchant_info(m_bill.merchantId);

                } else {
                    ToastUtils.show(TaskPayResultActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }


    private void  sendPayMoneyMsg(int toUserId){
        YWCustomMessageBody messageBody = new YWCustomMessageBody();
        JSONObject object = new JSONObject();
        try {
            object.put("customizeMessageType", "Task");
            object.put("tasktype", "MONEY");//GIFT
            object.put("taskTitle", "赞赏红包");
            object.put("taskContent",getIntent().getStringExtra("imMessage"));
            object.put("billId", userPayId+"");
            object.put("serviceId", toUserId+"");
            object.put("userId", SlashHelper.userManager().getUserId()+"");
        } catch (JSONException e) {

        }
        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
        messageBody.setSummary("[送红包]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
        YWIMKit imKit= LoginSampleHelper.getInstance().getIMKit();
        IYWContact appContact = YWContactFactory.createAPPContact(toUserId+"", imKit.getIMCore().getAppKey());
        imKit.getConversationService()
                .forwardMsgToContact(appContact
                        ,message,forwardCallBack);
//        startActivity(imKit.getChattingActivityIntent(toUserId+"", Urls.APP_KEY));
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(TaskPayResultActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(TaskPayResultActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };


//
//    //商家详情
//    private void merchant_info(int merchantId) {
//        showPd();
//        if (merchantInfoRequest != null) {
//            merchantInfoRequest.cancel();
//        }
//
//        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
//        input.merchantId = merchantId;
//
//        input.convertJosn();
//        merchantInfoRequest = new MerchantInfoRequest(input, new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                dismissPd();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                if (((APIM_MerchantGetinfo) response).status == 1) {
//                    M_Merchant m = ((APIM_MerchantGetinfo) response).merchant;
//                    tvZhifutip.setText(m.address);
//                    tvLab1.setText(m.name);
//                    merchantTel = m.tel;
//
//                } else {
//                    ToastUtils.show(TaskPayResultActivity.this, ((APIM_MerchantGetinfo) response).info);
//                }
//                dismissPd();
//            }
//        });
//        sendJsonRequest(merchantInfoRequest);
//    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_topinjia, R.id.btn_toorder, R.id.rtv_communicate, R.id.iv_callphone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                EventBus.getDefault().post(APPOINT_REFLASH);

                onBackPressed();
                break;
            case R.id.btn_topinjia:
                Intent intent3 = new Intent(TaskPayResultActivity.this, AssessmentActivity.class);
                intent3.putExtra("userPayId", userPayId);
                intent3.putExtra("managerhead", managerhead);
                intent3.putExtra("managername", managername);
                intent3.putExtra("merchantName", merchantName);
                startActivity(intent3);
                onBackPressed();
                break;
            case R.id.btn_toorder:
                Intent intent2 = new Intent(TaskPayResultActivity.this, PayInfoActivity.class);
                intent2.putExtra("userPayId", userPayId);
                startActivity(intent2);
                onBackPressed();

                break;
            case R.id.rtv_communicate:
                if (saleUserId > 0) {
                    Intent IMkitintent = LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(saleUserId + "", Urls.APP_KEY);
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
