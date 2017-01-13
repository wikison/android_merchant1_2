package com.zemult.merchant.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 心情详情页头部
 *
 * @author djy
 * @time 2016/7/29 16:00
 */
public class HeaderMoodDetailView extends HeaderViewInterface2<M_News> {

    @Bind(R.id.iv_title)
    ImageView ivTitle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv_industry_name)
    TextView tvIndustryName;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.iv_sex)
    ImageView ivSex;
    @Bind(R.id.tv_user_level)
    TextView tvUserLevel;
    @Bind(R.id.tv_friend_dimension)
    TextView tvFriendDimension;
    @Bind(R.id.tv_user_industry)
    TextView tvUserIndustry;
    @Bind(R.id.tv_user_industry_num)
    TextView tvUserIndustryNum;
    @Bind(R.id.ll_user_industry)
    LinearLayout llUserIndustry;
    @Bind(R.id.ll_user)
    LinearLayout llUser;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.gv)
    FixedGridView gv;
    @Bind(R.id.ll_tuwen)
    LinearLayout llTuwen;
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

    public HeaderMoodDetailView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(M_News news, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.header_mood_detail_layout, viewGroup, false);
        ButterKnife.bind(this, view);
        viewGroup.addView(view);
    }

    public void dealWithTheView(M_News entity) {
        if (!TextUtils.isEmpty(entity.pic)) { // 照片
            int width = DensityUtil.getWindowWidth(mContext) - DensityUtil.dip2px(mContext, 24);
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
            gv.setAdapter(adapter);
            gv.setVisibility(View.VISIBLE);

            adapter.setOnImageClickListener(new PhotoFix3Adapter.OnImageClickListener() {
                @Override
                public void onImageClick(int pos, List<String> photos) {
                    AppUtils.toImageDetial(mContext, pos, photos, false);
                }
            });
        } else
            gv.setVisibility(View.GONE);

        // 发布时间
        if (!TextUtils.isEmpty(entity.createtime))
            tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));

        // 是否显示多重角色
        if (entity.userIndustryList == null
                || entity.userIndustryList.isEmpty()
                || entity.userIndustryNum == 0)
            llUserIndustry.setVisibility(View.GONE);
        else {
            llUserIndustry.setVisibility(View.VISIBLE);
            String industryName = "";

            for (M_UserRole userRole : entity.userIndustryList) {
                if (!TextUtils.isEmpty(userRole.industryName)
                        && !TextUtils.isEmpty(userRole.industryLevel))
                    industryName += "Lv" + userRole.industryLevel + " " + userRole.industryName + "/";
            }
            if (industryName.lastIndexOf("/") == industryName.length() - 1)
                industryName = industryName.substring(0, industryName.length() - 1);

            tvUserIndustry.setText(industryName);
            tvUserIndustryNum.setText(entity.userIndustryNum + "重角色");
        }
        // 角色名字
        if (!TextUtils.isEmpty(entity.industryName))
            tvIndustryName.setText(entity.industryName);
        // 内容
        if (!TextUtils.isEmpty(entity.note))
            tvContent.setText(entity.note);
        // 用户头像
        if (!TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage(entity.userHead, ivHead);
        else
            ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.userName))
            tvUserName.setText(entity.userName);
        // 完成任务的用户性别(0男,1女)
        switch (entity.userSex){
            case 0:
                tvUserName.setTextColor(0xff2fc4fc);
                break;
            case 1:
                tvUserName.setTextColor(0xfff75cb3);
                break;
        }
        // 用户的等级
        tvUserLevel.setText(entity.userLevel + "");
        // 好友维度(0:非好友,1:1度好友,2:2度好友)
        switch (entity.friendDimension) {
            case 0:
                tvFriendDimension.setText("非好友");
                break;
            case 1:
                tvFriendDimension.setText("一度");
                break;
            case 2:
                tvFriendDimension.setText("二度");
                break;
        }
        // 点赞数
        tvLike.setText(entity.goodNum + "");
        // 评论数
        tvComment.setText(entity.commentNum + "");
        if(entity.userId == SlashHelper.userManager().getUserId())
            tvFriendDimension.setVisibility(View.INVISIBLE);
        else
            tvFriendDimension.setVisibility(View.VISIBLE);

        llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserClickListener != null) {
                    onUserClickListener.onUserClick();
                }
            }
        });
    }

    public void setCommentNum(int num){
        // 评论数
        tvComment.setText(num + "");
    }
    public void setLikeNum(int num){
        // 点赞数
        tvLike.setText(num + "");
    }

    public interface OnUserClickListener {
        void onUserClick();
    }

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }
}