package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/22.
 */

public class GiftAboutDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.iv_user_head_present)
    ImageView ivUserHeadPresent;
    @Bind(R.id.tv_user_name_present)
    TextView tvUserNamePresent;
    @Bind(R.id.tv_trade_time_present)
    TextView tvTradeTimePresent;
    @Bind(R.id.tv_pay_num_present)
    TextView tvPayNumPresent;
    @Bind(R.id.tv_persent_name)
    TextView tvPersentName;
    @Bind(R.id.tv_persent_price)
    TextView tvPersentPrice;
    @Bind(R.id.ll_present)
    LinearLayout llPresent;
    UserPayInfoRequest userPayInfoRequest;
    M_Bill m;
    int userPayId;
    private Context mContext;
    private Activity mActivity;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_giftaboutdetail);
    }

    @Override
    public void init() {
        lhTvTitle.setText("记录详情");
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        userPayId = getIntent().getIntExtra("userPayId", 0);
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
                    tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                    imageManager.loadCircleImage(m.toUserHead,ivUserHeadPresent);
                    tvUserNamePresent.setText(m.toUserName);
                    tvTradeTimePresent.setText(m.createtime);
                    tvPayNumPresent.setText(m.number);
                    tvPersentName.setText(m.presentName+"x1");
                    tvPersentPrice.setText(m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney));
                } else {
                    ToastUtils.show(GiftAboutDetailActivity.this, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }
    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_user_head_present})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_user_head_present:
                break;
        }
    }
}
