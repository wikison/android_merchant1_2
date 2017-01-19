package com.zemult.merchant.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.CityPickerActivity;
import com.zemult.merchant.activity.ScanQrActivity;
import com.zemult.merchant.activity.city.entity.City;
import com.zemult.merchant.activity.city.utils.StringUtils;
import com.zemult.merchant.activity.mine.SaleManageActivity;
import com.zemult.merchant.activity.search.SearchHotActivity;
import com.zemult.merchant.activity.slash.AllChangjingActivity;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewAdapter;
import com.zemult.merchant.aip.discover.CommonGetadvertListRequest;
import com.zemult.merchant.aip.slash.CommonFirstpageIndustryListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_CommonGetadvertList;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import com.zemult.merchant.mvp.presenter.HomePresenter;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.ColorUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderHomeView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;

/**
 * 首页
 *
 * @author djy
 * @time 2016/7/22 9:49
 */
public class HomeFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, IHomeView {

    public static final String TAG = HomeFragment.class.getSimpleName();
    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.ll_city)
    LinearLayout llCity;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.rll_search_bg)
    RoundLinearLayout rllSearchBg;
    //    @Bind(R.id.ll_saomiao)
//    LinearLayout llSaomiao;
    @Bind(R.id.ll_topbar)
    LinearLayout llTopbar;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.iv_maidan)
    ImageView ivMaidan;
    @Bind(R.id.iv_red_dot)
    ImageView ivRedDot;
    @Bind(R.id.rl_maidan)
    RelativeLayout rlMaidan;
    @Bind(R.id.rl_scan)
    RelativeLayout rlScan;

    private Context mContext;
    private Activity mActivity;

    private CommonGetadvertListRequest commonGetadvertListRequest;
    private CommonFirstpageIndustryListRequest industryListRequest; // 获取所有行业分类

    public static final int REQ_CITY = 0x110;
    private AMapLocationClient mLocationClient;
    private HomeChildNewAdapter mAdapter;
    private HeaderHomeView headerHomeView;
    private HomePresenter homePresenter;
    private int titleHeight = 48, bottomHeight = 50, noDataViewHeight, page = 1;
    private float mTopViewHeight, fraction, headerTopMargin;

    @OnClick({R.id.ll_city, R.id.rl_scan, R.id.rl_maidan})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_city:
                intent = new Intent(mContext, CityPickerActivity.class);
                startActivityForResult(intent, REQ_CITY);
                break;
            case R.id.rl_scan:
                boolean cameraPermission = AndPermission.hasPermission(mContext, Manifest.permission.CAMERA);
                if (cameraPermission) {
                    intent = new Intent(mActivity, ScanQrActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    requestCameraPermission();
                }
                break;
            case R.id.rl_maidan:
                // TODO: 2017/1/19
                break;
        }
    }

    @Override
    protected void lazyLoad() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
        initLocation();
        getNetworkData();
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
        homePresenter = new HomePresenter(listJsonRequest, this);

        noDataViewHeight = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleHeight + bottomHeight);
        registerReceiver(new String[]{Constants.BROCAST_LOGIN});
    }

    private void initView() {
        // 设置其他头部
        headerHomeView = new HeaderHomeView(mActivity);
        headerHomeView.fillView(null, smoothListView);

        // 设置ListView数据
        mAdapter = new HomeChildNewAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);
    }


    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, mAdapter.getItem(position - 2).merchantId);
                startActivity(intent);
            }
        });
        smoothListView.setOnScrollListener(new SmoothListView.OnSmoothScrollListener() {
            @Override
            public void onSmoothScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (smoothListView.getChildAt(1 - firstVisibleItem) != null) {
                    headerTopMargin = (float) smoothListView.getChildAt(1 - firstVisibleItem).getTop();

                    if (mTopViewHeight == 0) {
                        mTopViewHeight = (float) llTopbar.getLayoutParams().height;
                    }
                    if (headerTopMargin == 0)
                        fraction = 0f;
                    else if (headerTopMargin > 0)
                        if (headerTopMargin < mTopViewHeight)
                            fraction = 1 - (mTopViewHeight - headerTopMargin) / mTopViewHeight;
                        else
                            fraction = 1f;
                    else if (-headerTopMargin >= mTopViewHeight) {
                        fraction = 1f;
                    } else
                        fraction = 1 - (headerTopMargin + mTopViewHeight) / mTopViewHeight;

                    llTopbar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.font_black_28));
                }
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(mActivity, SearchHotActivity.class);
            }
        });
        headerHomeView.setOnHeaderClickListener(new HeaderHomeView.OnHeaderClickListener() {
            @Override
            public void onTabClick(int industryId, String industryName) {
                Intent intent = new Intent(mContext, AllChangjingActivity.class);
                intent.putExtra(AllChangjingActivity.INTENT_INDUSTYR_ID, industryId);
                intent.putExtra(AllChangjingActivity.INTENT_INDUSTYR_NAME, industryName);
                startActivity(intent);
            }
        });
    }

    private void initLocation() {
        boolean bLocationPermission = AndPermission.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
        if (bLocationPermission) {
            location();
        } else {
            requestLocationPermission();
        }
    }

    private void location() {
        mLocationClient = new AMapLocationClient(mContext);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        String location = aMapLocation.getCity();
                        String district = aMapLocation.getDistrict();
                        String name = StringUtils.extractLocation(location, district);
                        String no = aMapLocation.getCityCode();
                        City city = new City(name, "", no);
                        Log.e("onLocationChanged", "city: " + city);
                        Log.e("onLocationChanged", "district: " + district);

                        // 存储到SP中
                        SPUtils.put(mContext, Constants.SP_CITY, city.getNo());
                        SPUtils.put(mContext, Constants.SP_CENTER, aMapLocation.getLongitude() + "," + aMapLocation.getLatitude());

                        Constants.CENTER = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");
                        tvCity.setText(city.getName());

                    } else {
                        ToastUtil.showMessage("定位失败");
                    }
                }
            }
        });
        mLocationClient.startLocation();
    }

    private void getNetworkData() {
        common_getadvertList(); // 获取广告
        common_firstpage_industryList(); // 获取所有行业分类
        onRefresh();
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_LOGIN.equals(intent.getAction())) {
            onRefresh();
        }
    }

    //获取  广告列表
    private void common_getadvertList() {
        if (commonGetadvertListRequest != null) {
            commonGetadvertListRequest.cancel();
        }
        CommonGetadvertListRequest.Input input = new CommonGetadvertListRequest.Input();
        input.page = 1;//页面编号(-1:表示全部;0:app开启页1:首页广告位2:我的斜杠3:我是商家)

        input.convertJosn();
        commonGetadvertListRequest = new CommonGetadvertListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonGetadvertList) response).status == 1) {

                    headerHomeView.setAd(((APIM_CommonGetadvertList) response).advertList);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_CommonGetadvertList) response).info);
                }
            }
        });
        sendJsonRequest(commonGetadvertListRequest);
    }

    public void common_firstpage_industryList() {
        if (industryListRequest != null) {
            industryListRequest.cancel();
        }

        industryListRequest = new CommonFirstpageIndustryListRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonGetallindustry) response).status == 1) {
                    if (!((APIM_CommonGetallindustry) response).industryList.isEmpty()) {
                        headerHomeView.setVpIndustrys(((APIM_CommonGetallindustry) response).industryList);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_CommonGetallindustry) response).info);
                }
            }
        });
        sendJsonRequest(industryListRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        homePresenter.merchant_firstpage_List(-2, page = 1, Constants.ROWS, false);
    }


    @Override
    public void onLoadMore() {
        homePresenter.merchant_firstpage_List(-2, ++page, Constants.ROWS, true);
    }

    @Override
    public void hideProgressDialog() {
        dismissPd();
    }

    @Override
    public void showError(String error) {
        if ("用户不存在".equals(error)) {
            ToastUtils.show(mContext, "请先登录");
        } else {
            ToastUtils.show(mContext, error);
        }

    }

    @Override
    public void setMerchantList(List<M_Merchant> list, boolean isLoadMore, int maxpage) {
        if (list == null || list.size() == 0) {
            smoothListView.setLoadMoreEnable(false);
            mAdapter.setData(ModelUtil.getNoDataMerchantEntity(noDataViewHeight), false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);

            if (!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void stopRefreshOrLoad() {
        smoothListView.stopLoadMore();
        smoothListView.stopRefresh();
    }

    public enum HomeEnum {
        TASK, MOOD
    }

    /**
     * =================================================处理刷新请求===========================================================================
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQ_CITY) {
                if (!TextUtils.isEmpty(data.getStringExtra("city_name"))) {
                    tvCity.setText(data.getStringExtra("city_name"));
                    onRefresh();
                }
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        //从商户管理返回刷新
        if (s.equals(SaleManageActivity.REFLASH)) {
            onRefresh();
        }

    }


    private void requestCameraPermission() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.CAMERA)
                .send();
    }

    @PermissionYes(100)
    private void getCameraYes() {
        Intent intent = new Intent(mActivity, ScanQrActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @PermissionNo(100)
    private void getCameraNo() {
        Toast.makeText(getContext(), "无摄像头权限, 请前往设置中心开启摄像头权限", Toast.LENGTH_SHORT).show();
    }

    private void requestLocationPermission() {
        AndPermission.with(this)
                .requestCode(101)
                .permission(Manifest.permission.ACCESS_FINE_LOCATION)
                .send();
    }

    @PermissionYes(101)
    private void getLocationYes() {
        location();
    }

    @PermissionNo(101)
    private void getLocationNo() {
        //定位失败, 设置为常州
        Toast.makeText(getContext(), "定位失败", Toast.LENGTH_SHORT).show();
        City c = new City("常州", "changzhou", "0519");
        SPUtils.put(mContext, Constants.SP_CITY, c.getNo());
        Constants.CITYID = (String) SPUtils.get(mContext, Constants.SP_CITY, "0519");
        tvCity.setText(c.getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
