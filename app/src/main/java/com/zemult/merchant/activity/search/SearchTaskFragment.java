package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.SearchDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.activity.slash.dotask.NewDoTaskPicActivity;
import com.zemult.merchant.activity.slash.dotask.NewDoTaskVoteActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeChildAdapter;
import com.zemult.merchant.aip.slash.TaskIndustryListSearch;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 搜索vp的子fragment——任务记录
 *
 * @author djy
 * @time 2016/8/3 10:00
 */
public class SearchTaskFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, ITaskDetailView{
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    public static final String TAG = SearchTaskFragment.class.getSimpleName();

    private TaskIndustryListSearch request;
    private int page = 1;

    private HomeChildAdapter mAdapter; // 主页数据

    private Context mContext;
    private Activity mActivity;

    private int selectedTaskPos;
    private M_Task selectedTask;
    private String key;

    private TaskDetailPresenter taskDetailPresenter;
    private static final int REQ_TASK_DETAIL = 0x110;

    @Override
    protected void lazyLoad() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_child, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        task_search_industry_recordList(false);
    }

    private void initData() {
        key = getArguments().getString(SearchActivity.INTENT_KEY);
        mContext = getActivity();
        mActivity = getActivity();
        taskDetailPresenter = new TaskDetailPresenter(listJsonRequest, this);
    }

    private void initView() {
        mAdapter = new HomeChildAdapter(mContext, new ArrayList<M_Task>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        // 方案详情
        mAdapter.setOnPlanDetailClickListener(new HomeChildAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedTaskPos = position;
                selectedTask = mAdapter.getItem(position);

                M_Task task = mAdapter.getItem(position);
                Intent intent = new Intent(mContext, SearchDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivityForResult(intent, REQ_TASK_DETAIL);
            }
        });
        // 用户详情
        mAdapter.setOnUserDetailClickListener(new HomeChildAdapter.OnUserDetailClickListener() {
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

        mAdapter.setOnSearchClickListener(new HomeChildAdapter.OnSearchClickListener() {
            @Override
            public void onSearchClick(int position) {
                M_Task task = mAdapter.getItem(position);
                if(null!=task.voteList&&task.voteList.size()>0){
                    Intent intent =new Intent(getActivity(),NewDoTaskVoteActivity.class);
                    intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                    getActivity().startActivity(intent);
                }
                else{
                    Intent intent =new Intent(getActivity(),NewDoTaskPicActivity.class);
                    intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                    getActivity().startActivity(intent);
                }

            }
        });
    }

    public void search(String key){
        if(key.equals(this.key))
            return;

        showPd();
        this.key = key;
        task_search_industry_recordList(false);
    }

    //搜索方案列表
    public void task_search_industry_recordList(final boolean isLoadMore) {
        if (request != null) {
            request.cancel();
        }

        TaskIndustryListSearch.Input input = new TaskIndustryListSearch.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = key;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        request = new TaskIndustryListSearch(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskSearchIndustryRecordList) response).status == 1) {
                    fillAdapter(((APIM_TaskSearchIndustryRecordList) response).taskIndustryList,
                            ((APIM_TaskSearchIndustryRecordList) response).maxpage,
                            isLoadMore);

                } else {
                    ToastUtils.show(getActivity(), ((APIM_TaskSearchIndustryRecordList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    // 填充数据
    private void fillAdapter(List<M_Task> list, int maxpage, boolean isLoadMore) {
        if (mAdapter == null) {
            initView();
            initListener();
        }
        if (list == null || list.size() == 0) {
            smoothListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        task_search_industry_recordList(false);
    }

    @Override
    public void onLoadMore() {
        task_search_industry_recordList(true);
    }

    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            if(requestCode == REQ_TASK_DETAIL){
                taskDetailPresenter.task_industry_info(selectedTask.taskIndustryId);
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
