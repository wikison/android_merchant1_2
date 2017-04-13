package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.ServiceTicketListFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServiceTicketListActivity extends BaseActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tab)
    TabLayout tab;
    @Bind(R.id.vp_my_sertick)
    ViewPager vpMySertick;
    int page_position = -1;
    int saleUserId,merchantId;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_service_ticket_list);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        lhTvTitle.setText("服务单记录");
        page_position = getIntent().getIntExtra("page_position", 0);
        saleUserId=getIntent().getIntExtra("saleUserId",0);
        merchantId=getIntent().getIntExtra("merchantId",0);
    }

    private void initView() {
        ServiceTicketListFragmentAdapter adapter = new ServiceTicketListFragmentAdapter(getSupportFragmentManager(),saleUserId,merchantId);
        vpMySertick.setAdapter(adapter);
        vpMySertick.setOffscreenPageLimit(4);
        vpMySertick.setCurrentItem(page_position);
        tab.setupWithViewPager(vpMySertick);
    }

    private void initListener() {
        vpMySertick.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                vpMySertick.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
