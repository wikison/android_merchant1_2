package com.zemult.merchant.activity.city.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.city.entity.City;

import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mCities;

    public HotCityGridAdapter(Context context, List<City> cityList) {
        this.mContext = context;
        this.mCities=cityList;
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_city_grid_item, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (RoundTextView) view.findViewById(R.id.lcgi_rtv_city);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getName());
        return view;
    }

    public static class HotCityViewHolder {
        RoundTextView name;
    }
}
