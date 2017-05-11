package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MyProInviteFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyProInviteActivity extends BaseActivity {

    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tab)
    TabLayout tab;
    @Bind(R.id.vp_my_proinvite)
    ViewPager vpMyProinvite;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_pro_invite);
    }

    @Override
    public void init() {

        initData();
        initView();
        initListener();
    }

    private void initData() {
        lhTvTitle.setText("我的邀请函");
    }

    private void initView() {
        MyProInviteFragmentAdapter adapter = new MyProInviteFragmentAdapter(getSupportFragmentManager());
        vpMyProinvite.setAdapter(adapter);
        vpMyProinvite.setOffscreenPageLimit(2);
        tab.setupWithViewPager(vpMyProinvite);
    }


    private void initListener() {


    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;

        }
    }

}
