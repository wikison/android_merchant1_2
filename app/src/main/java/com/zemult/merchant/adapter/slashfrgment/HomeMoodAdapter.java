package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.FixedListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页--心情小记adapter
 *
 * @author djy
 * @time 2016/7/25 14:58
 */
public class HomeMoodAdapter extends BaseListAdapter<M_News> {

    private boolean isNoData, isHome = true;
    private int mHeight;

    public HomeMoodAdapter(Context context, List<M_News> list) {
        super(context, list);
    }

    // 设置数据 心情小记
    public void setData(List<M_News> list, boolean isLoadMore) {
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

    // 设置数据 心情小记
    public void setDataSearch(List<M_News> list, boolean isLoadMore) {
        isHome = false;
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
    public void refreshOneRecord(M_News newsInfo, int pos) {
        getData().set(pos, newsInfo);
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

        // 设置数据 心情小记
        if(isHome){
            if(position == 0)
                holder.llTop.setVisibility(View.GONE);
            else
                holder.llTop.setVisibility(View.VISIBLE);
        }

        M_News entity = getItem(position);
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
    private void initData(ViewHolder holder, M_News entity) {
        holder.llTuwen.setVisibility(View.VISIBLE);
        holder.rllVoice.setVisibility(View.GONE);
        holder.llVote.setVisibility(View.GONE);
        holder.llBusiness.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(entity.pic)) { // 照片
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
            holder.gv.setAdapter(adapter);
            holder.gv.setVisibility(View.VISIBLE);
        } else
            holder.gv.setVisibility(View.GONE);

        holder.ivTitle.setImageResource(R.mipmap.xingqingxiaoji_icon);  // 图标
        // 发布时间
        if (!TextUtils.isEmpty(entity.createtime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));
        // 标题
        holder.tvTitle.setText("心情小记");
        // 子标题
        if (!TextUtils.isEmpty(entity.industryName))
            holder.tvSubTitle.setText("来自“" + entity.industryName + "”的记录");

        // 是否显示多重角色
        if (entity.userIndustryList == null
                || entity.userIndustryList.isEmpty()
                || entity.userIndustryNum == 0)
            holder.llUserIndustry.setVisibility(View.GONE);
        else {
            holder.llUserIndustry.setVisibility(View.VISIBLE);
            String industryName = "";

            for (M_UserRole userRole : entity.userIndustryList) {
                if (!TextUtils.isEmpty(userRole.industryName)
                        && !TextUtils.isEmpty(userRole.industryLevel))
                    industryName += "Lv" + userRole.industryLevel + " " + userRole.industryName + "/";
            }
            if (industryName.lastIndexOf("/") == industryName.length() - 1)
                industryName = industryName.substring(0, industryName.length() - 1);

            holder.tvUserIndustry.setText(industryName);
            holder.tvUserIndustryNum.setText(entity.userIndustryNum + "重角色");
        }

        // 内容
        if (!TextUtils.isEmpty(entity.note))
            holder.tvContent.setText(entity.note);
        // 用户头像
        if (!TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage(entity.userHead, holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.userName))
            holder.tvUserName.setText(entity.userName);
        // 完成任务的用户性别(0男,1女)
        switch (entity.userSex){
            case 0:
                holder.tvUserName.setTextColor(0xff2fc4fc);
                break;
            case 1:
                holder.tvUserName.setTextColor(0xfff75cb3);
                break;
        }
        // 用户的等级
        holder.tvUserLevel.setText(entity.userLevel + "");
        // 点赞数
        holder.tvLike.setText(entity.goodNum + "");
        // 评论数
        holder.tvComment.setText(entity.commentNum + "");

        if(entity.isGood == 1)
            holder.ivLike.setImageResource(R.mipmap.zan_icon_sel);
        else
            holder.ivLike.setImageResource(R.mipmap.zan_icon);
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListner(ViewHolder holder, final int position) {
        holder.llContent.setOnClickListener(new MyClickListner(position));
        holder.llUser.setOnClickListener(new MyClickListner(position));
        holder.gv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (onPlanDetailClickListener != null)
                        onPlanDetailClickListener.onPlanDetailClick(position);
                }
                return false;
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
                case R.id.ll_content:
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
        @Bind(R.id.ll_top)
        LinearLayout llTop;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.iv_sex)
        ImageView ivSex;
        @Bind(R.id.iv_like)
        ImageView ivLike;
        @Bind(R.id.tv_user_level)
        TextView tvUserLevel;
        @Bind(R.id.tv_user_industry)
        TextView tvUserIndustry;
        @Bind(R.id.tv_user_industry_num)
        TextView tvUserIndustryNum;
        @Bind(R.id.ll_user_industry)
        LinearLayout llUserIndustry;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
        @Bind(R.id.iv_title)
        ImageView ivTitle;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_sub_title)
        TextView tvSubTitle;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.gv)
        FixedGridView gv;
        @Bind(R.id.ll_tuwen)
        LinearLayout llTuwen;
        @Bind(R.id.tv_second)
        TextView tvSecond;
        @Bind(R.id.rll_voice)
        RoundRelativeLayout rllVoice;
        @Bind(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @Bind(R.id.tv_pay_money)
        TextView tvPayMoney;
        @Bind(R.id.ll_business)
        LinearLayout llBusiness;
        @Bind(R.id.lv_vote)
        FixedListView lvVote;
        @Bind(R.id.ll_vote)
        LinearLayout llVote;
        @Bind(R.id.rll)
        RoundLinearLayout rll;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_del)
        TextView tvDel;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.ll_comment)
        LinearLayout llComment;
        @Bind(R.id.tv_like)
        TextView tvLike;
        @Bind(R.id.ll_like)
        LinearLayout llLike;
        @Bind(R.id.ll_content)
        LinearLayout llContent;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
