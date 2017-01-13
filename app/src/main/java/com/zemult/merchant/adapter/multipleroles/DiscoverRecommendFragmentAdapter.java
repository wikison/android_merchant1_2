package com.zemult.merchant.adapter.multipleroles;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by admin on 2016/9/1.
 */
public class DiscoverRecommendFragmentAdapter extends FragmentStatePagerAdapter {


    private String[] titles = new String[]{"平台推荐", "猜你喜欢","分类"};
    private List<Fragment> fragments;

    public DiscoverRecommendFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public DiscoverRecommendFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return null == fragments ? 0 : fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
