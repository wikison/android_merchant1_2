package com.zemult.merchant.alipay.taskpay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.PayInfoActivity;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.aip.slash.UserMerchantPayCommont_1_1Request;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

public class AssessmentActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.iv_right2)
    ImageView ivRight2;
    @Bind(R.id.ll_right2)
    LinearLayout llRight2;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.rtv_detail)
    RoundTextView rtvDetail;
    @Bind(R.id.ll_success)
    LinearLayout llSuccess;
    @Bind(R.id.iv_userhead)
    ImageView ivUserhead;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_shopname)
    TextView tvShopname;
    @Bind(R.id.ratingbar)
    RatingBar ratingbar;
    @Bind(R.id.btn_topinjia)
    Button btnTopinjia;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    int comment = 0;
    int userPayId;
    @Bind(R.id.et_pingjia)
    EditText etPingjia;
    @Bind(R.id.editnum)
    TextView editnum;
    private String managerhead, managername, merchantName;
    int iToComment = 0;

    UserPayInfoRequest userPayInfoRequest;
    UserMerchantPayCommont_1_1Request userMerchantPayCommont_1_1Request;

    public static String APPOINT_REFLASH = "appointreflash";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_assessment);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        userPayId = getIntent().getIntExtra("userPayId", 0);
        iToComment = getIntent().getIntExtra("toComment", 0);
        managerhead = getIntent().getStringExtra("managerhead");
        managername = getIntent().getStringExtra("managername");
        merchantName = getIntent().getStringExtra("merchantName");
    }

    private void initView() {
        lhTvTitle.setText("评价");

        tvUsername.setText(managername);
        tvShopname.setText(merchantName);
        if (!TextUtils.isEmpty(managerhead)) {
            imageManager.loadCircleHead(managerhead, ivUserhead);
        }

        //从待评价显示过来不展示支付完成
        if (iToComment == 1) {
            llSuccess.setVisibility(View.GONE);
            tvRight.setVisibility(View.GONE);
        } else {
            user_pay_info();
            llSuccess.setVisibility(View.VISIBLE);
            tvRight.setVisibility(View.VISIBLE);
            tvRight.setText("完成");
        }

    }

    private void initListener() {
        ratingbar.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float RatingCount) {
                comment = (int) RatingCount;
            }
        });
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
                    double rewardMoney = ((APIM_UserBillInfo) response).userPayInfo.rewardMoney;
                    if (rewardMoney != 0) {
                        sendPayMoneyMsg(((APIM_UserBillInfo) response).userPayInfo.saleUserId);
                    }
                } else {
                    ToastUtils.show(AssessmentActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }

    private void sendPayMoneyMsg(int toUserId) {
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
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(AssessmentActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(AssessmentActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };


    // 用户评价订单
    private void userMerchantPayCommont_1_1Request() {
        showPd();
        if (userMerchantPayCommont_1_1Request != null) {
            userMerchantPayCommont_1_1Request.cancel();
        }
        UserMerchantPayCommont_1_1Request.Input input = new UserMerchantPayCommont_1_1Request.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.userPayId = userPayId;
        input.comment = comment;
        input.note = etPingjia.getText().toString();
        input.convertJosn();
        userMerchantPayCommont_1_1Request = new UserMerchantPayCommont_1_1Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("感谢您的评价");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userMerchantPayCommont_1_1Request);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rtv_detail, R.id.btn_topinjia, R.id.tv_right})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.tv_right:
                EventBus.getDefault().post(APPOINT_REFLASH);
                onBackPressed();
                break;
            case R.id.rtv_detail:
                intent = new Intent(AssessmentActivity.this, PayInfoActivity.class);
                intent.putExtra("userPayId", userPayId);
                startActivity(intent);
                onBackPressed();
                break;
            case R.id.btn_topinjia:
                if (comment == 0) {
                    ToastUtil.showMessage("您还没有打分");
                    return;
                }
                userMerchantPayCommont_1_1Request();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
