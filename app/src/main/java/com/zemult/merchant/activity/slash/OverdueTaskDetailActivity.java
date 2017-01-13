package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.adapter.slashfrgment.TaskCompleteAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.mvp.presenter.CompleteTaskPresenter;
import com.zemult.merchant.mvp.view.ICompleteTaskView;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 已过期的任务详情
 *
 * @author djy
 * @time 2016/8/15 14:19
 */
public class OverdueTaskDetailActivity extends BaseActivity implements ICompleteTaskView {

    public static final String INTENT_TASK = "task";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_task_info)
    TextView tvTaskInfo;
    @Bind(R.id.tv_title_task_describe)
    TextView tvTitleTaskDescribe;
    @Bind(R.id.tv_note)
    TextView tvNote;
    @Bind(R.id.tv_title_task_reward)
    TextView tvTitleTaskReward;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.tv_voucher)
    TextView tvVoucher;
    @Bind(R.id.iv_publish_icon)
    ImageView ivPublishIcon;
    @Bind(R.id.tv_publish_name)
    TextView tvPublishName;
    @Bind(R.id.iv_complaints)
    ImageView ivComplaints;
    @Bind(R.id.rl_all_task_complete)
    RelativeLayout rlAllTaskComplete;
    @Bind(R.id.lv_task_complete)
    FixedListView lvTaskComplete;
    @Bind(R.id.tv_report)
    TextView tvReport;
    private Context mContext;
    private TaskCompleteAdapter completeAdapter; // 完成列表适配器
    private CompleteTaskPresenter completeListPresenter;
    private M_Task task;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_overdue_task_detail);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("详情");
        ivComplaints.setVisibility(View.GONE);
        tvReport.setVisibility(View.GONE);
        task = ((M_Task) getIntent().getSerializableExtra("task"));
        completeListPresenter = new CompleteTaskPresenter(listJsonRequest, this);
        completeAdapter = new TaskCompleteAdapter(mContext, new ArrayList<M_Task>());
        lvTaskComplete.setAdapter(completeAdapter);

        completeListPresenter.task_industry_recordList_task_1_2(task.taskIndustryId, 1,1, 5, false);

        initView();
        initListener();
    }

    private void initView() {
        tvTitle.setText(task.title);
        tvExp.setText(String.format("经验X%s", String.valueOf(task.experience)));
        switch (task.cashType) {
            case 0:
                tvBonuses.setVisibility(View.GONE);
                tvVoucher.setVisibility(View.GONE);
                break;
            case 1:
                tvBonuses.setVisibility(View.VISIBLE);
                tvVoucher.setVisibility(View.GONE);
                break;
            case 2:
                tvBonuses.setVisibility(View.GONE);
                tvVoucher.setVisibility(View.VISIBLE);
                tvVoucher.setText(String.format("代金券%s元", task.voucherMoney));
                break;
        }

        tvTaskInfo.setText(String.format("%sde任务 %s结束", task.industryName,
                "| " + DateTimeUtil.strPubEndDiffTime(task.endtime)));
        tvNote.setText(task.note);
        imageManager.loadCircleHasBorderImage(task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
        tvPublishName.setText(String.format("%s 发布", task.userName));
    }

    private void initListener() {
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
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_complaints, R.id.rl_all_task_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_complaints:
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra("infoId", task.taskIndustryId);
                intent.putExtra("infoType", 1);
                startActivity(intent);
                break;
            case R.id.rl_all_task_complete:
                Intent intent2 = new Intent(mContext, AllTaskCompleteActivity.class);
                intent2.putExtra(AllTaskCompleteActivity.INTENT_TASK_INDUSTRY_ID, task.taskIndustryId);
                startActivity(intent2);
                break;
        }
    }


    @Override
    public void showError(String error) {
        ToastUtil.showMessage(error);
    }

    @Override
    public void setCompleteList(List<M_Task> list, boolean isLoadMore, int maxpage) {
        if (list != null)
            completeAdapter.setData(list, isLoadMore);
    }

    @Override
    public void stopRefreshOrLoad() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}