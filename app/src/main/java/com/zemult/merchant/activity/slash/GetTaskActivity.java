package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskPayActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskPicActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoiceActivity;
import com.zemult.merchant.activity.slash.dotask.DoTaskVoteActivity;
import com.zemult.merchant.aip.task.TaskIndustryAddRequest;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 领取任务
 * @author djy
 * @time 2016/8/15 9:15
 */
public class GetTaskActivity extends BaseActivity {

    public static final String INTENT_TASK_INDUSTRY_ID = "taskIndustryId";
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
    @Bind(R.id.btn_get)
    Button btnGet;

    private int taskIndustryId;
    private TaskIndustryInfoRequest taskIndustryInfoRequest;
    private TaskIndustryAddRequest taskIndustryAddRequest;
    private M_Task m_task;
    private int taskIndustryRecordId;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_get_task);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("任务");
        taskIndustryId = getIntent().getIntExtra(INTENT_TASK_INDUSTRY_ID, -1);
        task_industry_info();
    }

    private void task_industry_info() {
        try {
            if (taskIndustryInfoRequest != null) {
                taskIndustryInfoRequest.cancel();
            }
            TaskIndustryInfoRequest.Input input = new TaskIndustryInfoRequest.Input();
            input.taskIndustryId = taskIndustryId;
            input.userId = SlashHelper.userManager().getUserId();
            input.convertJosn();

            taskIndustryInfoRequest = new TaskIndustryInfoRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((APIM_TaskIndustryInfo) response).status;
                    if (status == 1) {
                        m_task = ((APIM_TaskIndustryInfo) response).taskIndustryInfo;
                        tvTitle.setText(m_task.title);
                        tvExp.setText(String.format("经验X%s", String.valueOf(m_task.experience)));
                        tvTaskInfo.setText(String.format("%sde任务 %s结束", m_task.industryName,
                                "| " + DateTimeUtil.strPubEndDiffTime(m_task.endtime)));
                        tvNote.setText(m_task.note);
                        imageManager.loadCircleHasBorderImage(m_task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
                        tvPublishName.setText(String.format("%s 发布", m_task.userName));


                        // 奖励栏
                        switch (m_task.cashType) {
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
                                tvVoucher.setText(String.format("代金券%s元", m_task.voucherMoney));
                                break;
                        }
                        tvExp.setText(String.format("经验X%s", String.valueOf(m_task.experience)));
                    } else {
                        ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryInfoRequest);
        } catch (Exception e) {
        }
    }

    private void addTask() {
        if (taskIndustryAddRequest != null) {
            taskIndustryAddRequest.cancel();
        }
        TaskIndustryAddRequest.Input input = new TaskIndustryAddRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryId = m_task.taskIndustryId;
        input.convertJosn();
        taskIndustryAddRequest = new TaskIndustryAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("领取任务成功");
                    taskIndustryRecordId = ((CommonResult) response).taskIndustryRecordId;
                    doTaskType();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(taskIndustryAddRequest);
    }

    private void doTaskType() {
        Intent intent = new Intent();
        switch (m_task.type) {
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
        intent.putExtra(TaskDetailActivity.INTENT_TASK, m_task);
        intent.putExtra("task_industry_record_id", taskIndustryRecordId);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_get,  R.id.iv_complaints})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_get:
                addTask();
                break;
            case R.id.iv_complaints:
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra("infoId",taskIndustryId);
                intent.putExtra("infoType",1);
                startActivity(intent);
                break;
        }
    }
}
