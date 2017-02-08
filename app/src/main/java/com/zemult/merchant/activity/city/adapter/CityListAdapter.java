package com.zemult.merchant.activity.city.adapter;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.city.entity.City;
import com.zemult.merchant.activity.city.entity.LocateState;
import com.zemult.merchant.activity.city.utils.PinyinUtils;
import com.zemult.merchant.activity.city.view.WrapHeightGridView;
import com.zemult.merchant.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class CityListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 4;

    private Context mContext;
    private LayoutInflater inflater;
    private List<City> mCities, recentList;
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private OnCityClickListener onCityClickListener;
    private int locateState = LocateState.LOCATING;
    private City locatedCity;

    public CityListAdapter(Context mContext, List<City> mCities, List<City> recentList) {
        this.mContext = mContext;
        this.mCities = mCities;
        this.recentList = recentList;
        this.inflater = LayoutInflater.from(mContext);
        if (mCities == null) {
            mCities = new ArrayList<>();
        }
        mCities.add(0, new City("定位", "0", ""));
        mCities.add(1, new City("最近", "1", ""));
        mCities.add(2, new City("热门", "2", ""));
        int size = mCities.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++) {
            //当前城市拼音首字母
            String currentLetter = PinyinUtils.getFirstLetter(mCities.get(index).getPinyin());
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinyinUtils.getFirstLetter(mCities.get(index - 1).getPinyin()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, City city) {
        this.locateState = state;
        this.locatedCity = city;
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position < VIEW_TYPE_COUNT - 1 ? position : VIEW_TYPE_COUNT - 1;
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
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:     //定位
                view = inflater.inflate(R.layout.view_locate_city, parent, false);
                ViewGroup container = (ViewGroup) view.findViewById(R.id.layout_locate);
                TextView state = (TextView) view.findViewById(R.id.tv_located_city);
                switch (locateState) {
                    case LocateState.LOCATING:
                        state.setText(mContext.getString(R.string.locating));
                        break;
                    case LocateState.FAILED:
                        state.setText(R.string.located_failed);
                        break;
                    case LocateState.SUCCESS:
                        state.setText(locatedCity.getName());
                        break;
                }
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateState == LocateState.FAILED) {
                            if (AndPermission.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                //重新定位
                                if (onCityClickListener != null) {
                                    onCityClickListener.onLocateClick();
                                }
                            } else {
                                ToastUtil.showMessage("请去设置定位权限");
                            }

                        } else if (locateState == LocateState.SUCCESS) {
                            //返回定位城市
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(locatedCity);
                            }
                        }
                    }
                });
                break;
            case 1:   //最近访问城市
                view = inflater.inflate(R.layout.view_hot_city, parent, false);
                TextView tvRecentCategory = (TextView) view.findViewById(R.id.llc_tv_label);
                WrapHeightGridView wgvRecent = (WrapHeightGridView) view.findViewById(R.id.gridview_hot_city);
                tvRecentCategory.setText("最近访问城市");
                final HotCityGridAdapter hcgaRecent = new HotCityGridAdapter(mContext, recentList);
                wgvRecent.setAdapter(hcgaRecent);
                wgvRecent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onCityClickListener != null) {
                            onCityClickListener.onCityClick(hcgaRecent.getItem(position));
                        }
                    }
                });
                break;
            case 2:     //热门
                view = inflater.inflate(R.layout.view_hot_city, parent, false);
                TextView tvHotCategory = (TextView) view.findViewById(R.id.llc_tv_label);
                tvHotCategory.setText("热门城市");
                List<City> hotList = new ArrayList<>();
                hotList.add(new City("北京", "beijing", "010"));
                hotList.add(new City("上海", "shanghai", "021"));
                hotList.add(new City("广州", "shanghai", "020"));
                hotList.add(new City("深圳", "shanghai", "0755"));
                hotList.add(new City("杭州", "shanghai", "0571"));
                hotList.add(new City("南京", "shanghai", "025"));
                hotList.add(new City("天津", "shanghai", "022"));
                hotList.add(new City("武汉", "shanghai", "027"));
                hotList.add(new City("重庆", "shanghai", "023"));

                WrapHeightGridView gridView = (WrapHeightGridView) view.findViewById(R.id.gridview_hot_city);
                final HotCityGridAdapter hotCityGridAdapter = new HotCityGridAdapter(mContext, hotList);
                gridView.setAdapter(hotCityGridAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onCityClickListener != null) {
                            onCityClickListener.onCityClick(hotCityGridAdapter.getItem(position));
                        }
                    }
                });
                break;
            case 3:     //所有
                if (view == null) {
                    view = inflater.inflate(R.layout.layout_city_list_item, parent, false);
                    holder = new CityViewHolder();
                    holder.letter = (TextView) view.findViewById(R.id.lcii_tv_alpha);
                    holder.name = (TextView) view.findViewById(R.id.lcii_tv_city);
                    view.setTag(holder);
                } else {
                    holder = (CityViewHolder) view.getTag();
                }
                if (position >= 1) {
                    final City city = mCities.get(position);
                    holder.name.setText(city.getName());
                    String currentLetter = PinyinUtils.getFirstLetter(mCities.get(position).getPinyin());
                    String previousLetter = position >= 1 ? PinyinUtils.getFirstLetter(mCities.get(position - 1).getPinyin()) : "";
                    if (!TextUtils.equals(currentLetter, previousLetter)) {
                        holder.letter.setVisibility(View.VISIBLE);
                        holder.letter.setText(currentLetter);
                    } else {
                        holder.letter.setVisibility(View.GONE);
                    }
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(city);
                            }
                        }
                    });
                }
                break;
        }
        return view;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(City city);

        void onLocateClick();
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name;
    }
}
