package com.zemult.merchant.view;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.DetailVoteAdapter;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DensityUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人发布任务的详情头部
 *
 * @author djy
 * @time 2016/8/13 9:59
 */
public class HeaderMyPublishTaskDetailView extends HeaderViewInterface<M_Task> {

    @Bind(R.id.tv_task_state)
    TextView tvTaskState;
    @Bind(R.id.tv_task_role_name)
    TextView tvTaskRoleName;
    @Bind(R.id.tv_title_task_title)
    TextView tvTitleTaskTitle;
    @Bind(R.id.tv_task_title)
    RoundTextView tvTaskTitle;
    @Bind(R.id.et_task_describe)
    EditText etTaskDescribe;
    @Bind(R.id.rll_task_describe)
    RoundRelativeLayout rllTaskDescribe;
    @Bind(R.id.rl_task_describe)
    RelativeLayout rlTaskDescribe;
    @Bind(R.id.tv_task_end_time)
    TextView tvTaskEndTime;
    @Bind(R.id.tv_reward)
    TextView tvReward;
    @Bind(R.id.tv_task_bonuse)
    TextView tvTaskBonuse;
    @Bind(R.id.rl_reward)
    RelativeLayout rlReward;
    @Bind(R.id.tv_complete_num)
    TextView tvCompleteNum;
    @Bind(R.id.lv_vote)
    FixedListView lvVote;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.tv_commission)
    TextView tvCommission;
    @Bind(R.id.ll_merchant_trade)
    LinearLayout llMerchantTrade;

    public HeaderMyPublishTaskDetailView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(M_Task task, ListView listView) {
        View view = mInflate.inflate(R.layout.header_my_publish_task_detail_layout, listView, false);
        ButterKnife.bind(this, view);

        listView.addHeaderView(view);
    }

    public void dealWithTheView(M_Task m_task) {
        int state = m_task.state;
        if (state == 0) {
            tvTaskState.setText("进行中");
            tvTaskState.setTextColor(Convert.stringToColor("#FFA726"));
        } else if (state == 1) {
            tvTaskState.setText("已结束");
            tvTaskState.setTextColor(Convert.stringToColor("#333333"));
        }
        tvTaskRoleName.setText(m_task.industryName);
        tvTaskTitle.setText(m_task.title);
        etTaskDescribe.setText(m_task.note);
        tvTaskEndTime.setText(m_task.endtime);
        int cashType = m_task.cashType;
        if (cashType == 0) {
            rlReward.setVisibility(View.GONE);
        } else if (cashType == 1) {
            rlReward.setVisibility(View.VISIBLE);
            tvReward.setText("红包");
            tvTaskBonuse.setText(String.format("已发%d/%d个, 共%.2f/%.2f元", m_task.outNum, m_task.bonuseNum, m_task.outMoney, m_task.bonuseMoney));
        } else if (cashType == 2) {
            rlReward.setVisibility(View.VISIBLE);
            tvReward.setText("优惠券");
            tvTaskBonuse.setText(String.format("已发%d/%d张", m_task.outVoucherNum, m_task.voucherNum));
        }

        if (m_task.type == 2) {
            lvVote.setVisibility(View.VISIBLE);
            if (m_task.voteList == null
                    || m_task.voteList.isEmpty()) { // 选项列表
                lvVote.setVisibility(View.GONE);
            } else {
                lvVote.setVisibility(View.VISIBLE);
                DetailVoteAdapter adapter = new DetailVoteAdapter(mContext, m_task.voteList, m_task.voteAllNum,
                        DensityUtil.getWindowWidth(mContext) - DensityUtil.dip2px(mContext, 24));
                lvVote.setAdapter(adapter);
            }
        } else {
            lvVote.setVisibility(View.GONE);
        }

        if (m_task.type == 3) {
            llMerchantTrade.setVisibility(View.VISIBLE);
            tvDiscount.setText(m_task.discount == 0 ? "0" : m_task.discount + "折");
            tvCommission.setText(m_task.commissionDiscount == 0 ? "0" : m_task.commissionDiscount + "%");
            tvCompleteNum.setVisibility(View.GONE);
        } else {
            llMerchantTrade.setVisibility(View.GONE);
            tvCompleteNum.setVisibility(View.VISIBLE);
            tvCompleteNum.setText(String.format("当前%d人完成任务", m_task.completeNum));
        }
    }

}