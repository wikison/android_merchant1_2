package com.zemult.merchant.activity.city.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.city.entity.City;

import java.util.ArrayList;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mCities;

    public HotCityGridAdapter(Context context) {
        this.mContext = context;
        mCities = new ArrayList<>();
        mCities.add(new City("北京", "beijing", "010"));
        mCities.add(new City("上海", "shanghai", "021"));
        mCities.add(new City("广州", "shanghai", "020"));
        mCities.add(new City("深圳", "shanghai", "0755"));
        mCities.add(new City("杭州", "shanghai", "0571"));
        mCities.add(new City("南京", "shanghai", "025"));
        mCities.add(new City("天津", "shanghai", "022"));
        mCities.add(new City("武汉", "shanghai", "027"));
        mCities.add(new City("重庆", "shanghai", "023"));
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
