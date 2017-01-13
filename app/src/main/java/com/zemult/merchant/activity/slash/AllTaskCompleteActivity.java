package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.TaskCompleteAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.mvp.presenter.CompleteTaskPresenter;
import com.zemult.merchant.mvp.view.ICompleteTaskView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;

/**
 * 完成任务列表页
 *
 * @author djy
 * @time 2016/7/29 13:49
 */
public class AllTaskCompleteActivity extends BaseActivity implements ICompleteTaskView, SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;

    private static final String TAG = AllCommentActivity.class.getSimpleName();
    public static final String INTENT_TASK_INDUSTRY_RECORD_ID = "taskIndustryRecordId";
    public static final String INTENT_TASK_INDUSTRY_ID = "taskIndustryId";

    private List<M_Task> completeList = new ArrayList<>(); // ListView数据
    private TaskCompleteAdapter completeAdapter; // 完成列表适配器
    private int page;

    private CompleteTaskPresenter completeListPresenter;
    private Context mContext;
    private int taskIndustryRecordId, taskIndustryId;
    private int orderType = 1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_task_complete);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        mContext = this;
        taskIndustryRecordId = getIntent().getIntExtra(INTENT_TASK_INDUSTRY_RECORD_ID, 0);
        taskIndustryId = getIntent().getIntExtra(INTENT_TASK_INDUSTRY_ID, 0);
        completeListPresenter = new CompleteTaskPresenter(listJsonRequest, this);
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setText("完成任务列表");
        // 设置ListView数据
        completeAdapter = new TaskCompleteAdapter(mContext, completeList);
        smoothListView.setAdapter(completeAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    private void getNetworkData() {
        if(taskIndustryRecordId != 0)
            completeListPresenter.task_industry_recordList_other_1_2(taskIndustryRecordId, orderType, page = 1, Constants.ROWS, false);
        else
            completeListPresenter.task_industry_recordList_task_1_2(taskIndustryId, orderType, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onRefresh() {
        if(taskIndustryRecordId != 0)
            completeListPresenter.task_industry_recordList_other_1_2(taskIndustryRecordId, orderType, page = 1, Constants.ROWS, false);
        else
            completeListPresenter.task_industry_recordList_task_1_2(taskIndustryId, orderType, page = 1, Constants.ROWS, false);
    }

    @Override
    public void onLoadMore() {
        if(taskIndustryRecordId != 0)
            completeListPresenter.task_industry_recordList_other_1_2(taskIndustryRecordId, orderType, page = 1, Constants.ROWS, true);
        else
            completeListPresenter.task_industry_recordList_task_1_2(taskIndustryId, orderType, page = 1, Constants.ROWS, true);
    }

    @Override
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setCompleteList(List<M_Task> list, boolean isLoadMore, int maxpage) {
        if (list != null && !list.isEmpty()) {
            smoothListView.setLoadMoreEnable(page < maxpage);
            completeAdapter.setData(list, false);
            if (!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void stopRefreshOrLoad() {
        smoothListView.stopLoadMore();
        smoothListView.stopRefresh();
    }

}
