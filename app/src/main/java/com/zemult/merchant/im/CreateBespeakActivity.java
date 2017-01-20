package com.zemult.merchant.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.zemult.merchant.R;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.ChattingOperationCustomSample;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.PMNumView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class CreateBespeakActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.bespek_time)
    TextView bespekTime;
    @Bind(R.id.bespek_shopname)
    TextView bespekShopname;
    @Bind(R.id.btn_bespeak_commit)
    Button btnBespeakCommit;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;
    @Bind(R.id.et_bespeak)
    EditText etBespeak;


    UserReservationAddRequest userReservationAddRequest;

    int serviceId;
    String shopname="",ordertime="",ordername="",orderphone="",orderpeople,
            shophead="http://xiegang.oss-cn-shanghai.aliyuncs.com/app/android_1110201480495229434.jpg", note;
    int merchantId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeak);
        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                orderpeople = num+"";
            }
        });


    }

    @Override
    public void init() {
        serviceId=getIntent().getIntExtra("serviceId",50);
        shophead=getIntent().getStringExtra("shophead");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void user_reservation_add() {

        try {
            if (userReservationAddRequest != null) {
                userReservationAddRequest.cancel();
            }
            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
            input.merchantId = merchantId;
            input.saleUserId = serviceId;
            input.reservationTime = ordertime;
            input.num = orderpeople;
            input.userName = ordername;
            input.userPhone = orderphone;
            input.note = note;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userReservationAddRequest = new UserReservationAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }
                @Override
                public void onResponse(Object response) {
                    int status = ((APIM_TaskIndustryInfo) response).status;
                    if (status == 1) {
                        finish();
                    } else {
                        ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                    }
                }
            });
            sendJsonRequest(userReservationAddRequest);
        } catch (Exception e) {
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit,R.id.rl_ordershopname,R.id.rl_ordertime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                if (noLogin(CreateBespeakActivity.this))
                    return;
                shopname=  bespekShopname.getText().toString();
                ordertime=  bespekTime.getText().toString();
                pmnvSelectDeadline.getText().toString();
                note=etBespeak.getText().toString();
                if(StringUtils.isEmpty(shopname)){
                    return;
                }
                if(StringUtils.isEmpty(ordertime)){
                    ToastUtil.showMessage("请选择预约时间");
                    return;
                }
                if(StringUtils.isEmpty(orderpeople+"")){
                    ToastUtil.showMessage("请选择预预定人数");
                    return;
                }
                if(StringUtils.isEmpty(ordername)){
                    ToastUtil.showMessage("请填写预约人姓名");
                    return;
                }
                if(StringUtils.isEmpty(orderphone)){
                    ToastUtil.showMessage("请填写预约人电话");
                    return;
                }

                YWCustomMessageBody messageBody = new YWCustomMessageBody();
                //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                JSONObject object = new JSONObject();
                try {
                    object.put("customizeMessageType", "Task");
                    object.put("userHead", shophead);
                    object.put("taskTitle", "[预约-待确认] 预约时间:"+ordertime+"预约地址:"+shopname);
                    object.put("serviceId", serviceId);
                } catch (JSONException e) {

                }

                messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                YWIMKit  imKit= LoginSampleHelper.getInstance().getIMKit();
                IYWContact appContact = YWContactFactory.createAPPContact(serviceId+"", imKit.getIMCore().getAppKey());

                imKit.getConversationService()
                        .forwardMsgToContact(appContact
                                ,message,forwardCallBack);
                startActivity(imKit.getChattingActivityIntent(serviceId+""));
                user_reservation_add();

                break;

            case R.id.rl_ordershopname:
                Intent intent1=new Intent(CreateBespeakActivity.this,ChooseShopListActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_ordertime:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, bespekTime.getText().toString(), "截止时间必须大于当前时间", 2);
                dateTimePicKDialog.dateTimePicKDialog(bespekTime);
                break;

        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(CreateBespeakActivity.this,"forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(CreateBespeakActivity.this,"forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
