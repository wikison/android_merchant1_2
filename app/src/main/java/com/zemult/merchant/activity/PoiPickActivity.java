package com.zemult.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.search.SearchLocateAdapter;
import com.zemult.merchant.util.AMapUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/3/30.
 */

public class PoiPickActivity extends Activity {

    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.rll_search_bg)
    RoundLinearLayout rllSearchBg;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.mapView)
    MapView mapView;
    @Bind(R.id.iv_locate)
    ImageView ivLocate;
    @Bind(R.id.listView)
    ListView listView;

    Context mContext;
    Activity mActivity;

    private AMap aMap;
    private Marker marker;
    private UiSettings mUiSettings;

    List<PoiItem> aMapLocationList = new ArrayList<>();
    SearchLocateAdapter searchLocateAdapter;

    PoiSearch.Query poiQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ButterKnife.bind(this);

        mapView.onCreate(savedInstanceState);

        init();
    }

    public void setContentView() {
        setContentView(R.layout.activity_poi_pick);
        ButterKnife.bind(this);
    }

    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mActivity = this;

        searchLocateAdapter = new SearchLocateAdapter(this, aMapLocationList, true);
        listView.setAdapter(searchLocateAdapter);
    }

    private void initView() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings
                    .setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        }
        aMap.setTrafficEnabled(false);// 显示实时交通状况
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);

        //是否显示地图中放大缩小按钮
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层

        location();
    }

    private void initListener() {

        //监测地图画面的移动
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                //     ToastUtil.showToast(getApplicationContext(), cameraPosition.target.longitude+"当前地图中心位置: "+cameraPosition.target.latitude);
                addMark(cameraPosition.target.latitude, cameraPosition.target.longitude);
                latSearchList(cameraPosition.target.latitude, cameraPosition.target.longitude);

            }

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }
        });

        //不设置触摸地图的时候会报错
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

    }

    private void location() {
        AMapUtil.getInstence().init(this, new AMapUtil.GetAMapListener() {
            @Override
            public void onMapListener(String cityName, AMapLocation aMapLocation, boolean location) {
                if (true) {
                    if (!StringUtils.isBlank(aMapLocation.getCityCode())) {
                        searchList(aMapLocation.getCityCode(), aMapLocation.getRoad());
                        //把地图移动到定位地点
                        moveMapCamera(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        addLocationMark(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    }
                } else {
                    ToastUtil.showMessage("定位失败");
                }
            }
        });
    }

    //把地图画面移动到定位地点
    private void moveMapCamera(double latitude, double longitude) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));
    }

    private void addMark(double latitude, double longitude) {
        if (marker == null) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.dingwei_blue)))
                    .draggable(true));
        } else {
            marker.setPosition(new LatLng(latitude, longitude));
            aMap.invalidate();
        }

    }

    private void addLocationMark(double latitude, double longitude) {
        if (marker == null) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.mipmap.wodeweizhi)))
                    .draggable(true));
        } else {
            marker.setPosition(new LatLng(latitude, longitude));
            aMap.invalidate();
        }

    }

    //poi搜索
    private void searchList(String cityCode, String road) {
        if (StringUtils.isBlank(road)) {
            aMapLocationList.clear();
            searchLocateAdapter.notifyDataSetChanged();
        }
        poiQuery = new PoiSearch.Query(road, "", cityCode);
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

    private void latSearchList(double latitude, double longitude) {
        //设置周边搜索的中心点以及半径
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        //地点范围50米
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 50, GeocodeSearch.AMAP);

        geocodeSearch.getFromLocationAsyn(query);

        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        searchList(result.getRegeocodeAddress().getCityCode(), result.getRegeocodeAddress().getTownship());
                    }
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });

    }

    @OnClick({R.id.iv_locate, R.id.et_search, R.id.tv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_locate:
                location();
                break;
            case R.id.et_search:
                IntentUtil.start_activity(mActivity, SearchLocationActivity.class);
                break;
            case R.id.tv_cancel:
                this.finish();
                break;
        }
    }
}
