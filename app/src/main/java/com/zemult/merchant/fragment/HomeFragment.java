package com.zemult.merchant.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.PoiPickActivity;
import com.zemult.merchant.activity.ScanQrActivity;
import com.zemult.merchant.activity.city.db.DBManager;
import com.zemult.merchant.activity.city.entity.City;
import com.zemult.merchant.activity.city.utils.StringUtils;
import com.zemult.merchant.activity.mine.MyAppointmentActivity;
import com.zemult.merchant.activity.search.SearchHotActivity;
import com.zemult.merchant.activity.slash.PreInviteActivity;
import com.zemult.merchant.activity.slash.NewServicePlanActivity;
import com.zemult.merchant.activity.slash.SelfUserDetailActivity;
import com.zemult.merchant.activity.slash.User2FirstSaleUserRequest;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.AllIndustryAdapter;
import com.zemult.merchant.adapter.slashfrgment.HomeChild1_2_3Adapter;
import com.zemult.merchant.aip.discover.CommonGetadvertListRequest;
import com.zemult.merchant.aip.mine.UserReservationListRequest;
import com.zemult.merchant.aip.slash.CommonFirstpageIndustryListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_CommonGetadvertList;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.mvp.presenter.HomePresenter;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.ColorUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderHomeView;
import com.zemult.merchant.view.SharePopwindow;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 首页
 *
 * @author djy
 * @time 2016/7/22 9:49
 */
public class HomeFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, IHomeView {

    public static final String TAG = HomeFragment.class.getSimpleName();
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.tv_city)
    TextView tvCity;
    @Bind(R.id.ll_city)
    LinearLayout llCity;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.rll_search_bg)
    RoundLinearLayout rllSearchBg;
    @Bind(R.id.ll_topbar)
    LinearLayout llTopbar;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.ivDot)
    ImageView ivDot;
    @Bind(R.id.rv_top)
    RecyclerView rvTop;
    @Bind(R.id.rl_top)
    RelativeLayout rlTop;

    private Context mContext;
    private Activity mActivity;

    private CommonGetadvertListRequest commonGetadvertListRequest;
    private CommonFirstpageIndustryListRequest industryListRequest; // 获取所有行业分类

    public static final int REQ_CITY = 0x110;
    private AMapLocationClient mLocationClient;
    private HomeChild1_2_3Adapter mAdapter;
    private HeaderHomeView headerHomeView;
    private HomePresenter homePresenter;
    private DBManager dbManager;
    private int titleHeight = 44, bottomHeight = 50, adHeight = 194, tabHeight = 135, tvHeight = 38, noDataViewHeight, page = 1;
    private float mTopViewHeight, fraction, headerTopMargin, headerTopHeight;
    private boolean showRedDot;
    private AllIndustryAdapter industryAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int industryId = -1;
    private SharePopwindow sharePopWindow;

    @OnClick({R.id.ll_city, R.id.rl_add})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_city:
                intent = new Intent(mContext, PoiPickActivity.class);
                intent.putExtra("cityName", tvCity.getText().toString());
                startActivityForResult(intent, REQ_CITY);
                break;

            case R.id.rl_add:
                if (noLogin(mContext))
                    return;
                CommonDialog.showPopupWindow(mContext, view, ModelUtil.getHomeRightData(showRedDot), new CommonDialog.PopClickListener() {
                    @Override
                    public void onClick(int pos) {
                        switch (pos) {
                            case 0:
                                Intent intent = new Intent(mContext, MyAppointmentActivity.class);
                                intent.putExtra("fromHome", true);
                                startActivity(intent);
                                break;

                            case 1:
                                boolean cameraPermission = AndPermission.hasPermission(mContext, Manifest.permission.CAMERA);
                                if (cameraPermission) {
                                    intent = new Intent(mActivity, ScanQrActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    startActivity(intent);
                                } else {
                                    requestCameraPermission();
                                }
                                break;

                            case 2:
                                intent = new Intent(mActivity, PreInviteActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                if (sharePopWindow.isShowing())
                                    sharePopWindow.dismiss();
                                else
                                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                                break;
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void lazyLoad() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
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
        dbManager = new DBManager(mContext);
        dbManager.copyDBFile();
        homePresenter = new HomePresenter(listJsonRequest, this);

        noDataViewHeight = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleHeight + bottomHeight + 38);
        registerReceiver(new String[]{Constants.BROCAST_LOGIN});
    }

    private void initView() {
        // 设置其他头部
        headerHomeView = new HeaderHomeView(mActivity);
        headerHomeView.fillView(null, smoothListView);

        // 设置ListView数据
        mAdapter = new HomeChild1_2_3Adapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);
    }


    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int userId = mAdapter.getItem(position - 2).saleUserId;
                Intent intent;
                if (SlashHelper.userManager().getUserId() == userId) {
                    intent = new Intent(mContext, SelfUserDetailActivity.class);
                } else {
                    intent = new Intent(mContext, UserDetailActivity.class);
                }
                intent.putExtra(UserDetailActivity.USER_ID, mAdapter.getItem(position - 2).saleUserId);
                intent.putExtra(UserDetailActivity.MERCHANT_ID, mAdapter.getItem(position - 2).merchantId);
                startActivity(intent);

//                startActivity(new Intent(mContext, NewServicePlanActivity.class));

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
                        headerTopHeight = (float) DensityUtil.dip2px(mContext, adHeight);
                    }

                    if (headerTopMargin == 0)
                        fraction = 0f;
                    else if (headerTopMargin > 0) {
                        if (headerTopMargin < mTopViewHeight)
                            fraction = 1 - (mTopViewHeight - headerTopMargin) / mTopViewHeight;
                        else {
                            fraction = 1f;
                        }
                    } else if (headerTopHeight + headerTopMargin <= mTopViewHeight) {
                        fraction = 1f;
                    } else
                        fraction = 1 - ((headerTopHeight + headerTopMargin) / headerTopHeight);

                    llTopbar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.transparent, R.color.font_black_28));
                    rllSearchBg.getDelegate().setBackgroundColor(ColorUtil.getNewColorByStartEndColor(mContext, fraction, R.color.half_transparent_white, R.color.white));

                    //吸顶效果
                    if (headerTopMargin <= -((float) DensityUtil.dip2px(mContext, adHeight + tabHeight + tvHeight - titleHeight)))
                        rlTop.setVisibility(View.VISIBLE);
                    else
                        rlTop.setVisibility(View.GONE);
                }
            }
        });
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(mActivity, SearchHotActivity.class);
            }
        });
        headerHomeView.setOnIndustryClickListener(new HeaderHomeView.OnIndustryListener() {
            @Override
            public void onIndustryClick(int id) {
                industryAdapter.setSelectedId(id);
                industryId = id;
                onRefresh();
            }

            @Override
            public void onIndustryMove(int pos, int offsetLeft) {
                linearLayoutManager.scrollToPositionWithOffset(pos, offsetLeft);
            }
        });

        sharePopWindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(mContext, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;
                }
            }
        });
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享取消");
        }
    };

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
                        dbManager.insertCity(city);
                        Log.e("onLocationChanged", "city: " + city);
                        Log.e("onLocationChanged", "district: " + district);

                        // 存储到SP中
                        SPUtils.put(mContext, Constants.SP_CITY, city.getNo());
                        SPUtils.put(mContext, Constants.SP_POI, aMapLocation.getPoiName());
                        SPUtils.put(mContext, Constants.SP_CENTER, aMapLocation.getLongitude() + "," + aMapLocation.getLatitude());

                        Constants.CITYID = (String) SPUtils.get(mContext, Constants.SP_CITY, Constants.CITYID);
                        Constants.CENTER = (String) SPUtils.get(mContext, Constants.SP_CENTER, Constants.CENTER);
                        tvCity.setText(aMapLocation.getPoiName());
                        //tvCity.setText(city.getName());

                    } else {
                        dbManager.insertCity(new City(Constants.CITY_NAME, Constants.CITY_PINYIN, Constants.CITYID));
                        tvCity.setText(Constants.CITY_NAME);
                    }
                    onRefresh();
                }
            }
        });
        mLocationClient.startLocation();
    }

    private void getNetworkData() {
        common_getadvertList(); // 获取广告
        common_firstpage_industryList(); // 获取所有行业分类
        if(SlashHelper.userManager().getUserId() != 0)
            user2_first_saleUser();
        onRefresh();
    }

    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_LOGIN.equals(intent.getAction())) {
            if(SlashHelper.userManager().getUserId() != 0)
                user2_first_saleUser();
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

                        List<M_Industry> industryList = ((APIM_CommonGetallindustry) response).industryList;
                        //设置布局管理器
                        linearLayoutManager = new LinearLayoutManager(mContext);
                        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        rvTop.setLayoutManager(linearLayoutManager);
                        //设置适配器
                        industryAdapter = new AllIndustryAdapter(mContext, industryList);
                        rvTop.setAdapter(industryAdapter);

                        industryAdapter.setSelectedId(((APIM_CommonGetallindustry) response).industryList.get(0).id);
                        industryAdapter.setOnItemClickLitener(new AllIndustryAdapter.OnItemClickLitener() {
                            @Override
                            public void onItemClick(int id) {
                                headerHomeView.setSelectedId(id);
                                industryId = id;
                                onRefresh();
                            }
                        });
                        rvTop.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                    int position = linearLayoutManager.findFirstVisibleItemPosition();
                                    View current = linearLayoutManager.findViewByPosition(position);
                                    headerHomeView.onMove(position, current.getLeft());
                                }
                            }
                        });
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_CommonGetallindustry) response).info);
                }
            }
        });
        sendJsonRequest(industryListRequest);
    }


    private User2FirstSaleUserRequest firstSaleUserRequest;

    private void user2_first_saleUser() {
        if (firstSaleUserRequest != null) {
            firstSaleUserRequest.cancel();
        }
        User2FirstSaleUserRequest.Input input = new User2FirstSaleUserRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.center = Constants.CENTER;
        input.convertJosn();

        firstSaleUserRequest = new User2FirstSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    CommonResult result = (CommonResult) response;
                    M_Merchant myMerchant = null;
                    if(result.isSaleUser == 1){
                        myMerchant = new M_Merchant();
                        myMerchant.setName(result.name);
                        myMerchant.setSaleUserId(result.saleUserId);
                        myMerchant.setSaleUserName(result.saleUserName);
                        myMerchant.setSaleUserHead(result.saleUserHead);
                        myMerchant.setSaleUserFanNum(result.saleUserFanNum);
//                        myMerchant.setPerMoney(result.perMoney);
//                        myMerchant.setDistance(result.distance);
//                        myMerchant.setSaleUserExperience(result.saleUserExperience);
//                        myMerchant.setSaleUserSumScore(result.saleUserSumScore);
//                        myMerchant.setReviewstatus(result.reviewstatus);
//                        myMerchant.setPic(result.pic);
//                        myMerchant.setPics(result.pics);
//                        myMerchant.setSaleUserTags(result.saleUserTags);
                    }
                    headerHomeView.setMyInfo(myMerchant);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(firstSaleUserRequest);
    }


    private UserReservationListRequest userReservationListRequest;

    private void userReservationList() {
        if (userReservationListRequest != null) {
            userReservationListRequest.cancel();
        }
        UserReservationListRequest.Input input = new UserReservationListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.state = 1;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();
        userReservationListRequest = new UserReservationListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserReservationList) response).status == 1) {
                    if (((APIM_UserReservationList) response).reservationList != null
                            && !((APIM_UserReservationList) response).reservationList.isEmpty()) {
                        showRedDot = true;
                        ivDot.setVisibility(View.VISIBLE);
                    } else {
                        showRedDot = false;
                        ivDot.setVisibility(View.GONE);
                    }
                }
            }
        });
        sendJsonRequest(userReservationListRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        userReservationList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        homePresenter.merchant_firstpage_List(industryId, page = 1, Constants.ROWS, false);
    }


    @Override
    public void onLoadMore() {
        homePresenter.merchant_firstpage_List(industryId, ++page, Constants.ROWS, true);
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

//            if (!isLoadMore)
//                smoothListView.setSelection(0);
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
                if (!TextUtils.isEmpty(data.getStringExtra("poi_name"))) {
                    tvCity.setText(data.getStringExtra("poi_name"));
                    onRefresh();
                }
            }
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
    private void getLocationYes(List<String> grantedPermissions) {
        location();
    }

    @PermissionNo(101)
    private void getLocationNo(List<String> deniedPermissions) {
        //定位失败, 设置为常州
        City c = new City("常州", "changzhou", "0519");
        SPUtils.put(mContext, Constants.SP_CITY, c.getNo());
        Constants.CITYID = (String) SPUtils.get(mContext, Constants.SP_CITY, "0519");
        tvCity.setText(c.getName());
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//    }

    public void scrollToTop() {
        smoothListView.setSelection(0);
    }

}
