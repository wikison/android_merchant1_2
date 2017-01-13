package com.zemult.merchant.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.HomeVoteAdapter;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 任务详情页头部
 *
 * @author djy
 * @time 2016/7/29 16:00
 */
public class HeaderTaskDetailView extends HeaderViewInterface2<M_Task> {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_industry_name)
    TextView tvIndustryName;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.ll_user)
    LinearLayout llUser;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.gv_pic)
    FixedGridView gvPic;
    @Bind(R.id.iv_voice)
    ImageView ivVoice;
    @Bind(R.id.tv_second)
    TextView tvSecond;
    @Bind(R.id.rll_voice)
    RoundRelativeLayout rllVoice;
    @Bind(R.id.lv_vote)
    FixedListView lvVote;
    @Bind(R.id.ll_vote)
    LinearLayout llVote;
    @Bind(R.id.tv_exp)
    TextView tvExp;
    @Bind(R.id.tv_bonuses)
    TextView tvBonuses;
    @Bind(R.id.tv_voucher)
    TextView tvVoucher;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_del)
    TextView tvDel;
    @Bind(R.id.tv_comment)
    TextView tvComment;
    @Bind(R.id.ll_comment)
    LinearLayout llComment;
    @Bind(R.id.iv_like_one)
    ImageView ivLikeOne;
    @Bind(R.id.tv_like)
    TextView tvLike;
    @Bind(R.id.tv_vote_num)
    TextView tvVoteNum;
    @Bind(R.id.tv_content_tip)
    TextView tvContentTip;
    @Bind(R.id.tv_vote)
    TextView tvVote;
    @Bind(R.id.tv_vote_chose_num)
    TextView tvVoteChoseNum;
    @Bind(R.id.tv_vote_all)
    TextView tvVoteAll;
    @Bind(R.id.ll_vote_tip)
    LinearLayout llVoteTip;
    @Bind(R.id.tv_start)
    TextView tvStart;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_commission_money)
    TextView tvCommissionMoney;
    @Bind(R.id.tv_quan)
    TextView tvQuan;

    private int allWidth;
    private float itemWidth;
    private HomeVoteAdapter voteAdapter;

    public HeaderTaskDetailView(Activity context) {
        super(context);
        allWidth = DensityUtil.getWindowWidth(context) - DensityUtil.dip2px(mContext, 114);
    }

    @Override
    protected void getView(M_Task task, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.header_task_detail_layout, viewGroup, false);
        ButterKnife.bind(this, view);
        viewGroup.addView(view);
    }

    public ImageView getVoiceBtn() {
        return ivVoice;
    }

    public void dealWithTheView(final M_Task entity, int tabPos) {
        // 用户头像
        if (!TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage(entity.userHead, ivHead);
        else
            ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.userName))
            tvUserName.setText(entity.userName);
        // 任务标题
        if (!TextUtils.isEmpty(entity.title))
            tvTitle.setText(entity.title);
        // 子标题
        if (!TextUtils.isEmpty(entity.industryName))
            tvIndustryName.setText(entity.industryName + "de探索");
        // 完成时间
        if (!TextUtils.isEmpty(entity.completeTime))
            tvTime.setText(DateTimeUtil.strPubDiffTime(entity.completeTime) + "完成");
        // 点赞数
        tvLike.setText(entity.goodNum + "");
        // 评论数
        tvComment.setText(entity.commentNum + "");

        if (entity.isGood == 1)
            ivLikeOne.setImageResource(R.mipmap.zan_icon_sel);
        else
            ivLikeOne.setImageResource(R.mipmap.zan_icon_nor);

        // 奖励栏 探索奖励方式(0:无,1:红包,2:代金券)
        switch (entity.cashType) {
            case 1:
                tvBonuses.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvQuan.setVisibility(View.VISIBLE);
                break;
        }
        tvExp.setText(String.format("经验X%s", String.valueOf(entity.experience)));

        llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserClickListener != null)
                    onUserClickListener.onUserClick();
            }
        });

        if (tabPos == 3) {
            tvIndustryName.setVisibility(View.GONE);
            ivLikeOne.setVisibility(View.GONE);
            tvLike.setVisibility(View.GONE);
            tvContent.setVisibility(View.GONE);
            gvPic.setVisibility(View.GONE);
            rllVoice.setVisibility(View.GONE);
            llVote.setVisibility(View.GONE);

            tvPayMoney.setVisibility(View.VISIBLE);
            tvCommissionMoney.setVisibility(View.VISIBLE);

            tvPayMoney.setText("交易金额：" + entity.payMoney + "元");
            tvCommissionMoney.setText("获得佣金：" + entity.commissionMoney + "元");
            return;
        }

        // 文字
        if (!TextUtils.isEmpty(entity.userNote)) {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(entity.userNote);
            new myAsyncTask().execute();
        } else
            tvContent.setVisibility(View.GONE);
        // 照片
        if (!TextUtils.isEmpty(entity.pic)) {
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
            adapter.setWidth(DensityUtil.getWindowWidth((Activity) mContext) - DensityUtil.dip2px(mContext, 24));
            gvPic.setAdapter(adapter);
            gvPic.setVisibility(View.VISIBLE);
            adapter.setOnImageClickListener(new PhotoFix3Adapter.OnImageClickListener() {
                @Override
                public void onImageClick(int pos, List<String> photos) {
                    AppUtils.toImageDetial(mContext, pos, photos, false);
                }
            });
        } else
            gvPic.setVisibility(View.GONE);
        // 语音
        if (!TextUtils.isEmpty(entity.audioTime)
                && !entity.audioTime.equals("0")) {
            rllVoice.setVisibility(View.VISIBLE);
            rllVoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onVioceClickListener != null)
                        onVioceClickListener.onVioceClick();
                }
            });
            String secondStr = entity.audioTime;
            if (secondStr.contains("s")) {
                tvSecond.setText(entity.audioTime);
                secondStr = secondStr.replace("s", "");
            } else
                tvSecond.setText(entity.audioTime + "s");

            float second = Float.valueOf(secondStr);
            itemWidth = (float) allWidth * (second / 60);
//            RoundLinearLayout.LayoutParams lp = (RoundLinearLayout.LayoutParams) rllChild.getLayoutParams();
//            lp.width = (int) itemWidth;
//            rllChild.setLayoutParams(lp);
        } else
            rllVoice.setVisibility(View.GONE);
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
                    tvVote.setText("该用户选择了选项" + (i + 1));
                    break;
                }
            }
            tvVoteNum.setText("目前" + entity.voteAllNum + "人投票");
            voteAdapter = new HomeVoteAdapter(mContext, entity.voteList, 5);
            lvVote.setAdapter(voteAdapter);

            if (entity.voteList.size() > 5) {
                llVoteTip.setVisibility(View.VISIBLE);
                tvVoteChoseNum.setText("6...等共" + entity.voteList.size() + "个选项");
            }
        }

        tvContentTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("展开".equals(tvContentTip.getText().toString())) {
                    tvContentTip.setText("收起");
                    tvContent.setMaxLines(100);
                } else {
                    tvContentTip.setText("展开");
                    tvContent.setMaxLines(8);
                }
            }
        });
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
    }

    public interface OnUserClickListener {
        void onUserClick();
    }

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }


    public interface OnVioceClickListener {
        void onVioceClick();
    }

    private OnVioceClickListener onVioceClickListener;

    public void setOnVioceClickListener(OnVioceClickListener onVioceClickListener) {
        this.onVioceClickListener = onVioceClickListener;
    }

    private class myAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            int lineCount = tvContent.getLineCount();
            if (tvContent.getLineCount() > 8) {
                tvContent.setMaxLines(8);
                tvContentTip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

    }

    public void startPlay(int max) {
        tvStart.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(max);
        progressBar.setProgress(0);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void stopPlay() {
        tvStart.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}