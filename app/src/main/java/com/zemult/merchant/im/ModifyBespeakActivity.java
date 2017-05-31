//package com.zemult.merchant.im;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.alibaba.mobileim.YWIMKit;
//import com.alibaba.mobileim.channel.event.IWxCallback;
//import com.alibaba.mobileim.contact.IYWContact;
//import com.alibaba.mobileim.contact.YWContactFactory;
//import com.alibaba.mobileim.conversation.YWCustomMessageBody;
//import com.alibaba.mobileim.conversation.YWMessage;
//import com.alibaba.mobileim.conversation.YWMessageChannel;
//import com.android.volley.VolleyError;
//import com.flyco.roundview.RoundTextView;
//import com.zemult.merchant.R;
//import com.zemult.merchant.activity.slash.ChooseReservationMerchantActivity;
//import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
//import com.zemult.merchant.app.BaseActivity;
//import com.zemult.merchant.im.common.Notification;
//import com.zemult.merchant.im.sample.LoginSampleHelper;
//import com.zemult.merchant.model.CommonResult;
//import com.zemult.merchant.model.M_Merchant;
//import com.zemult.merchant.util.AppUtils;
//import com.zemult.merchant.util.DateTimePickDialogUtil;
//import com.zemult.merchant.util.SlashHelper;
//import com.zemult.merchant.util.ToastUtil;
//import com.zemult.merchant.view.PMNumView;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import butterknife.Bind;
//import butterknife.OnClick;
//import cn.trinea.android.common.util.StringUtils;
//import zema.volley.network.ResponseListener;
//
//public class ModifyBespeakActivity extends BaseActivity {
//
//    @Bind(R.id.bespek_time)
//    TextView bespekTime;
//    @Bind(R.id.bespek_shopname)
//    TextView bespekShopname;
//    @Bind(R.id.btn_bespeak_commit)
//    RoundTextView btnBespeakCommit;
//    @Bind(R.id.pmnv_select_deadline)
//    PMNumView pmnvSelectDeadline;
//    @Bind(R.id.et_dingjin)
//    EditText etDingjin;
//    @Bind(R.id.et_customerremark)
//    EditText etCustomerRemark;
//    @Bind(R.id.lh_btn_back)
//    Button lhBtnBack;
//    @Bind(R.id.lh_tv_title)
//    TextView lhTvTitle;
//    @Bind(R.id.ll_back)
//    LinearLayout llBack;
//    @Bind(R.id.firstline)
//    View viewFirstline;
//    @Bind(R.id.rl_orderuser)
//    RelativeLayout rlOrderuser;
//    @Bind(R.id.play_btn)
//    Button playBtn;
//
//
//    UserReservationAddRequest userReservationAddRequest;
//    int userSex = 0;
//    int serviceId;
//    String shopname = "", ordertime = "",strdingjin="",strremark="", orderpeople, note;
//    int merchantId;
//    M_Merchant m_merchant;
//    int CHOOSEMERCHANT = 100;
//    boolean isFromMerchant;
//
//    int showOrderState;//1 生成预约单(有语音)  2 生成预约单（无语音）
//                       // 3 预约单 （待确定  服务管家） 4 预约单 （待确定  客户）
//                       //5  已确认（服务管家）  6  已确认（客户）
//
//
//    @Override
//    public void setContentView() {
//        setContentView(R.layout.activity_bespeaknew);
//
//    }
//
//
//    @Override
//    public void init() {
//        serviceId = getIntent().getIntExtra("serviceId", 0);
//        m_merchant = (M_Merchant) getIntent().getExtras().getSerializable("m_merchant");
//        isFromMerchant = m_merchant == null ? false : true;
//        if (isFromMerchant) {
//            shopname = m_merchant.getName();
//            merchantId = m_merchant.getMerchantId();
//            bespekShopname.setText(shopname);
//            bespekShopname.setCompoundDrawables(null, null, null, null);
//            viewFirstline.setVisibility(View.VISIBLE);
//            rlOrderuser.setVisibility(View.VISIBLE);
//        } else {
//            viewFirstline.setVisibility(View.GONE);
//            rlOrderuser.setVisibility(View.GONE);
//            bespekShopname.setText("请选择商户");
//        }
//
//        pmnvSelectDeadline.setMinNum(1);
//        pmnvSelectDeadline.setMaxNum(99);
//        pmnvSelectDeadline.setDefaultNum(1);
//        pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
//        orderpeople = "" + pmnvSelectDeadline.getDefaultNum();
//        pmnvSelectDeadline.setFilter();
//
//
//        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
//            @Override
//            public void onNumChanged(int num) {
//                orderpeople = num + "";
//                pmnvSelectDeadline.setDefaultNum(num);
//            }
//        });
//        lhTvTitle.setText("找TA约服");
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    private void user_reservation_add() {
//
//        try {
//            if (userReservationAddRequest != null) {
//                userReservationAddRequest.cancel();
//            }
//            UserReservationAddRequest.Input input = new UserReservationAddRequest.Input();
//            input.merchantId = merchantId+"";
//            input.saleUserId = serviceId;
//            input.reservationTime = ordertime + ":00";
//            input.num = orderpeople;
//            input.note = note;
//            input.userId = SlashHelper.userManager().getUserId();
//            input.convertJosn();
//
//            userReservationAddRequest = new UserReservationAddRequest(input, new ResponseListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.print(error);
//                }
//
//                @Override
//                public void onResponse(Object response) {
//                    if (((CommonResult) response).status == 1) {
//                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
//                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
//                        JSONObject object = new JSONObject();
//                        try {
//                            object.put("customizeMessageType", "Task");
//                            object.put("tasktype", "ORDER");
//                            object.put("taskTitle", "[预约-待确认] 预约时间:" + ordertime + "预约地址:" + shopname);
//                            object.put("serviceId", serviceId + "");
//                            object.put("reservationId", ((CommonResult) response).reservationId);
//                        } catch (JSONException e) {
//
//                        }
//                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
//                        messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
//                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
//                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
//                        IYWContact appContact = YWContactFactory.createAPPContact(serviceId + "", imKit.getIMCore().getAppKey());
//                        imKit.getConversationService()
//                                .forwardMsgToContact(appContact
//                                        , message, forwardCallBack);
//                        startActivity(imKit.getChattingActivityIntent(serviceId + ""));
//                        finish();
//                    } else {
//                        ToastUtil.showMessage(((CommonResult) response).info);
//                    }
//                }
//            });
//            sendJsonRequest(userReservationAddRequest);
//        } catch (Exception e) {
//        }
//    }
//
//
//
//    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit, R.id.rl_ordershopname, R.id.rl_ordertime,R.id.play_btn})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.lh_btn_back:
//            case R.id.ll_back:
//                finish();
//                break;
//            case R.id.btn_bespeak_commit:
//                if (noLogin(ModifyBespeakActivity.this))
//                    return;
//                shopname = bespekShopname.getText().toString();
//                ordertime = bespekTime.getText().toString();
//                pmnvSelectDeadline.getText().toString();
//                note = AppUtils.replaceBlank(etCustomerRemark.getText().toString());
//                strdingjin = etDingjin.getText().toString();
//                strremark = etCustomerRemark.getText().toString();
//                if ("请选择商户".equals(shopname)) {
//                    ToastUtil.showMessage("请选择商户");
//                    return;
//                }
//                if (merchantId == 0) {
//                    ToastUtil.showMessage("请选择商户");
//                    return;
//                }
//                if (StringUtils.isEmpty(orderpeople)) {
//                    ToastUtil.showMessage("请选择预约人数");
//                    return;
//                }
//
//                if (StringUtils.isEmpty(ordertime) || "请选择预约时间".equals(ordertime)) {
//                    ToastUtil.showMessage("请选择预约时间");
//                    return;
//                }
//                if (StringUtils.isEmpty(strremark)) {
//                    ToastUtil.showMessage("请输入包厢或房间号");
//                    return;
//                }
//
//                user_reservation_add();
//
//                break;
//
//            case R.id.rl_ordershopname:
//                if (!isFromMerchant) {
//                    Intent intent = new Intent(ModifyBespeakActivity.this, ChooseReservationMerchantActivity.class);
//                    intent.putExtra("userId", serviceId);// 管家id
//                    intent.putExtra("actionFrom", "CreateRoomBespeakActivity");
//                    startActivityForResult(intent, CHOOSEMERCHANT);
//                }
//
//                break;
//            case R.id.rl_ordertime:
//                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
//                        this, bespekTime.getText().toString(), "预约时间必须大于当前时间", 1);
//                dateTimePicKDialog.dateTimePicKDialog(bespekTime);
//                break;
//            case R.id.play_btn:
//
//
//                break;
//
//
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CHOOSEMERCHANT && resultCode == RESULT_OK) {
//            bespekShopname.setText(data.getStringExtra("shopName"));
//            merchantId = data.getIntExtra("merchantId", 0);
//        }
//    }
//
//    final IWxCallback forwardCallBack = new IWxCallback() {
//
//        @Override
//        public void onSuccess(Object... result) {
//            Notification.showToastMsg(ModifyBespeakActivity.this, "forward succeed!");
//        }
//
//        @Override
//        public void onError(int code, String info) {
//            Notification.showToastMsg(ModifyBespeakActivity.this, "forward fail!");
//
//        }
//
//        @Override
//        public void onProgress(int progress) {
//
//        }
//    };
//}
