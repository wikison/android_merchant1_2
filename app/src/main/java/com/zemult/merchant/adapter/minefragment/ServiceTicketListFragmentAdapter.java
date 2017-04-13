package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zemult.merchant.fragment.ServiceTicketListFragment;

/**
 * Created by admin on 2017/4/13.
 */

public class ServiceTicketListFragmentAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private String[] titles = new String[]{"全部", "待确认", "待买单", "已结束"};
    ServiceTicketListFragment serviceTicketListFragment;
    int saleUserId,merchantId;

    public ServiceTicketListFragmentAdapter(FragmentManager fm, int saleUserId, int merchantId) {
        super(fm);
        this.saleUserId = saleUserId;
        this.merchantId = merchantId;
    }

    public ServiceTicketListFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        serviceTicketListFragment = new ServiceTicketListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("page_position", position);
        bundle.putInt("saleUserId", saleUserId);
        bundle.putInt("merchantId", merchantId);
        serviceTicketListFragment.setArguments(bundle);
        return serviceTicketListFragment;
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
