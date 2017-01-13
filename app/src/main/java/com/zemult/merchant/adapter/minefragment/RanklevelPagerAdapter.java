package com.zemult.merchant.adapter.minefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zemult.merchant.fragment.LevelRankFragment;

import java.util.List;

/**
 * Created by admin on 2016/7/23.
 */
public class RanklevelPagerAdapter extends FragmentPagerAdapter {
    private List<String> title;

    private LevelRankFragment levelRankFragment;
    private List<Integer> flag;



    public RanklevelPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public RanklevelPagerAdapter(FragmentManager fm,  List<String> title,List<Integer> flag) {
        super(fm);
        this.flag = flag;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {

        levelRankFragment = new LevelRankFragment();//viewpager子页面
        Bundle bundle = new Bundle();
        bundle.putInt("data", flag.get(position));
        levelRankFragment.setArguments(bundle);
        return levelRankFragment;

    }

    @Override
    public int getCount() {
        return flag.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
