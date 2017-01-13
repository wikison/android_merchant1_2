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
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.aip.slash.TaskIndustryRecordListFirstpageRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.FilterData;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.HomeFilterView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * 首页vp的子fragment——任务记录
 *
 * @author djy
 * @time 2016/7/25 10:20
 */
public class HomeTaskFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, ITaskDetailView{
    @Bind(R.id.id_stickynavlayout_innerscrollview)
    SmoothListView smoothListView;

    public static final String TAG = HomeTaskFragment.class.getSimpleName();


    private TaskIndustryRecordListFirstpageRequest firstpageRequest;
    private List<M_Task> taskList = new ArrayList<>();// ListView数据
    private int sex = -1; // 性别 (-1:全部,0:男,1:女)--筛选条件
    private int type = -1; // 类型(-1:全部,0:图文,1:语音,2:投票,3:买单)--筛选条件
    private int friend = 0; // 类型 (0:热门,1:最新)--筛选条件--热门:系统发布,最新:无限制
    private int page = 1;

    private HomeTaskAdapter mAdapter; // 主页数据

    private Context mContext;
    private Activity mActivity;

    private int selectedTaskPos;
    private M_Task selectedTask;
    private HomeFilterView.TaskEnum taskEnum; // 点击FilterView的位置
    private FilterData filterData; // 筛选数据
    private int noDataViewHeight;
    private boolean hasLoaded;

    public static final String CALL_HOMEFRAGMENT_TO_STICK = "call_HomeFragment_stick";
    public static final String TASK_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH = "task_call_HomeFragment_completeRefresh";
    private TaskDetailPresenter taskDetailPresenter;
    private static final int REQ_TASK_DETAIL = 0x110;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded) {
            hasLoaded = true;
            //获取列表数据
            showPd();
            task_industry_recordList_firstpage(false);
        }
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
        registerReceiver(new String[]{Constants.BROCAST_FRESHSLASH});
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();
        taskDetailPresenter = new TaskDetailPresenter(listJsonRequest, this);
        // 筛选数据
        filterData = new FilterData();
        filterData.setFriend(ModelUtil.getFriendData());
        filterData.setSex(ModelUtil.getSexData());
        filterData.setType(ModelUtil.getTypeData());

        noDataViewHeight = DensityUtil.getWindowHeight(mActivity) - DensityUtil.dip2px(mContext, 44 + 40 + 45 + 48); // 标题栏高度 ＋ tab的高度 + FilterView的高度 + 底部选项卡的高度
    }

    private void initView() {
        mAdapter = new HomeTaskAdapter(mContext, taskList);
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

                M_Task task = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivityForResult(intent, REQ_TASK_DETAIL);
            }
        });
        // 用户详情
        mAdapter.setOnUserDetailClickListener(new HomeTaskAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, mAdapter.getItem(position).userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, mAdapter.getItem(position).userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, mAdapter.getItem(position).userSex);
                startActivity(intent);
            }
        });
    }

    //搜索方案列表
    public void task_industry_recordList_firstpage(final boolean isLoadMore) {
        if (firstpageRequest != null) {
            firstpageRequest.cancel();
        }

        TaskIndustryRecordListFirstpageRequest.Input input = new TaskIndustryRecordListFirstpageRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.orderType = -1;
        input.sex = sex;
        input.taskType = type;
        input.friendType = friend;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        firstpageRequest = new TaskIndustryRecordListFirstpageRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EventBus.getDefault().post(TASK_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH);
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskSearchIndustryRecordList) response).status == 1) {
                    fillAdapter(((APIM_TaskSearchIndustryRecordList) response).recordList,
                            ((APIM_TaskSearchIndustryRecordList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_TaskSearchIndustryRecordList) response).info);
                }
                EventBus.getDefault().post(TASK_CALL_HOMEFRAGMENT_TO_COMPLETE_REFRESH);
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(firstpageRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Task> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setLoadMoreEnable(false);
            mAdapter.setData(ModelUtil.getNoDataTaskEntity(noDataViewHeight), false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);

            if(!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public boolean canPtr() {
        View view = smoothListView.getChildAt(smoothListView.getFirstVisiblePosition());
        if (view != null)
            return view.getTop() == 0;
        else
            return false;
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {
        task_industry_recordList_firstpage(true);
    }

    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            if(requestCode == REQ_TASK_DETAIL){
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
