package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.FindPayActivity;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.search.SearchUserAdpater;
import com.zemult.merchant.aip.slash.MerchantSaleuserListFanRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListLatestRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * MerchantDetailChildFragment
 * 商家详情fragment
 *
 * @author djy
 * @time 2016/11/9 13:02
 */
public class MerchantDetailChildFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener{
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;

    public static final String TAG = MerchantDetailChildFragment.class.getSimpleName();
    private int titleViewHeight = 48; // 标题栏的高度
    private int tabHeight = 44; // tab的高度

    private Context mContext;
    private Activity mActivity;

    private SearchUserAdpater mAdapter;

    private int merchantId;
    private int page = 1, tabPos;

    private MerchantSaleuserListRequest allRequest;
    private MerchantSaleuserListLatestRequest latestRequest;
    private MerchantSaleuserListFanRequest fanRequest;

    private List<M_Userinfo> listLatest;
    private MerchantDetailActivity merchantDetailActivity;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        merchantDetailActivity = (MerchantDetailActivity) activity;
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_mood, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        showPd();
        getNetworkData(false);
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();

        merchantId = getArguments().getInt(MerchantDetailActivity.MERCHANT_ID, -1);
        tabPos = getArguments().getInt("pos", -1);
    }

    private void initView() {
        mAdapter = new SearchUserAdpater(mContext, new ArrayList<M_Userinfo>());
        mAdapter.setFromMerchant();
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        mAdapter.setOnUserDetailClickListener(new SearchUserAdpater.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                if(mAdapter.getItem(position).getUserId() == SlashHelper.userManager().getUserId())
                    return;
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mAdapter.getItem(position).getUserId());
//                intent.putExtra(UserDetailActivity.MERCHANT_INFO, merchantDetailActivity.merchantInfo);
                intent.putExtra(UserDetailActivity.USER_NAME, mAdapter.getItem(position).getUserName());
                intent.putExtra(UserDetailActivity.USER_HEAD, mAdapter.getItem(position).getUserHead());
                intent.putExtra(UserDetailActivity.USER_SEX, mAdapter.getItem(position).getSex());
                startActivity(intent);
            }
        });

        mAdapter.setOnBuyClickListener(new SearchUserAdpater.OnBuyClickListener() {
            @Override
            public void onBuyClick(int position) {
                if(noLogin(mContext))
                    return;
                Intent intent = new Intent(mContext, FindPayActivity.class);
//                intent.putExtra(FindPayActivity.MERCHANT_INFO, merchantDetailActivity.merchantInfo);
                mAdapter.getItem(position).setName(mAdapter.getItem(position).getUserName());
                mAdapter.getItem(position).setHead(mAdapter.getItem(position).getUserHead());
                intent.putExtra(FindPayActivity.USER_INFO, mAdapter.getItem(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        mAdapter.setOnAllClickListener(new SearchUserAdpater.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                if(noLogin(mContext))
                    return;
                if(mAdapter.getItem(position).getUserId() == SlashHelper.userManager().getUserId())
                    return;

                Intent IMkitintent = LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(mAdapter.getItem(position).getUserId() + "", Urls.APP_KEY);
                startActivity(IMkitintent);
            }
        });
    }


    /**
     * 访问网络接口
     *
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    public void getNetworkData(final boolean isLoadMore) {
        if(tabPos == 2)
            merchant_saleuserList_fan(isLoadMore);
        else{
            if(isLoadMore)
                merchant_saleuserList_all(isLoadMore);
            else {
                merchant_saleuserList_latest();
            }
        }
    }

    /**
     * 商家下的营销经理列表(全部)
     */
    private void merchant_saleuserList_all(final boolean isLoadMore) {
        if (allRequest != null) {
            allRequest.cancel();
        }
        MerchantSaleuserListRequest.Input input = new MerchantSaleuserListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        allRequest = new MerchantSaleuserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    setUserListAll(((APIM_SearchUsersList) response).userList,
                            ((APIM_SearchUsersList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(allRequest);
    }

    /**
     * 商家下的营销经理列表(最近联系)
     */
    private void merchant_saleuserList_latest() {
        if (latestRequest != null) {
            latestRequest.cancel();
        }
        MerchantSaleuserListLatestRequest.Input input = new MerchantSaleuserListLatestRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = 5;
        input.convertJosn();
        latestRequest = new MerchantSaleuserListLatestRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                merchant_saleuserList_all(false);
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    listLatest = ((APIM_SearchUsersList) response).userList;
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                dismissPd();
                merchant_saleuserList_all(false);
            }
        });
        sendJsonRequest(latestRequest);
    }

    /**
     * 商家下的营销经理列表(我的关注)
     */
    private void merchant_saleuserList_fan(final boolean isLoadMore) {
        if (fanRequest != null) {
            fanRequest.cancel();
        }
        MerchantSaleuserListFanRequest.Input input = new MerchantSaleuserListFanRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        fanRequest = new MerchantSaleuserListFanRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    setUserListFanAll(((APIM_SearchUsersList) response).userList,
                            ((APIM_SearchUsersList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(fanRequest);
    }

    /**
     * 全部(我的关注)
     */
    private void setUserListFanAll(List<M_Userinfo> list, int maxpage, boolean isLoadMore) {
        if(mAdapter == null){
            initView();
            initListener();
        }
        if (list.isEmpty()) {
            int height = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleViewHeight + tabHeight);
            mAdapter.setData(ModelUtil.getNoDataUserEntity(height), false);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }

    /**
     * 全部
     */
    private void setUserListAll(List<M_Userinfo> listAll, int maxpage, boolean isLoadMore) {
        if(mAdapter == null){
            initView();
            initListener();
        }
        if (listAll.isEmpty() && listLatest.isEmpty()) {
            int height = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleViewHeight + tabHeight);
            mAdapter.setData(ModelUtil.getNoDataUserEntity(height), false);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            if(isLoadMore)
                mAdapter.setData(listAll, isLoadMore);
            else
                mAdapter.setData(listLatest, listAll);
        }
    }

    @Override
    public void onRefresh() {
        getNetworkData(false);
    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
