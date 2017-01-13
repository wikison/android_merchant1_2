package com.zemult.merchant.activity.role;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.role.PartyHomeFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.IntentUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class PartyHomeActivity extends BaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    ImageView lhBtnRight;
    @Bind(R.id.tab)
    TabLayout tabs;
    @Bind(R.id.vp_party)
    ViewPager vpParty;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_party_home);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {

        lhTvTitle.setText("角色聚会活动");

    }

    private void initView() {
        PartyHomeFragmentAdapter adapter = new PartyHomeFragmentAdapter(getSupportFragmentManager(), this);
        vpParty.setAdapter(adapter);
        vpParty.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(vpParty);
    }

    private void initListener() {
        vpParty.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpParty.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.lh_btn_right})
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                this.finish();
                break;
            case R.id.lh_btn_right:
                IntentUtil.start_activity(PartyHomeActivity.this, CreatPartyActivity.class);
                break;
        }
    }
}
