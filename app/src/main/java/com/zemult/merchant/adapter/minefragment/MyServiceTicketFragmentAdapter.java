package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.MyServiceTicketFragment;

/**
 * Created by admin on 2017/4/8.
 */

public class MyServiceTicketFragmentAdapter extends FragmentStatePagerAdapter  {
    private Context context;
    private String[] titles = new String[]{"全部", "已确认", "已支付", "已结束"};
    MyServiceTicketFragment myServiceTicketFragment;

    public MyServiceTicketFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public MyServiceTicketFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        myServiceTicketFragment = new MyServiceTicketFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position",position);
        myServiceTicketFragment.setArguments(bundle);
        return myServiceTicketFragment;
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
