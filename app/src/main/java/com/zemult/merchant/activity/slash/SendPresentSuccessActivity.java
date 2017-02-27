package com.zemult.merchant.activity.slash;

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
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.CreateBespeakActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Present;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/1/22.
 */

public class SendPresentSuccessActivity extends BaseActivity {


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
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_present)
    ImageView ivPresent;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    @Bind(R.id.btn_tks)
    Button btnTks;
    @Bind(R.id.et_tks)
    EditText etTks;
    @Bind(R.id.ll_comf)
    LinearLayout llComf;
    @Bind(R.id.ll_tks)
    LinearLayout llTks;

//    M_Present m_present;

    String tksMsg="",giftName,giftPrice,serviceId,userId;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_send_present_success);

    }

    @Override
    public void init() {
        lhTvTitle.setText("赞赏礼物");
//        m_present = (M_Present) getIntent().getSerializableExtra(SendPresentActivity.PRESENT);
        giftName=getIntent().getStringExtra("giftName");
        giftPrice=getIntent().getStringExtra("giftPrice");
        serviceId=getIntent().getStringExtra("serviceId");
        userId=getIntent().getStringExtra("userId");
        // 礼物名称
        if (!TextUtils.isEmpty(giftName)) {
            // 礼物图片
            if (giftName.contains("兰博基尼")) {
                tvTitle.setText("您已成功赠送" + "一辆豪车!");
                ivPresent.setImageResource(R.mipmap.che_icon);

            } else if (giftName.contains("钻戒")) {
                tvTitle.setText("您已成功赠送" + "一枚钻戒!");
                ivPresent.setImageResource(R.mipmap.zuanjie_icon);

            } else if (giftName.contains("钱包")) {
                tvTitle.setText("您已成功赠送" + "一个钱包!");
                ivPresent.setImageResource(R.mipmap.qianbao_icon);

            } else if (giftName.contains("花")) {
                tvTitle.setText("您已成功赠送" + "一束鲜花!");
                ivPresent.setImageResource(R.mipmap.hua_icon);
            } else {
                tvTitle.setText("您已成功赠送" + "666!");
                ivPresent.setImageResource(R.mipmap.liu_cion);
            }
        }

        if(!StringUtils.isEmpty(serviceId)){
            llTks.setVisibility(View.VISIBLE);
            llComf.setVisibility(View.GONE);
            tvTitle.setText("哇塞，没见过这么豪的土豪，快来收礼物吧~");
        }

        tvName.setText(giftName + " ￥" + giftPrice);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_confirm,R.id.btn_tks})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                this.finish();
                break;
            case R.id.btn_tks:
                if("".equals(etTks.getText().toString())){
                    tksMsg="谢谢土豪~祝你身体棒棒哒，事业顺顺哒~礼物已收到，么么哒~";
                }
                else{
                    tksMsg=etTks.getText().toString();
                }
//                YWCustomMessageBody messageBody = new YWCustomMessageBody();
//                messageBody.setContent(tksMsg); // 用户要发送的自定义消息，SDK不关心具体的格式，比如用户可以发送JSON格式
//                messageBody.setSummary("[送礼物]"); // 可以理解为消息的标题，用于显示会话列表和消息通知栏
                YWMessage message = YWMessageChannel.createTextMessage(tksMsg);
                YWIMKit imKit= LoginSampleHelper.getInstance().getIMKit();
                IYWContact appContact = YWContactFactory.createAPPContact(userId, imKit.getIMCore().getAppKey());
                imKit.getConversationService()
                        .forwardMsgToContact(appContact
                                ,message,forwardCallBack);

                finish();
                break;

        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(SendPresentSuccessActivity.this,"forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(SendPresentSuccessActivity.this,"forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
