package com.zemult.merchant.adapter.minefragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.MyProInviteFragment;

/**
 * Created by admin on 2017/5/11.
 */

public class MyProInviteFragmentAdapter extends FragmentStatePagerAdapter {

    MyProInviteFragment myProInviteFragment;
    String[] titles = new String[]{"我生成的邀请函", "我收到的邀请函"};

    public MyProInviteFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        myProInviteFragment = new MyProInviteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        myProInviteFragment.setArguments(bundle);
        return myProInviteFragment;
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
