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

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.SearchDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.UserPublishAdapter;
import com.zemult.merchant.aip.task.TaskIndustryListPushRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
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
 * 用户详情--TA发布的探索
 * @author djy
 * @time 2016/8/1 8:42
 */
public class UserPublishFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener{
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;

    public static final String TAG = UserPublishFragment.class.getSimpleName();
    private int titleViewHeight = 48; // 标题栏的高度
    private int tabHeight = 44; // tab的高度
    private int bottomHeight = 44; // 底部的高度

    private Context mContext;
    private Activity mActivity;

    private UserPublishAdapter mAdapter;

    private int userId;
    private int page = 1;

    private boolean hasLoaded = false;
    private TaskIndustryListPushRequest taskIndustryListPushRequest;

    @Override
    protected void lazyLoad() {
        if(!hasLoaded){
            hasLoaded = true;
            showPd();
            getNetworkData(false);
        }
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
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();

        userId = getArguments().getInt(UserDetailActivity.USER_ID, -1);
        if(userId == SlashHelper.userManager().getUserId())
            bottomHeight = 0;
        else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) smoothListView.getLayoutParams();
            params.bottomMargin = DensityUtil.dip2px(mContext, 80);
            smoothListView.setLayoutParams(params);
        }
    }

    private void initView() {
        mAdapter = new UserPublishAdapter(mContext, new ArrayList<M_Task>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Task news = mAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, SearchDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, news);
                startActivity(intent);
            }
        });
    }


    /**
     * 访问网络接口
     *
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    public void getNetworkData(final boolean isLoadMore) {
        getUserNewsList(isLoadMore);
    }

    /**
     * 获取用户的方案列表(角色/场景/时间倒排序)
     */
    private void getUserNewsList(final boolean isLoadMore) {
        if (taskIndustryListPushRequest != null) {
            taskIndustryListPushRequest.cancel();
        }
        TaskIndustryListPushRequest.Input input = new TaskIndustryListPushRequest.Input();

        input.userId = userId;
        input.merchantId = -1;
        input.state = -1;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        taskIndustryListPushRequest = new TaskIndustryListPushRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryListNew) response).status == 1) {
                    setUserPublishList(((APIM_TaskIndustryListNew) response).taskList,
                            ((APIM_TaskIndustryListNew) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_TaskIndustryListNew) response).info);
                }
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryListPushRequest);
    }

    /**
     * 设置TA的记录
     */
    private void setUserPublishList(List<M_Task> list, int maxpage, boolean isLoadMore) {
        if(mAdapter == null){
            initView();
            initListener();
        }
        if (list == null || list.size() == 0) {
            int height = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, titleViewHeight + tabHeight + bottomHeight);
            mAdapter.setData(ModelUtil.getNoDataTaskEntity(height), false, false);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore, page < maxpage);
        }
    }

    @Override
    public void onRefresh() {
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
