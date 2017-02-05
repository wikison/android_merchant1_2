package com.zemult.merchant.activity.mine;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.MyOrderFragmentAdapter;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 我的订单
 *
 * @author Wikison
 * @time 2016/12/29 16:40
 */
public class MyOrderActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.tab)
    TabLayout tabs;
    @Bind(R.id.vp_my_order)
    ViewPager vpMyOrder;
    int page_position = -1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_order);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        lhTvTitle.setText("我的消费单");
        page_position = getIntent().getIntExtra("page_position", 0);
    }

    private void initView() {
        MyOrderFragmentAdapter adapter = new MyOrderFragmentAdapter(getSupportFragmentManager());
        vpMyOrder.setAdapter(adapter);
        vpMyOrder.setOffscreenPageLimit(4);
        vpMyOrder.setCurrentItem(page_position);
        tabs.setupWithViewPager(vpMyOrder);
    }


    private void initListener() {
        vpMyOrder.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpMyOrder.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back})
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                this.finish();
                break;
        }
    }
}