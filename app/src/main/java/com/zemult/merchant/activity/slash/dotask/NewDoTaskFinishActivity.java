package com.zemult.merchant.activity.slash.dotask;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.HomeVoteAdapter;
import com.zemult.merchant.aip.task.TaskIndustryRecordInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedListView;

import butterknife.Bind;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/9/28.
 */

public class NewDoTaskFinishActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.tv_experience)
    TextView tvExperience;
    @Bind(R.id.rll_experience)
    RoundLinearLayout rllExperience;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.rll_bonus)
    RoundLinearLayout rllBonus;
    @Bind(R.id.rtv_label)
    RoundTextView rtvLabel;
    @Bind(R.id.rll_label)
    RoundLinearLayout rllLabel;
    @Bind(R.id.lv_vote)
    FixedListView lvVote;
    @Bind(R.id.tv_vote_chose_num)
    TextView tvVoteChoseNum;
    @Bind(R.id.tv_vote_all)
    TextView tvVoteAll;
    @Bind(R.id.ll_vote_tip)
    LinearLayout llVoteTip;
    @Bind(R.id.ll_vote)
    LinearLayout llVote;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;

    int experience = 0;
    double bonuseMoney = 0;
    String label = "";
    String voteId;
    int taskIndustryRecordId = 0;

    private HomeVoteAdapter voteAdapter;
    Context mContext;

    private TaskIndustryRecordInfoRequest taskIndustryRecordInfoRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_newdo_task_finish);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        lhBtnBack.setVisibility(View.GONE);
        lhTvTitle.setText("参与探索");

        experience = getIntent().getIntExtra("experience", 0);
        bonuseMoney = getIntent().getDoubleExtra("bonuse_money", 0);
        label = getIntent().getStringExtra("tag_name");
        voteId = getIntent().getStringExtra("vote_id");
        taskIndustryRecordId = getIntent().getIntExtra("taskIndustryRecordId", 0);

        if (!StringUtils.isBlank(voteId)) {
            getTaskDetail(taskIndustryRecordId);
        }
    }

    private void initView() {
        if (experience == 0) {
            rllExperience.setVisibility(View.GONE);
        } else {
            rllExperience.setVisibility(View.VISIBLE);
            tvExperience.setText("经验 X" + experience);
        }
        if (bonuseMoney == 0) {
            rllBonus.setVisibility(View.GONE);
        } else {
            rllBonus.setVisibility(View.VISIBLE);
            tvBonuses.setText("红包X" + bonuseMoney + "元");
        }
        rtvLabel.setText(label);

    }

    private void initListener() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getTaskDetail(int taskIndustryRecordId) {
        if (taskIndustryRecordInfoRequest != null) {
            taskIndustryRecordInfoRequest.cancel();
        }
        TaskIndustryRecordInfoRequest.Input input = new TaskIndustryRecordInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.convertJosn();

        taskIndustryRecordInfoRequest = new TaskIndustryRecordInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryInfo) response).status == 1) {
                    final M_Task entity = ((APIM_TaskIndustryInfo) response).taskIndustryRecordInfo;
                    // 投票
                    if (entity.voteList == null
                            || entity.voteList.isEmpty()) {
                        llVote.setVisibility(View.GONE);
                        lvVote.setVisibility(View.GONE);
                    } else {
                        llVote.setVisibility(View.VISIBLE);
                        lvVote.setVisibility(View.VISIBLE);
                        for (int i = 0; i < entity.voteList.size(); i++) {
                            M_Vote vote = entity.voteList.get(i);
                            if (vote.voteNote.equals(entity.voteChose)) {
                                vote.setSelected(true);
                                break;
                            }
                        }
                        voteAdapter = new HomeVoteAdapter(mContext, entity.voteList, 5);
                        lvVote.setAdapter(voteAdapter);

                        if (entity.voteList.size() > 5) {
                            llVoteTip.setVisibility(View.VISIBLE);
                            tvVoteChoseNum.setText("6...等共" + entity.voteList.size() + "个选项");
                        }
                    }

                    tvVoteAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ("展开".equals(tvVoteAll.getText().toString())) {
                                tvVoteAll.setText("收起");
                                voteAdapter.setData(entity.voteList, entity.voteList.size());
                                tvVoteChoseNum.setVisibility(View.GONE);
                            } else {
                                tvVoteAll.setText("展开");
                                voteAdapter.setData(entity.voteList, 5);
                                tvVoteChoseNum.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                }
            }
        });
        sendJsonRequest(taskIndustryRecordInfoRequest);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

}
