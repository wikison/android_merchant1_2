package com.zemult.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.GiftAboutDetailActivity;
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
//收到赞赏红包
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
    @Bind(R.id.rl_head)
    RelativeLayout rlHead;
    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.redfrom_tv)
    TextView redfromTv;
    @Bind(R.id.tv_seeMore)
    TextView tvSeeMore;
    @Bind(R.id.head_iv)
    ImageView headIv;
    UserPayInfoRequest userPayInfoRequest;
    M_Bill m;
    int userPayId;
    private Context mContext;
    private Activity mActivity;
    protected ImageManager mImageManager;



    @Override
    public void setContentView() {
        setContentView(R.layout.activity_receivered);
    }

    @Override
    public void init() {
        lhTvTitle.setText("赞赏红包");
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
                    moneyTv.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                    imageManager.loadCircleImage(m.userHead,headIv);
                    redfromTv.setText(m.userName+"的红包");
                } else {
                    ToastUtils.show(mActivity, ((APIM_UserBillInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayInfoRequest);
    }




    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_btn_right, R.id.lh_btn_rightiamge, R.id.tv_seeMore})
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
            case R.id.tv_seeMore:
                Intent it = new Intent(mContext,RedRecordDetailActivity.class);
                it.putExtra(RedRecordDetailActivity.INTENT_INFO,m);
                startActivity(it);
                break;
        }
    }


}
