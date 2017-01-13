package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.RanklevelPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/7/23.
 */
public class LevelRankingActivity extends FragmentActivity {
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
    private List<Integer> flag=new ArrayList<Integer>();

    private RanklevelPagerAdapter ranklevelPagerAdapter;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levelrank_activity);
        ButterKnife.bind(this);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("等级排行榜");
        mTitle.add("总排行榜");
        mTitle.add("好友排行榜");


        flag.add(0);
        flag.add(1);
        ranklevelPagerAdapter = new RanklevelPagerAdapter(getSupportFragmentManager(),mTitle,flag);
        mViewPager.setAdapter(ranklevelPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        tab.setupWithViewPager(mViewPager);

        tab.setTabsFromPagerAdapter(ranklevelPagerAdapter);

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
