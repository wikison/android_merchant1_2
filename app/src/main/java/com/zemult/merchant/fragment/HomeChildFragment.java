package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewAdapter;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.mvp.presenter.HomePresenter;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;

/**
 * 热门、推荐，我的,邀请
 *
 * @author djy
 * @time 2016/8/31 16:10
 */
public class HomeChildFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, IHomeView {
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;

    public static final String TAG = HomeChildFragment.class.getSimpleName();


    private int page = 1;

    private HomeChildNewAdapter mAdapter; // 主页数据

    private Context mContext;
    private Activity mActivity;

    private int noDataViewHeight;

    public static final String TASK_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH = "call_HomeFragment_completeRefresh";
    private HomePresenter homePresenter;

    private int titleHeight = 44, tabHeight = 56, bottomHeight = 50;

    @Override
    protected void lazyLoad() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_child, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
        onRefresh();
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
        homePresenter = new HomePresenter(listJsonRequest, this);

        noDataViewHeight = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleHeight + tabHeight + bottomHeight); // 标题栏高度 ＋ tab的高度  + 底部选项卡的高度
    }

    private void initView() {
        mAdapter = new HomeChildNewAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, mAdapter.getItem(position-1).merchantId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        homePresenter.merchant_firstpage(-2, page = 1, Constants.ROWS, false);
    }


    @Override
    public void onLoadMore() {
        homePresenter.merchant_firstpage(-2, ++page, Constants.ROWS, true);
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
        if (mAdapter == null) {
            initView();
            initListener();
        }
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
        EventBus.getDefault().post(TASK_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH);
    }

}
