package com.zemult.merchant.activity.slash.dotask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.aip.task.TaskIndustryCompleteVoteRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/8/1.
 */
public class NewDoTaskVoteActivity extends BaseActivity {
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


    @Bind(R.id.rg_vote)
    RadioGroup rgVote;
    String voteIds = "";
    TaskIndustryCompleteVoteRequest taskIndustryCompleteVoteRequest;
    int taskIndustryRecordId, taskIndustryId;
    @Bind(R.id.tv_right)
    TextView tvRight;
    M_Task m_task;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_newdo_task_vote);
    }

    @Override
    public void init() {
        lhTvTitle.setText("任务");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("下一步");
        m_task= ((M_Task) getIntent().getSerializableExtra("task"));
        taskIndustryRecordId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryRecordId;
        if (taskIndustryRecordId <= 0) {
            taskIndustryRecordId = getIntent().getIntExtra("task_industry_record_id", -1);
        }
        taskIndustryId = ((M_Task) getIntent().getSerializableExtra("task")).taskIndustryId;


        rgVote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i=0;i<group.getChildCount();i++){
                    group.getChildAt(i).setBackgroundColor(Color.parseColor("#f2f2f2"));
                }
                RadioButton tempButton = (RadioButton) findViewById(checkedId); // 通过RadioGroup的findViewById方法，找到ID为checkedID的RadioButton
                // 以下就可以对这个RadioButton进行处理了
                tempButton.setBackgroundColor(Color.parseColor("#fde59c"));
                voteIds = tempButton.getTag().toString();
            }
        });


        tvTitle.setText(m_task.title);
        List<M_Vote> voteList = m_task.voteList;
        for (int i = 0; i < voteList.size(); i++) {
            RadioButton tempButton = new RadioButton(NewDoTaskVoteActivity.this);
            tempButton.setBackgroundColor(Color.parseColor("#f2f2f2"));   // 设置RadioButton的背景图片
            tempButton.setButtonDrawable(getResources().getDrawable(R.color.transparent));           // 设置按钮的样式 R.drawable.check_box_style
            Drawable dright = getResources().getDrawable(R.drawable.check_box_style);
            dright.setBounds(0, 0, dright.getMinimumWidth(), dright.getMinimumHeight());
            tempButton.setCompoundDrawables(null, null, dright, null);
            tempButton.setPadding(80, 0, 100, 0);                 // 设置文字距离按钮四周的距离
            tempButton.setText(voteList.get(i).voteNote);
            tempButton.setTag(voteList.get(i).voteId);
            tempButton.setTextColor(Color.parseColor("#464646"));
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, DensityUtil.dp2px(this, 44));
            lp.topMargin = 20;
            lp.leftMargin = 20;
            lp.rightMargin = 20;
            rgVote.addView(tempButton, lp);
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
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
                Intent intent =new Intent(NewDoTaskVoteActivity.this,NewDoTaskPicActivity.class);
                intent.putExtra("voteIds",voteIds);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, m_task);
                startActivityForResult(intent,1000);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            finish();
        }
    }

    //    private void task_industry_complete_vote() {
//        try {
//            if (taskIndustryCompleteVoteRequest != null) {
//                taskIndustryCompleteVoteRequest.cancel();
//            }
//            TaskIndustryCompleteVoteRequest.Input input = new TaskIndustryCompleteVoteRequest.Input();
//            input.userId = SlashHelper.userManager().getUserId();
//            input.taskIndustryRecordId = taskIndustryRecordId;
//            input.voteIds = voteIds;
//            input.convertJosn();
//
//            taskIndustryCompleteVoteRequest = new TaskIndustryCompleteVoteRequest(input, new ResponseListener() {
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    System.out.print(error);
//                }
//
//
//                @Override
//                public void onResponse(Object response) {
//                    int status = ((CommonResult) response).status;
//                    if (status == 1) {
//                        ToastUtil.showMessage("投票成功");
//                        Intent intent = new Intent(Constants.BROCAST_FRESHSLASH);
//                        sendBroadcast(intent);
//                        sendBroadcast(new Intent(Constants.BROCAST_FRESHTASKLIST));
//                        finish();
//                    } else {
//                        ToastUtil.showMessage(((CommonResult) response).info);
//                    }
//                }
//            });
//            sendJsonRequest(taskIndustryCompleteVoteRequest);
//        } catch (Exception e) {
//        }
//    }


}
