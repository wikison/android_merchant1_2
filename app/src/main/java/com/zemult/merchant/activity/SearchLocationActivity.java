package com.zemult.merchant.activity;

import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.search.SearchLocateAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.AMapUtil;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/3/30.
 */

public class SearchLocationActivity extends BaseActivity {
    @Bind(R.id.search_view)
    SearchView searchView;
    @Bind(R.id.listView)
    ListView listView;

    SearchLocateAdapter searchLocateAdapter;
    PoiSearch.Query poiQuery;
    List<PoiItem> aMapLocationList = new ArrayList<>();
    String cityCode = "0519";


    @Override

    public void setContentView() {
        setContentView(R.layout.activity_search_locate);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

    }

    private void initData() {
        searchLocateAdapter = new SearchLocateAdapter(this, aMapLocationList, false);
        listView.setAdapter(searchLocateAdapter);

        AMapUtil.getInstence().init(this, new AMapUtil.GetAMapListener() {
            @Override
            public void onMapListener(String cityName, AMapLocation aMapLocation, boolean location) {
                if (true) {
                    if (!StringUtils.isBlank(aMapLocation.getCityCode()) && !StringUtils.isBlank((aMapLocation.getRoad()))) {
                        cityCode = aMapLocation.getCityCode();
                        searchList(aMapLocation.getCityCode(), aMapLocation.getRoad());
                    }
                } else {
                    ToastUtil.showMessage("定位失败");
                }
            }
        });
    }

    private void initView() {

    }

    private void initListener() {
        searchView.setOnThinkingClickListener(new SearchView.OnThinkingClickListener() {
            @Override
            public void onThinkingClick(String text) {
                if (!StringUtils.isBlank(text))
                    searchList(cityCode, text);
            }
        });


    }

    private void searchList(String cityCode, String text) {
        if (StringUtils.isBlank(text)) {
            aMapLocationList.clear();
            searchLocateAdapter.notifyDataSetChanged();
        }
        poiQuery = new PoiSearch.Query(text, "", cityCode);
        poiQuery.setPageSize(15);
        poiQuery.setPageNum(2);
        PoiSearch poiSearch = new PoiSearch(this, poiQuery);
        poiSearch.setOnPoiSearchListener(onPoiSearchListener);
        poiSearch.searchPOIAsyn();
    }

    //索引搜索
    PoiSearch.OnPoiSearchListener onPoiSearchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult result, int rCode) {
            if (rCode == 1000) {
                if (result != null && result.getQuery() != null) {// 搜索poi的结果
                    if (result.getQuery().equals(poiQuery)) {// 是否是同一条
                        //     poiResult = result;
                        // 取得搜索到的poiitems有多少页
                        List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                        List<SuggestionCity> suggestionCities = result
                                .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                        aMapLocationList.clear();
                        if (poiItems != null && poiItems.size() > 0) {
                            for (int i = 0; i < poiItems.size(); i++) {
                                aMapLocationList.add(poiItems.get(i));
                            }
                        }
                        searchLocateAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };
}
