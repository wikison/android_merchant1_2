package com.zemult.merchant.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MyTaskActivity;
import com.zemult.merchant.activity.slash.OverdueTaskDetailActivity;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskPayActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskPicActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoiceActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoteActivity;
import com.zemult.merchant.adapter.slash.MyTaskListAdapter;
import com.zemult.merchant.aip.task.TaskIndustryAddRequest;
import com.zemult.merchant.aip.task.TaskIndustryListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/7/28.
 */
public class MyTaskFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    public static final String TAG = MyTaskFragment.class.getSimpleName();
    protected WeakReference<View> mRootView;
    @Bind(R.id.sml_task)
    SmoothListView mSmoothListView;
    @Bind(R.id.ll_no_data)
    LinearLayout mLinearLayoutNoData;
    TaskIndustryListRequest taskIndustryListRequest;
    TaskIndustryAddRequest taskIndustryAddRequest;
    MyTaskListAdapter myTaskListAdapter;
    List<M_Task> mTaskList = new ArrayList<>();
    int pagePosition = 0;
    int industryId = -1;
    MyTaskActivity myTaskActivity;
    int taskIndustryRecordId = -1;
    private View view;
    private Context mContext;
    private boolean hasLoaded;
    private int page = 1;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded || !isVisible) {
            return;
        }

        if (mTaskList.size() < 1) {
            //获取列表数据
            taskIndustryList(false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get page index
        myTaskActivity = (MyTaskActivity) getActivity();
        pagePosition = getArguments().getInt("page_position");
        industryId = myTaskActivity.industryId;

        registerReceiver(new String[]{Constants.BROCAST_FRESHTASKLIST});
    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_FRESHTASKLIST.equals(intent.getAction())) {
            taskIndustryList(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //保存当前界面，防止重新创建
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.fragment_my_task, container, false);
            mRootView = new WeakReference<View>(view);
            hasLoaded = true;
            ButterKnife.bind(this, view);
            lazyLoad();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }

        mSmoothListView.setRefreshEnable(true);
        mSmoothListView.setLoadMoreEnable(false);
        mSmoothListView.setSmoothListViewListener(this);

        return mRootView.get();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
//        initView();

    }

    private void initData() {
        mContext = getActivity();
    }

//    private void initView() {
//
//    }

    private void initListener() {
        myTaskListAdapter.setOnTextTaskClickListener(new MyTaskListAdapter.OnTextTaskClickListener() {
            @Override
            public void onTextTaskClick(int position) {
                M_Task task = myTaskListAdapter.getItem(position);
                Intent intent;
                switch (pagePosition) {
                    case 0:
                        addTask(task);
                        break;
                    case 1:
                        doTaskType(task);
                        break;
                    case 2:
                        intent = new Intent(mContext, TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(mContext, OverdueTaskDetailActivity.class);
                        intent.putExtra(OverdueTaskDetailActivity.INTENT_TASK, task);
                        startActivity(intent);
                        break;
                }

            }


        });
    }

    private void addTask(final M_Task task) {
        if (taskIndustryAddRequest != null) {
            taskIndustryAddRequest.cancel();
        }
        TaskIndustryAddRequest.Input input = new TaskIndustryAddRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryId = task.taskIndustryId;
        input.convertJosn();
        taskIndustryAddRequest = new TaskIndustryAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(getActivity(), "领取任务成功");
                    taskIndustryRecordId = ((CommonResult) response).taskIndustryRecordId;
                    onRefresh();
                    doTaskType(task);
                } else {
                    ToastUtils.show(getActivity(), ((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(taskIndustryAddRequest);

    }

    private void doTaskType(M_Task task) {
        Intent intent = new Intent();
        switch (task.type) {
            case 0:
                intent = new Intent(mContext, DoTaskPicActivity.class);
                break;
            case 1:
                intent = new Intent(mContext, DoTaskVoiceActivity.class);
                break;
            case 2:
                intent = new Intent(mContext, DoTaskVoteActivity.class);
                break;
            case 3:
                intent = new Intent(mContext, DoTaskPayActivity.class);
                break;
        }
        intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
        intent.putExtra("task_industry_record_id", taskIndustryRecordId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

    }

    //获取对应页面下的任务列表
    private void taskIndustryList(final boolean isLoadMore) {
        if (taskIndustryListRequest != null) {
            taskIndustryListRequest.cancel();
        }

        TaskIndustryListRequest.Input input = new TaskIndustryListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        //任务状态(-1新任务,0:待完成,1:已完成,2:已过期 )
        if (pagePosition == 0) {
            input.state = -1;
        } else if (pagePosition == 1) {
            input.state = 0;
        } else if (pagePosition == 2) {
            input.state = 1;
        } else {
            input.state = 2;
        }
        input.industryId = myTaskActivity.industryId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        showPd();
        taskIndustryListRequest = new TaskIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryListNew) response).status == 1) {
                    if (myTaskListAdapter == null) {
                        myTaskListAdapter = new MyTaskListAdapter(mContext, mTaskList, pagePosition);
                        mSmoothListView.setAdapter(myTaskListAdapter);
                        initListener();
                    }
                    fillAdapter(((APIM_TaskIndustryListNew) response).taskList,
                            ((APIM_TaskIndustryListNew) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_TaskIndustryListNew) response).info);
                }
                dismissPd();
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(taskIndustryListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Task> list, int maxPage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            mLinearLayoutNoData.setVisibility(View.VISIBLE);
            mSmoothListView.setVisibility(View.GONE);
            mSmoothListView.setLoadMoreEnable(false);
        } else {
            mLinearLayoutNoData.setVisibility(View.GONE);
            mSmoothListView.setVisibility(View.VISIBLE);
            mSmoothListView.setLoadMoreEnable(page < maxPage);
            myTaskListAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        taskIndustryList(false);
    }

    @Override
    public void onLoadMore() {
        taskIndustryList(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
