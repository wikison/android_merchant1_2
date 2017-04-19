package com.zemult.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.GiftAboutDetailActivity;
import com.zemult.merchant.activity.slash.SendPresentSuccessActivity;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/2/5.
 */
//收到赞赏
public class ReceiveRedActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.redfrom_tv)
    TextView redfromTv;
    @Bind(R.id.tv_seeMore)
    TextView tvSeeMore;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.btn_tks)
    Button btnTks;
    @Bind(R.id.et_tks)
    EditText etTks;


    UserPayInfoRequest userPayInfoRequest;
    M_Bill m;
    int userPayId;
    private Context mContext;
    private Activity mActivity;
    protected ImageManager mImageManager;
    String userId;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_receivered);
    }

    @Override
    public void init() {
        lhTvTitle.setText("赞赏");
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        userPayId = getIntent().getIntExtra("billId", 0);
        userId = getIntent().getStringExtra("userId");
        tvSeeMore.setText(Html.fromHtml("<u>查看详情</u>"));
        if (userPayId > 0)
            user_pay_info();
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
                    m = ((APIM_UserBillInfo) response).userPayInfo;
                    //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))
//                    if(m.type==0){
//                        moneyTv.setText("" + (m.rewardMoney == 0 ? "0" : Convert.getMoneyString(m.rewardMoney)+ "元"));
//                    }else{
                        moneyTv.setText("" + (m.rewardMoney == 0 ? "0" : Convert.getMoneyString(m.rewardMoney)+ "元"));
//                    }

                    if (!TextUtils.isEmpty(m.userHead)) {
                        imageManager.loadCircleImage(m.userHead, headIv);
                    }
                  //  redfromTv.setText(getIntent().getStringExtra("taskContent")+"");
                   redfromTv.setText(m.userName + "的赞赏\n"+getIntent().getStringExtra("taskContent").replace("的赞赏","的赞赏\n"));
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_right, R.id.lh_btn_rightiamge, R.id.tv_seeMore, R.id.btn_tks})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.lh_btn_right:
                break;
            case R.id.lh_btn_rightiamge:
                break;
            case R.id.btn_tks:
                String tksMsg = "";
                if ("".equals(etTks.getText().toString())) {
                    tksMsg = "谢谢土豪~祝你身体棒棒哒，事业顺顺哒~礼物已收到，么么哒~";
                } else {
                    tksMsg = etTks.getText().toString();
                }
                YWMessage message = YWMessageChannel.createTextMessage(tksMsg);
                YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
                IYWContact appContact = YWContactFactory.createAPPContact(userId, imKit.getIMCore().getAppKey());
                imKit.getConversationService()
                        .forwardMsgToContact(appContact
                                , message, forwardCallBack);

                finish();
                break;
            case R.id.tv_seeMore:
                Intent it = new Intent(mContext, RedRecordDetailActivity.class);
                it.putExtra(RedRecordDetailActivity.COMEFROM,3);
                it.putExtra(RedRecordDetailActivity.INTENT_INFO, m);
                startActivity(it);
                break;
        }
    }

    final IWxCallback forwardCallBack = new IWxCallback() {

        @Override
        public void onSuccess(Object... result) {
            Notification.showToastMsg(ReceiveRedActivity.this, "forward succeed!");
        }

        @Override
        public void onError(int code, String info) {
            Notification.showToastMsg(ReceiveRedActivity.this, "forward fail!");

        }

        @Override
        public void onProgress(int progress) {

        }
    };
}
