package com.zemult.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.app.BaseActivity;
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
//发赞赏红包
public class SendAppreciateRedActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.sendto_tv)
    TextView sendtoTv;
    @Bind(R.id.money_tv)
    TextView moneyTv;

    UserPayInfoRequest userPayInfoRequest;
    M_Bill m;
    int userPayId;
    private Context mContext;
    private Activity mActivity;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_appreciatered);
    }

    @Override
    public void init() {
        lhTvTitle.setText("赞赏红包");
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        userPayId = getIntent().getIntExtra("billId", 0);
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
                    if(m.type==0){
                        moneyTv.setText("" + (m.rewardMoney == 0 ? "0" : Convert.getMoneyString(m.rewardMoney)));
                    }else{
                        moneyTv.setText("" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                    }
                    if (!TextUtils.isEmpty(m.toUserHead)) {
                        imageManager.loadCircleImage(m.toUserHead, headIv);
                    }
                    sendtoTv.setText("已向" + m.toUserName + "发送赞赏红包");
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }
}
