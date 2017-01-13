package com.zemult.merchant.activity.slash;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.MoreDiscoverPagerAdapter;
import com.zemult.merchant.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2016/9/8.
 */

//查看更多
public class MoreDiscoverActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tab)
    TabLayout tab;
    @Bind(R.id.mViewPager)
    ViewPager mViewPager;
    private List<String> mTitle = new ArrayList<String>();
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<Integer> flag = new ArrayList<Integer>();
    private MoreDiscoverPagerAdapter moreDiscoverPagerAdapter;
    public static final String INTENT_TASK_INDUSTRY_RECORD_ID = "taskIndustryRecordId";
    public static final String TASKID = "ox34";
    public static final String TYPE = "pushtype";
    private int taskIndustryId;
    private int taskIndustryRecordId, pushtype;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_morediscover);
    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("更多探索");
        //从探索详情里面跳转的
        taskIndustryId = getIntent().getIntExtra(TASKID, -100);
        pushtype = getIntent().getIntExtra(TYPE, -100);

        //从探索完成详情跳转的
        taskIndustryRecordId = getIntent().getIntExtra(INTENT_TASK_INDUSTRY_RECORD_ID, -111);
        mTitle.add("热门探索");
        mTitle.add("最新探索");
        flag.add(1);
        flag.add(0);
        moreDiscoverPagerAdapter = new MoreDiscoverPagerAdapter(getSupportFragmentManager(), flag, mTitle, taskIndustryId, taskIndustryRecordId, pushtype);
        mViewPager.setAdapter(moreDiscoverPagerAdapter);
        tab.setupWithViewPager(mViewPager);

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
