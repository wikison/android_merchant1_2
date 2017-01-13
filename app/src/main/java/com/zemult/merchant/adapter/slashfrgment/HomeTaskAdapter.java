package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.FixedListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 任务记录adapter
 *
 * @author djy
 * @time 2016/7/25 14:58
 */
public class HomeTaskAdapter extends BaseListAdapter<M_Task> {

    private boolean isNoData, showAll, unshowUser, loadMoreEnable = true, showActivity, showTitle, showRole;
    private int mHeight, allWidth;
    private float itemWidth;

    public HomeTaskAdapter(Context context, List<M_Task> list) {
        super(context, list);
        allWidth = DensityUtil.getWindowWidth((Activity) mContext) - DensityUtil.dip2px(mContext, 114);
    }

    public void showAll(boolean showAll) {
        this.showAll = showAll;
    }

    public void unshowUser(boolean unshowUser) {
        this.unshowUser = unshowUser;
    }

    public void showActivity(boolean showActivity) {
        this.showActivity = showActivity;
    }
    public void showTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }
    public void showRole(boolean showRole) {
        this.showRole = showRole;
    }

    // 设置数据 任务
    public void setData(List<M_Task> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        isNoData = false;
        if (list.size() == 1 && list.get(0).isNoData()) {
            // 暂无数据布局
            isNoData = list.get(0).isNoData();
            mHeight = list.get(0).getHeight();
        }
        notifyDataSetChanged();
    }

    // 设置数据 任务
    public void setData(List<M_Task> list, boolean isLoadMore, boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        isNoData = false;
        if (list.size() == 1 && list.get(0).isNoData()) {
            // 暂无数据布局
            isNoData = list.get(0).isNoData();
            mHeight = list.get(0).getHeight();
        }
        notifyDataSetChanged();
    }

    // 刷新单条记录
    public void refreshOneRecord(M_Task task, int pos) {
        task.taskIndustryRecordId = getData().get(pos).taskIndustryRecordId;
        getData().set(pos, task);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 暂无数据
        if (isNoData) {
            convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_no_data);
            rootView.setLayoutParams(params);
            return convertView;
        }
        // 正常数据
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_home_child, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (position == getCount() - 1 && loadMoreEnable == false)
            holder.viewBottom.setVisibility(View.VISIBLE);
        else
            holder.viewBottom.setVisibility(View.GONE);

        M_Task entity = getItem(position);
        // 设置数据
        initData(holder, entity);
        // 设置监听器
        initListner(holder, position);

        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, M_Task entity) {
        if (unshowUser) {
            holder.llUser.setVisibility(View.GONE);
            holder.viewTop12.setVisibility(View.VISIBLE);
        } else {
            holder.llUser.setVisibility(View.VISIBLE);
            // 用户头像
            if (!TextUtils.isEmpty(entity.userHead))
                mImageManager.loadCircleImage(entity.userHead, holder.ivHead);
            else
                holder.ivHead.setImageResource(R.mipmap.user_icon);
            // 用户名
            if (!TextUtils.isEmpty(entity.userName))
                holder.tvUserName.setText(entity.userName);
        }
        if(showTitle){
            holder.tvTitle.setVisibility(View.VISIBLE);
            // 标题
            if (!TextUtils.isEmpty(entity.title))
                holder.tvTitle.setText(entity.title);
        }
        if(showRole){
            holder.tvRole.setVisibility(View.VISIBLE);
            // 角色
            if (!TextUtils.isEmpty(entity.industryName))
                holder.tvRole.setText(entity.industryName + "de探索");
        }

        // 完成时间
        if (!TextUtils.isEmpty(entity.completeTime)) {
            if (showActivity && !TextUtils.isEmpty(entity.industryName))
                holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.completeTime) + "  完成" + entity.industryName + "的探索");
            else
                holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.completeTime) + "完成");
        }

        // 点赞数
        holder.tvLike.setText(entity.goodNum + "");
        // 评论数
        holder.tvComment.setText(entity.commentNum + "");

        if (entity.isGood == 1)
            holder.ivLike.setImageResource(R.mipmap.zan_icon_sel);
        else
            holder.ivLike.setImageResource(R.mipmap.zan_icon_nor);

        holder.tvContent.setVisibility(View.GONE);
        holder.gvPic.setVisibility(View.GONE);
        holder.rllVoice.setVisibility(View.GONE);
        holder.llVote.setVisibility(View.GONE);

        if (showActivity) {
            holder.llLike.setVisibility(View.GONE);
            holder.tvPayMoney.setVisibility(View.VISIBLE);
            holder.tvCommissionMoney.setVisibility(View.VISIBLE);

            holder.tvPayMoney.setText("完成交易额" + entity.payMoney + "元");
            holder.tvCommissionMoney.setText("获得佣金" + entity.commissionMoney + "元");
        } else {
            // 文字
            if (!TextUtils.isEmpty(entity.userNote)) {
                holder.tvContent.setVisibility(View.VISIBLE);
                holder.tvContent.setText(entity.userNote);
                if (!showAll)
                    return;
            } else
                holder.tvContent.setVisibility(View.GONE);
            // 照片
            if (!TextUtils.isEmpty(entity.pic)) {
                PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
                holder.gvPic.setAdapter(adapter);
                holder.gvPic.setVisibility(View.VISIBLE);
                if (!showAll)
                    return;
            } else
                holder.gvPic.setVisibility(View.GONE);
            // 语音
            if (!TextUtils.isEmpty(entity.audioTime)) {
                holder.rllVoice.setVisibility(View.VISIBLE);
                holder.tvSecond.setText(entity.audioTime + "s");
                if (!showAll)
                    return;
            } else
                holder.rllVoice.setVisibility(View.GONE);
            // 投票
            if (entity.voteList == null
                    || entity.voteList.isEmpty()) {
                holder.llVote.setVisibility(View.GONE);
                holder.lvVote.setVisibility(View.GONE);
            } else {
                holder.llVote.setVisibility(View.VISIBLE);
                holder.lvVote.setVisibility(View.VISIBLE);

                for (int i = 0; i < entity.voteList.size(); i++) {
                    M_Vote vote = entity.voteList.get(i);
                    if (vote.voteNote.equals(entity.voteChose)) {
                        vote.setSelected(true);
                        holder.tvVote.setText("该用户选择了选项" + (i + 1));
                        break;
                    }
                }
                holder.tvVoteNum.setText("目前" + entity.voteAllNum + "人投票");
                holder.lvVote.setAdapter(new HomeVoteAdapter(mContext, entity.voteList, 3));
                if (entity.voteList.size() > 3) {
                    holder.tvVoteChoseNum.setVisibility(View.VISIBLE);
                    holder.tvVoteChoseNum.setText("4...等共" + entity.voteList.size() + "个选项");
                } else
                    holder.tvVoteChoseNum.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListner(ViewHolder holder, final int position) {
        holder.llRootView.setOnClickListener(new MyClickListner(position));
        holder.llUser.setOnClickListener(new MyClickListner(position));
        holder.gvPic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (onPlanDetailClickListener != null)
                        onPlanDetailClickListener.onPlanDetailClick(position);
                }
                return false;
            }
        });
        holder.lvVote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (onPlanDetailClickListener != null)
                    onPlanDetailClickListener.onPlanDetailClick(position);
            }
        });
    }

    /**
     * 监听器
     */
    class MyClickListner implements View.OnClickListener {
        int position;

        public MyClickListner(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_root_view:
                    if (onPlanDetailClickListener != null)
                        onPlanDetailClickListener.onPlanDetailClick(position);
                    break;
                case R.id.ll_user:
                    if (onUserDetailClickListener != null)
                        onUserDetailClickListener.onUserDetailClick(position);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 用户详情点击接口
     */
    private OnUserDetailClickListener onUserDetailClickListener;

    public void setOnUserDetailClickListener(OnUserDetailClickListener onUserDetailClickListener) {
        this.onUserDetailClickListener = onUserDetailClickListener;
    }

    public interface OnUserDetailClickListener {
        void onUserDetailClick(int position);
    }

    /**
     * 方案详情点击接口
     */
    private OnPlanDetailClickListener onPlanDetailClickListener;

    public void setOnPlanDetailClickListener(OnPlanDetailClickListener onPlanDetailClickListener) {
        this.onPlanDetailClickListener = onPlanDetailClickListener;
    }

    public interface OnPlanDetailClickListener {
        void onPlanDetailClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.view_top)
        LinearLayout viewTop;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
        @Bind(R.id.view_top_12)
        View viewTop12;
        @Bind(R.id.tv_pay_money)
        TextView tvPayMoney;
        @Bind(R.id.tv_commission_money)
        TextView tvCommissionMoney;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_role)
        TextView tvRole;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.gv_pic)
        FixedGridView gvPic;
        @Bind(R.id.iv_voice)
        ImageView ivVoice;
        @Bind(R.id.tv_start)
        TextView tvStart;
        @Bind(R.id.tv_second)
        TextView tvSecond;
        @Bind(R.id.rll_voice)
        RoundRelativeLayout rllVoice;
        @Bind(R.id.tv_vote)
        TextView tvVote;
        @Bind(R.id.tv_vote_num)
        TextView tvVoteNum;
        @Bind(R.id.lv_vote)
        FixedListView lvVote;
        @Bind(R.id.tv_vote_chose_num)
        TextView tvVoteChoseNum;
        @Bind(R.id.ll_vote)
        LinearLayout llVote;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_del)
        TextView tvDel;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.ll_comment)
        LinearLayout llComment;
        @Bind(R.id.iv_like)
        ImageView ivLike;
        @Bind(R.id.tv_like)
        TextView tvLike;
        @Bind(R.id.ll_like)
        LinearLayout llLike;
        @Bind(R.id.view_bottom)
        View viewBottom;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
