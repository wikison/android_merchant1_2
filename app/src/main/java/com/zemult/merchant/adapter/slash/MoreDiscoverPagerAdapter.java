package com.zemult.merchant.adapter.slash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zemult.merchant.fragment.MoreDiscoverFragment;

import java.util.List;

/**
 * Created by admin on 2016/9/8.
 */
public class MoreDiscoverPagerAdapter extends FragmentPagerAdapter {

    private List<String> title;
    private MoreDiscoverFragment moreDiscoverFragment;

    private List<Integer> flag;
    private int taskIndustryId, pushtype;
    private int taskIndustryRecordId;

    public MoreDiscoverPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public MoreDiscoverPagerAdapter(FragmentManager fm, List<Integer> flag, List<String> title, int taskIndustryId, int taskIndustryRecordId, int pushtype) {
        super(fm);
        this.flag = flag;
        this.title = title;
        this.taskIndustryId = taskIndustryId;
        this.taskIndustryRecordId = taskIndustryRecordId;
        this.pushtype = pushtype;
    }

    @Override
    public Fragment getItem(int position) {

        moreDiscoverFragment = new MoreDiscoverFragment(taskIndustryId, taskIndustryRecordId, pushtype);
        Bundle bundle = new Bundle();
        bundle.putInt("data", flag.get(position));
        moreDiscoverFragment.setArguments(bundle);
        return moreDiscoverFragment;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
