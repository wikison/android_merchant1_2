package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.MyTaskFragment;

/**
 * Created by Wikison on 2016/7/28.
 */
public class MyTaskFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] titles = new String[]{"新任务", "待完成", "已完成", "已过期"};
    MyTaskFragment myTaskFragment;

    public MyTaskFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyTaskFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        myTaskFragment= new MyTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        myTaskFragment.setArguments(bundle);
        return myTaskFragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
