package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.TaskCompleteAdapter;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.aip.task.TaskIndustryRecordListTaskRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderMyPublishTaskDetailView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/10.
 */
public class MyPublishTaskDetailActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lv_task_complete)
    SmoothListView smoothListView;

    int taskIndustryId = -1, page = 1;
    TaskIndustryInfoRequest taskIndustryInfoRequest;
    TaskIndustryRecordListTaskRequest taskIndustryRecordListTaskRequest;
    int cashType = 0;
    private Context mContext;
    private Activity mActivity;
    private M_Task intent_task;
    private TaskCompleteAdapter completeAdapter; // 完成列表适配器
    private HeaderMyPublishTaskDetailView headerMyPublishTaskDetailView;
    private static int REQ_BONUSE_INFO = 0x110;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_my_task_detail);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

    }

    private void initData() {
        mContext = this;
        mActivity = this;
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.title_publish_task_detail));

        intent_task = (M_Task) getIntent().getSerializableExtra("my_publish_task");
        if (intent_task != null) {
            cashType = intent_task.cashType;
            taskIndustryId = intent_task.taskIndustryId;
            getTaskInfo();
            getCompleteList(false);
        }
    }

    private void initView() {
        headerMyPublishTaskDetailView = new HeaderMyPublishTaskDetailView(mActivity);
        headerMyPublishTaskDetailView.fillView(intent_task, smoothListView);
        if (intent_task != null) {
            headerMyPublishTaskDetailView.dealWithTheView(intent_task);
            completeAdapter = new TaskCompleteAdapter(mActivity, new ArrayList<M_Task>(), intent_task.type, intent_task.cashType);
        }

        smoothListView.setAdapter(completeAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        completeAdapter.setOnHeadClickListener(new TaskCompleteAdapter.OnHeadClickListener() {
            @Override
            public void onHeadClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, completeAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, completeAdapter.getItem(position).userName);
                startActivity(intent);
            }
        });
        completeAdapter.setOnItemClickListener(new TaskCompleteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                M_Task task = completeAdapter.getItem(position);
                Intent intent = new Intent(mContext, TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                intent.putExtra("intent_from", "MyPublishTaskDetail");
                startActivityForResult(intent, REQ_BONUSE_INFO);
            }
        });
    }

    private void getTaskInfo() {
        if (taskIndustryInfoRequest != null) {
            taskIndustryInfoRequest.cancel();
        }
        TaskIndustryInfoRequest.Input input = new TaskIndustryInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryId = taskIndustryId;
        input.convertJosn();
        taskIndustryInfoRequest = new TaskIndustryInfoRequest(input, new ResponseListener() {
            @Override
            public void onResponse(Object response) {
                int status = ((APIM_TaskIndustryInfo) response).status;
                if (status == 1) {
                    intent_task = ((APIM_TaskIndustryInfo) response).taskIndustryInfo;
                    headerMyPublishTaskDetailView.dealWithTheView(intent_task);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        sendJsonRequest(taskIndustryInfoRequest);
    }


    private void getCompleteList(final boolean isLoadMore) {
        if (taskIndustryRecordListTaskRequest != null) {
            taskIndustryRecordListTaskRequest.cancel();
        }
        TaskIndustryRecordListTaskRequest.Input input = new TaskIndustryRecordListTaskRequest.Input();
        input.taskIndustryId = taskIndustryId; //角色任务id
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        taskIndustryRecordListTaskRequest = new TaskIndustryRecordListTaskRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    setCompleteList(((APIM_TaskIndustryListNew) response).taskIndustryRecordList,
                            ((APIM_TaskIndustryListNew) response).maxpage, isLoadMore);
                } else {
                    ToastUtil.showMessage(((APIM_TaskIndustryListNew) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });

        sendJsonRequest(taskIndustryRecordListTaskRequest);
    }

    /**
     * 设置TA的记录
     */
    private void setCompleteList(List<M_Task> list, int maxpage, boolean isLoadMore) {
        if (list == null) {
            completeAdapter.setData(new ArrayList<M_Task>(), isLoadMore);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            completeAdapter.setData(list, isLoadMore);
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getCompleteList(false);
    }

    @Override
    public void onLoadMore() {
        getCompleteList(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_BONUSE_INFO) {
                getTaskInfo();
                getCompleteList(false);
            }
        }
    }
}
