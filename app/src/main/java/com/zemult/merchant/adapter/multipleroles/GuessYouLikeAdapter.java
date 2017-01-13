package com.zemult.merchant.adapter.multipleroles;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.RoleHomeActivity;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.adapter.slashfrgment.HomeVoteAdapter;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.aip.slash.ManagerAddmanagerRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/9/1.
 */
public class GuessYouLikeAdapter extends BaseListAdapter<M_Task> {


    private List<M_Task> mDatas = new ArrayList<M_Task>();
    ManagerAddmanagerRequest managerAddmanagerRequest;
    public static final String Call_SLASHMENUWINDOW_REFRESH = "call_SlashMenuWindow_refresh";

    private boolean isNoData, showAll, unshowUser;
    private int mHeight, allWidth;
    private float itemWidth;

    public GuessYouLikeAdapter(Context context, List<M_Task> list) {
        super(context, list);
        allWidth = DensityUtil.getWindowWidth((Activity) mContext) - DensityUtil.dip2px(mContext, 134);
    }

    /**
     * @param context
     * @param list
     * @param showAll (默认显示缩略图)全部显示传true
     */
    public GuessYouLikeAdapter(Context context, List<M_Task> list, boolean showAll) {
        super(context, list);
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
            convertView = mInflater.inflate(R.layout.item_guessyoulike, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        mDatas = getData();

        M_Task entity = mDatas.get(position);
        // 设置数据
        initData(holder, entity, position);
        // 设置监听器
        initListner(holder, position);

        return convertView;
    }

    private void initListner(ViewHolder holder, final int position) {
//        holder.llContent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onPlanDetailClickListener != null)
//                    onPlanDetailClickListener.onPlanDetailClick(position);
//            }
//        });

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


    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, final M_Task entity, final int position) {

        //角色头像
        if (!TextUtils.isEmpty(entity.industryIcon)) {
            mImageManager.loadCircleImage(entity.industryIcon, holder.headIv);
        }
        // 角色名称
        if (!TextUtils.isEmpty(entity.industryName))
            holder.nameTv.setText(entity.industryName);
        if (!TextUtils.isEmpty(entity.industryName))
            holder.addTv.setText("您探索#" + entity.industryName + "  获得了热切关注,快为自己新添一个斜杠吧~");

        // 角色标签说明
        if (!TextUtils.isEmpty(entity.industryTag))
            holder.noteTv.setText(entity.industryTag);
        //按钮

        if (entity.state == 0) {//未申请
            holder.goBtn.setText("申请");
            holder.goBtn.setBackgroundResource(R.drawable.roleapply_btn);
            holder.goBtn.setTextColor(mContext.getResources().getColor(R.color.bg_head));
            holder.goBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager_addmanager(entity.industryId, position); //申请
                }
            });

        } else if (entity.state == 1) {         //已经申请的状态
            holder.goBtn.setText("查看");
            holder.goBtn.setBackgroundResource(R.drawable.rolesee_btn);
            holder.goBtn.setTextColor(mContext.getResources().getColor(R.color.seego_color));
            holder.goBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, entity.industryId);
                    mContext.startActivity(intent);
                }
            });

        }

        // 完成时间
        if (!TextUtils.isEmpty(entity.completeTime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.completeTime) + "完成");
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

    //用户 成为某经营人角色(参与角色)
    private void manager_addmanager(int industryId, final int position) {
        if (managerAddmanagerRequest != null) {
            managerAddmanagerRequest.cancel();
        }
        ManagerAddmanagerRequest.Input input = new ManagerAddmanagerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.convertJosn();
        managerAddmanagerRequest = new ManagerAddmanagerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(mContext, "参与成功");
                    mDatas.get(position).state = 1;
                    EventBus.getDefault().post(Call_SLASHMENUWINDOW_REFRESH);
                    notifyDataSetChanged();  //改变按钮样式
                    Intent intent = new Intent(mContext, RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, mDatas.get(position).industryId);
                    mContext.startActivity(intent);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerAddmanagerRequest);
    }

//    static class ViewHolder {
//        @Bind(R.id.add_tv)
//        TextView addtv;
//        @Bind(R.id.head_iv)
//        ImageView headIv;
//        @Bind(R.id.name_tv)
//        TextView nameTv;
//        @Bind(R.id.note_tv)
//        TextView noteTv;
//        @Bind(R.id.go_btn)
//        Button goBtn;
//
//        @Bind(R.id.view_top_12)
//        View viewTop12;
//        @Bind(R.id.tv_content)
//        TextView tvContent;
//        @Bind(R.id.gv_pic)
//        FixedGridView gvPic;
//        @Bind(R.id.iv_voice)
//        ImageView ivVoice;
//        @Bind(R.id.tv_start)
//        TextView tvStart;
//        @Bind(R.id.tv_second)
//        TextView tvSecond;
//        @Bind(R.id.rll_voice)
//        RoundRelativeLayout rllVoice;
//        @Bind(R.id.tv_vote)
//        TextView tvVote;
//        @Bind(R.id.tv_vote_num)
//        TextView tvVoteNum;
//        @Bind(R.id.lv_vote)
//        FixedListView lvVote;
//        @Bind(R.id.ll_vote)
//        LinearLayout llVote;
//        @Bind(R.id.tv_time)
//        TextView tvTime;
//        @Bind(R.id.tv_del)
//        TextView tvDel;
//        @Bind(R.id.tv_comment)
//        TextView tvComment;
//        @Bind(R.id.ll_comment)
//        LinearLayout llComment;
//        @Bind(R.id.iv_like)
//        ImageView ivLike;
//        @Bind(R.id.tv_like)
//        TextView tvLike;
//        @Bind(R.id.ll_like)
//        LinearLayout llLike;
//        @Bind(R.id.ll_content)
//        LinearLayout llContent;
//        //        @Bind(R.id.view_bottom)
////        View viewBottom;
////        @Bind(R.id.ll_root_view)
////        LinearLayout llRootView;
//        @Bind(R.id.tv_vote_chose_num)
//        TextView tvVoteChoseNum;
//
//        ViewHolder(View view) {
//            ButterKnife.bind(this, view);
//        }
//    }

    static class ViewHolder {
        @Bind(R.id.add_tv)
        TextView addTv;
        @Bind(R.id.head_iv)
        ImageView headIv;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.note_tv)
        TextView noteTv;
        @Bind(R.id.go_btn)
        Button goBtn;
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
