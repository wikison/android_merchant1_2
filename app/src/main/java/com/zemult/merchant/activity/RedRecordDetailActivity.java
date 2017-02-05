package com.zemult.merchant.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/2/5.
 */
//红包记录
public class RedRecordDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_head)
    RelativeLayout rlHead;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.iv_user_head)
    ImageView ivUserHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_trade_number)
    TextView tvTradeNumber;
    @Bind(R.id.tv_pay_time)
    TextView tvPayTime;
    @Bind(R.id.ll_present)
    LinearLayout llPresent;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_redrecorddetail);
    }

    @Override
    public void init() {

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_user_head})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_user_head:
                break;
        }
    }
}
