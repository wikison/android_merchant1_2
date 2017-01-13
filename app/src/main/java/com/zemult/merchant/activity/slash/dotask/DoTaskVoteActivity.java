package com.zemult.merchant.activity.slash.dotask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.ReportActivity;
import com.zemult.merchant.aip.task.TaskIndustryCompleteVoteRequest;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/1.
 */
public class DoTaskVoteActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_type_icon)
    ImageView ivTypeIcon;
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
    @Bind(R.id.rg_vote)
    RadioGroup rgVote;
    String voteIds = "";
    TaskIndustryInfoRequest taskIndustryInfoRequest;
    TaskIndustryCompleteVoteRequest taskIndustryCompleteVoteRequest;
    int taskIndustryRecordId, taskIndustryId;
    @Bind(R.id.tv_right)
    TextView tvRight;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_do_task_vote);
    }

    @Override
    public void init() {
        lhTvTitle.setText("任务");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");
        taskIndustryRecordId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryRecordId;
        if (taskIndustryRecordId <= 0) {
            taskIndustryRecordId = getIntent().getIntExtra("task_industry_record_id", -1);
        }
        taskIndustryId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryId;

        imageManager.loadResImage(R.mipmap.toupiao_icon_big, ivTypeIcon);

        rgVote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                RadioButton tempButton = (RadioButton) findViewById(checkedId); // 通过RadioGroup的findViewById方法，找到ID为checkedID的RadioButton
                // 以下就可以对这个RadioButton进行处理了
                voteIds = tempButton.getTag().toString();
            }
        });


        task_industry_info();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right, R.id.iv_complaints})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_right:
                if (StringUtils.isEmpty(voteIds)) {
                    ToastUtil.showMessage("请选择一个选项");
                    return;
                }
                task_industry_complete_vote();
                break;
            case R.id.iv_complaints:
                Intent intent = new Intent(DoTaskVoteActivity.this, ReportActivity.class);
                intent.putExtra("infoId", taskIndustryId);
                intent.putExtra("infoType", 1);
                startActivity(intent);
                break;
        }
    }


    private void task_industry_complete_vote() {
        try {
            if (taskIndustryCompleteVoteRequest != null) {
                taskIndustryCompleteVoteRequest.cancel();
            }
            TaskIndustryCompleteVoteRequest.Input input = new TaskIndustryCompleteVoteRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.taskIndustryRecordId = taskIndustryRecordId;
            input.voteIds = voteIds;
            input.convertJosn();

            taskIndustryCompleteVoteRequest = new TaskIndustryCompleteVoteRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("投票成功");
                        Intent intent = new Intent(Constants.BROCAST_FRESHSLASH);
                        sendBroadcast(intent);
                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
                        finish();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryCompleteVoteRequest);
        } catch (Exception e) {
        }
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
                        M_Task m_task = ((APIM_TaskIndustryInfo) response).taskIndustryInfo;
                        tvTitle.setText(m_task.title);
                        tvExp.setText(String.format("经验X%s", String.valueOf(m_task.experience)));
                        String remainder = "";
                        switch (m_task.cashType) {
                            case 0:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.GONE);
                                break;
                            case 1:
                                tvBonuses.setVisibility(View.VISIBLE);
                                tvVoucher.setVisibility(View.GONE);
                                remainder = "红包余量" + (m_task.bonuseNum - m_task.outNum) + "/" + m_task.bonuseNum;
                                break;
                            case 2:
                                tvBonuses.setVisibility(View.GONE);
                                tvVoucher.setVisibility(View.VISIBLE);
                                tvVoucher.setText(String.format("代金券%s元", m_task.voucherMoney));
                                remainder = "代金券余量" + (m_task.voucherNum - m_task.outVoucherNum) + "/" + m_task.voucherNum;
                                break;
                        }

                        if (!StringUtils.isBlank(remainder)) {
                            remainder = "| " + remainder;
                        }
                        tvTaskInfo.setText(String.format("%sde任务 %s结束", m_task.industryName,
                                "| " + DateTimeUtil.strPubEndDiffTime(m_task.endtime)));
                        tvNote.setText(m_task.note);
                        imageManager.loadCircleHasBorderImage(m_task.userHead, ivPublishIcon, getResources().getColor(R.color.border_color), 1);
                        tvPublishName.setText(String.format("%s 发布", m_task.userName));
                        List<M_Vote> voteList = ((APIM_TaskIndustryInfo) response).taskIndustryInfo.voteList;
                        for (int i = 0; i < voteList.size(); i++) {
                            RadioButton tempButton = new RadioButton(DoTaskVoteActivity.this);
                            tempButton.setBackgroundResource(R.drawable.bg_frame_gray);   // 设置RadioButton的背景图片
                            tempButton.setButtonDrawable(getResources().getDrawable(R.color.transparent));           // 设置按钮的样式 R.drawable.check_box_style
                            Drawable dright = getResources().getDrawable(R.drawable.check_box_style);
                            dright.setBounds(0, 0, dright.getMinimumWidth(), dright.getMinimumHeight());
                            tempButton.setCompoundDrawables(null, null, dright, null);
                            tempButton.setPadding(80, 0, 100, 0);                 // 设置文字距离按钮四周的距离
                            tempButton.setText(voteList.get(i).voteNote);
                            tempButton.setTag(voteList.get(i).voteId);
                            tempButton.setTextColor(Color.parseColor("#666666"));
                            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, 100);
                            lp.topMargin = 20;
                            lp.leftMargin = 20;
                            lp.rightMargin = 20;
                            rgVote.addView(tempButton, lp);
                        }
                    } else {
                        ToastUtil.showMessage(((APIM_TaskIndustryInfo) response).info);
                    }
                }
            });
            sendJsonRequest(taskIndustryInfoRequest);
        } catch (Exception e) {

        }


    }
}
