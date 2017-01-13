package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.MyPublishTaskFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.IntentUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/7/30.
 */
public class MyPublishTaskActivity extends BaseActivity {

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
    @Bind(R.id.vp_my_task)
    ViewPager vpMyTask;

    int merchantId = -1;
    boolean isMerchant = false;
    String sMerchantName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_publish_task);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        merchantId = getIntent().getIntExtra("merchantId", -1);
        sMerchantName = getIntent().getStringExtra("merchantName");
        isMerchant = merchantId > 0 ? true : false;
        if (isMerchant)
            lhTvTitle.setText(getResources().getString(R.string.title_merchant_publish_task));
        else
            lhTvTitle.setText(getResources().getString(R.string.title_my_publish_task));

    }

    private void initView() {
        MyPublishTaskFragmentAdapter adapter = new MyPublishTaskFragmentAdapter(getSupportFragmentManager(), this, merchantId);
        vpMyTask.setAdapter(adapter);
        vpMyTask.setOffscreenPageLimit(3);
        tabs.setupWithViewPager(vpMyTask);
    }

    private void initListener() {
        vpMyTask.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpMyTask.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.lh_btn_right})
    public void Onclick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                this.finish();
                break;
            case R.id.lh_btn_right:
                if (isMerchant) {
                    intent= new Intent(this, MerchantTaskTypeActivity.class);
                    intent.putExtra("merchantId", merchantId);
                    intent.putExtra("merchantName", sMerchantName);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    IntentUtil.start_activity(this, PublishTaskActivity.class);
                }
                break;
        }
    }

}
