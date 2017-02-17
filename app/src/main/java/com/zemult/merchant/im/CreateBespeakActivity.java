package com.zemult.merchant.im;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.zemult.merchant.activity.slash.ChooseReservationMerchantActivity;
import com.zemult.merchant.aip.reservation.UserReservationAddRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimePickDialogUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FNRadioGroup;
import com.zemult.merchant.view.PMNumView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class CreateBespeakActivity extends BaseActivity {

    @Bind(R.id.bespek_time)
    TextView bespekTime;
    @Bind(R.id.bespek_shopname)
    TextView bespekShopname;
    @Bind(R.id.btn_bespeak_commit)
    RoundTextView btnBespeakCommit;
    @Bind(R.id.pmnv_select_deadline)
    PMNumView pmnvSelectDeadline;
    @Bind(R.id.et_bespeak)
    EditText etBespeak;
    @Bind(R.id.et_customername)
    EditText etCustomername;
    @Bind(R.id.et_customerphone)
    EditText etCustomerphone;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.rg_group)
    RadioGroup rgGroup;
    @Bind(R.id.fn_my_service)
    FNRadioGroup fnMyService;


    UserReservationAddRequest userReservationAddRequest;
    int userSex = 0;
    int serviceId;
    String shopname = "", ordertime = "", ordername = "", orderphone = "", orderpeople, note;
    int merchantId;
    M_Merchant m_merchant;
    int CHOOSEMERCHANT = 100;
    boolean isFromMerchant;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeak);

    }

    @Override
    public void init() {
        serviceId = getIntent().getIntExtra("serviceId", 0);
        m_merchant = (M_Merchant) getIntent().getExtras().getSerializable("m_merchant");
        isFromMerchant = m_merchant == null ? false : true;
        if (isFromMerchant) {
            shopname = m_merchant.getName();
            merchantId = m_merchant.getMerchantId();
            initTags(m_merchant.tags);
            bespekShopname.setText(shopname);
            bespekShopname.setCompoundDrawables(null, null, null, null);
        } else {
            bespekShopname.setText("请选择商户");
        }

        etCustomername.setText(SlashHelper.userManager().getUserinfo().getName());
        etCustomerphone.setText(SlashHelper.userManager().getUserinfo().getPhoneNum());
        pmnvSelectDeadline.setMinNum(1);
        pmnvSelectDeadline.setMaxNum(99);
        pmnvSelectDeadline.setDefaultNum(1);
        pmnvSelectDeadline.setText("" + pmnvSelectDeadline.getDefaultNum());
        orderpeople = "" + pmnvSelectDeadline.getDefaultNum();
        pmnvSelectDeadline.setFilter();


        pmnvSelectDeadline.setOnNumChangeListener(new PMNumView.NumChangeListener() {
            @Override
            public void onNumChanged(int num) {
                orderpeople = num + "";
                pmnvSelectDeadline.setDefaultNum(num);
            }
        });
        lhTvTitle.setText("预约服务");
        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_nvshi) {
                    userSex = 1;
                } else {
                    userSex = 0;
                }
            }
        });
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
            input.reservationTime = ordertime + ":00";
            input.num = orderpeople;
            input.userName = ordername;
            input.userPhone = orderphone;
            input.note = note;
            input.userSex = userSex;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            userReservationAddRequest = new UserReservationAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        YWCustomMessageBody messageBody = new YWCustomMessageBody();
                        //定义自定义消息协议，用户可以根据自己的需求完整自定义消息协议，不一定要用JSON格式，这里纯粹是为了演示的需要
                        JSONObject object = new JSONObject();
                        try {
                            object.put("customizeMessageType", "Task");
                            object.put("tasktype", "ORDER");
                            object.put("taskTitle", "[预约-待确认] 预约时间:" + ordertime + "预约地址:" + shopname);
                            object.put("serviceId", serviceId + "");
                            object.put("reservationId", ((CommonResult) response).reservationId);
                        } catch (JSONException e) {

                        }
                        messageBody.setContent(object.toString()); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
                        messageBody.setSummary("[预约单]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                        YWMessage message = YWMessageChannel.createCustomMessage(messageBody);
                        YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                        IYWContact appContact = YWContactFactory.createAPPContact(serviceId + "", imKit.getIMCore().getAppKey());
                        imKit.getConversationService()
                                .forwardMsgToContact(appContact
                                        , message, forwardCallBack);
                        startActivity(imKit.getChattingActivityIntent(serviceId + ""));
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userReservationAddRequest);
        } catch (Exception e) {
        }
    }


    private void initTags(String tags) {
        fnMyService.setChildMargin(0, 24, 24, 0);
        fnMyService.removeAllViews();
        if (!StringUtils.isBlank(tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                fnMyService.setVisibility(View.VISIBLE);

                RadioButton rbTitle = new RadioButton(this);
                rbTitle.setTextSize(15);
                rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                rbTitle.setPadding(0, 0, 8, 0);
                rbTitle.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rbTitle.setTextColor(0xff282828);
//                rbTitle.setText("TA的服务");
                fnMyService.addView(rbTitle);

                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(this);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    fnMyService.addView(rbTag);

                }
            } else {
                fnMyService.setVisibility(View.GONE);
            }
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit, R.id.rl_ordershopname, R.id.rl_ordertime})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                if (noLogin(CreateBespeakActivity.this))
                    return;
                shopname = bespekShopname.getText().toString();
                ordertime = bespekTime.getText().toString();
                pmnvSelectDeadline.getText().toString();
                note = AppUtils.replaceBlank(etBespeak.getText().toString());
                ordername = etCustomername.getText().toString();
                orderphone = etCustomerphone.getText().toString();
                if (StringUtils.isEmpty(shopname)) {
                    return;
                }
                if (StringUtils.isEmpty(orderpeople)) {
                    ToastUtil.showMessage("请选择预约人数");
                    return;
                }

                if (StringUtils.isEmpty(ordertime) || "请选择预约时间".equals(ordertime)) {
                    ToastUtil.showMessage("请选择预约时间");
                    return;
                }
                if (StringUtils.isEmpty(ordername)) {
                    ToastUtil.showMessage("请填写预约人姓名");
                    return;
                }
                if (StringUtils.isEmpty(orderphone)) {
                    ToastUtil.showMessage("请填写预约人电话");
                    return;
                }
                if (!StringMatchUtils.isMobileNO(orderphone)) {
                    ToastUtil.showMessage("请填写正确的预约人电话");
                    return;
                }


                user_reservation_add();

                break;

            case R.id.rl_ordershopname:
                if (!isFromMerchant) {
                    Intent intent = new Intent(CreateBespeakActivity.this, ChooseReservationMerchantActivity.class);
                    intent.putExtra("userId", serviceId);// 管家id
                    intent.putExtra("actionFrom", "CreateBespeakActivity");
                    startActivityForResult(intent, CHOOSEMERCHANT);
                }

                break;
            case R.id.rl_ordertime:
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, bespekTime.getText().toString(), "预约时间必须大于当前时间", 1);
                dateTimePicKDialog.dateTimePicKDialog(bespekTime);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSEMERCHANT && resultCode == RESULT_OK) {
            initTags(data.getStringExtra("tags"));
            bespekShopname.setText(data.getStringExtra("shopName"));
            merchantId = data.getIntExtra("merchantId", 0);
        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(CreateBespeakActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(CreateBespeakActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
