package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.MyOrderFragment;

/**
 * Created by Wikison on 2016/7/28.
 */
public class MyOrderFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] titles = new String[]{"全部", "待支付", "待评价", "已失效"};
    MyOrderFragment myOrderFragment;

    public MyOrderFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyOrderFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        myOrderFragment= new MyOrderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        myOrderFragment.setArguments(bundle);
        return myOrderFragment;
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
