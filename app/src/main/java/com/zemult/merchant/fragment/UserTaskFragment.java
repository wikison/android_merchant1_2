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
import com.zemult.merchant.activity.mine.RoleDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.aip.common.TaskIndustryRecordListUserRequest;
import com.zemult.merchant.aip.task.TaskIndustryRecordListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.view.ITaskDetailView;
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
 * 用户详情--TA完成的探索
 *
 * @author djy
 * @time 2016/7/30 11:02
 */
public class UserTaskFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, ITaskDetailView {


    public static final String TAG = UserTaskFragment.class.getSimpleName();
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;

    private int titleViewHeight = 48; // 标题栏的高度
    private int tabHeight = 44; // tab的高度
    private int bottomHeight = 44; // 底部的高度

    private Context mContext;
    private Activity mActivity;

    private HomeTaskAdapter mAdapter;

    private int userId;
    private int page = 1;
    private int industryId = -1; // 角色id(-1表示全部角色)
    private TaskIndustryRecordListUserRequest recordListRequest; // TA完成的探索列表

    public TaskIndustryRecordListRequest taskIndustryRecordListRequest;//查看用户的已完成探索记录列表(角色详情界面的)

    private int selectedTaskPos;
    private M_Task selectedTask;

    private static final int REQ_TASK_DETAIL = 0x110;
    private TaskDetailPresenter taskDetailPresenter;

    @Override
    protected void lazyLoad() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_task, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
        getNetworkData(false);
    }


    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
        taskDetailPresenter = new TaskDetailPresenter(listJsonRequest, this);

        // 用户详情--任务记录
        userId = getArguments().getInt(UserDetailActivity.USER_ID, -1);
        // 多重角色--角色扮演--角色详情--任务记录
        industryId = getArguments().getInt(RoleDetailActivity.INTENT_INDUSTRYID, -1);

        if (industryId != -1)
            userId = SlashHelper.userManager().getUserId();
    }

    private void initView() {
        mAdapter = new HomeTaskAdapter(mContext, new ArrayList<M_Task>());
        mAdapter.showTitle(true);
        mAdapter.unshowUser(true);
        if (industryId == -1)
            mAdapter.showRole(true);
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(false);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        // 方案详情
        mAdapter.setOnPlanDetailClickListener(new HomeTaskAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedTaskPos = position;
                selectedTask = mAdapter.getItem(position);

                M_Task news = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, news);
                startActivityForResult(intent, REQ_TASK_DETAIL);
            }
        });
    }

    /**
     * 访问网络接口
     *
     * @param isLoadMore true 加载更多时调用 false 初始化时以及下拉刷新
     */
    public void getNetworkData(final boolean isLoadMore) {
        if (industryId != -1) {
            taskIndustryRecordList(isLoadMore);
        } else {
            task_industry_recordList(isLoadMore);
        }
    }

    /**
     * 查看用户的已完成探索记录列表
     */
    private void taskIndustryRecordList(final boolean isLoadMore) {
        if (taskIndustryRecordListRequest != null) {
            taskIndustryRecordListRequest.cancel();
        }
        TaskIndustryRecordListRequest.Input input = new TaskIndustryRecordListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userId;
        input.industryId = industryId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        taskIndustryRecordListRequest = new TaskIndustryRecordListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskSearchIndustryRecordList) response).status == 1) {
                    setUserTaskList(((APIM_TaskSearchIndustryRecordList) response).recordList,
                            ((APIM_TaskSearchIndustryRecordList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_TaskSearchIndustryRecordList) response).info);
                }
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(taskIndustryRecordListRequest);
    }


    /**
     * TA完成的探索列表
     */
    private void task_industry_recordList(final boolean isLoadMore) {
        if (recordListRequest != null) {
            recordListRequest.cancel();
        }
        TaskIndustryRecordListUserRequest.Input input = new TaskIndustryRecordListUserRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        recordListRequest = new TaskIndustryRecordListUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskSearchIndustryRecordList) response).status == 1) {
                    setUserTaskList(((APIM_TaskSearchIndustryRecordList) response).recordList,
                            ((APIM_TaskSearchIndustryRecordList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_TaskSearchIndustryRecordList) response).info);
                }
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(recordListRequest);
    }

    /**
     * 设置TA的记录
     */
    private void setUserTaskList(List<M_Task> list, int maxpage, boolean isLoadMore) {
        if (mAdapter == null) {
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


    /**
     * =================================处理刷新请求========================================
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQ_TASK_DETAIL) {
                taskDetailPresenter.getTaskDetail(selectedTask.taskIndustryRecordId);
            }
        }
    }

    @Override
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setTaskDetailInfo(M_Task taskIndustryInfo) {
        mAdapter.refreshOneRecord(taskIndustryInfo, selectedTaskPos);
    }
}
