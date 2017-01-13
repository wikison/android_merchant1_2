package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.MyPublishTaskFragment;

/**
 * Created by Wikison on 2016/7/28.
 */
public class MyPublishTaskFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] titles = new String[]{"全部", "进行中", "已结束"};
    MyPublishTaskFragment myPublishTaskFragment;
    int merchantId=-1;

    public MyPublishTaskFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyPublishTaskFragmentAdapter(FragmentManager fm, Context context, int merchantId) {
        super(fm);
        this.context = context;
        this.merchantId = merchantId;
    }

    @Override
    public Fragment getItem(int position) {
        myPublishTaskFragment = new MyPublishTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        bundle.putInt("merchant_id", merchantId);
        myPublishTaskFragment.setArguments(bundle);
        return myPublishTaskFragment;
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
