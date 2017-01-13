package com.zemult.merchant.adapter.role;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.role.PartyHomeFragment;

/**
 * Created by Wikison on 2016/7/28.
 */
public class PartyHomeFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private String[] titles = new String[]{"推荐活动", "我的活动"};
    PartyHomeFragment partyHomeFragment;

    public PartyHomeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public PartyHomeFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        partyHomeFragment = new PartyHomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        partyHomeFragment.setArguments(bundle);
        return partyHomeFragment;
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
