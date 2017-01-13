package com.zemult.merchant.adapter.createroleadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.zemult.merchant.fragment.CreateRoleFragment;
import com.zemult.merchant.bean.IndusPreferItem;

import java.util.ArrayList;

/**
 * Created by admin on 2016/4/15.
 */
public class CreateRolePagerAdapter extends FragmentStatePagerAdapter {
    //viewpage的页面切换adapter

    private Context context;
    private ArrayList<IndusPreferItem> userChannelList = new ArrayList<IndusPreferItem>();

    private CreateRoleFragment createRoleFragment;

    public CreateRolePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public CreateRolePagerAdapter(FragmentManager fm, Context context, ArrayList<IndusPreferItem> userChannelList) {
        super(fm);
        this.context = context;
        this.userChannelList = userChannelList;
    }


    @Override
    public int getCount() {
        return userChannelList.size();
    }

    @Override
    public Fragment getItem(int position) {

        createRoleFragment = new CreateRoleFragment();//viewpager子页面
        Bundle bundle = new Bundle();
        bundle.putInt("data", userChannelList.get(position).getId());
        createRoleFragment.setArguments(bundle);
        return createRoleFragment;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return userChannelList.get(position).getName();
    }
}

