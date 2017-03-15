package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyInviteActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.myinvite_lv)
    SmoothListView myinviteLv;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_invite);
    }

    @Override
    public void init() {
        lhTvTitle.setText("我的预邀");
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
                break;
            case R.id.ll_right:
                break;
        }
    }

}
