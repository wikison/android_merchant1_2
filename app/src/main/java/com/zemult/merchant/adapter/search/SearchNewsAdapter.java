package com.zemult.merchant.adapter.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.view.FixedGridView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/6/29.
 */


/**
 * 搜索场景角色Adapter
 */
public class SearchNewsAdapter extends BaseListAdapter<M_News> {

    private int needHeight;

    public SearchNewsAdapter(Context context, List<M_News> list) {
        super(context, list);
    }

    // 不添加空数据
    public void setDataNoFill(List<M_News> list, boolean isLoadMore,boolean isNewSearch) {
        if (!isLoadMore || isNewSearch) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    // 刷新单条记录
    public void refreshOneRecord(M_News newsInfo, int pos) {
        getData().get(pos).goodNum = newsInfo.goodNum;
        getData().get(pos).commentNum = newsInfo.commentNum;

        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 正常数据
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_home_child, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (position == 0)
            holder.viewDivider.setVisibility(View.GONE);
        else
            holder.viewDivider.setVisibility(View.VISIBLE);

        M_News entity = getItem(position);
        // 用户是否公开工作经历(0:否,1:是)
//        if (entity.isOpen == 0 || (TextUtils.isEmpty(entity.company)
//                && TextUtils.isEmpty(entity.position)))
//            holder.llJob.setVisibility(View.GONE);
//        else
//            holder.llJob.setVisibility(View.VISIBLE);

        holder.ivRight.setVisibility(View.INVISIBLE);
//        holder.ivSlash.setVisibility(View.GONE);
        holder.llRootView.setVisibility(View.VISIBLE);

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
        // 用户头像
        if (TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage("", holder.ivHead);
        else
            mImageManager.loadCircleImage(entity.userHead, holder.ivHead);
        // 用户名
        if (!TextUtils.isEmpty(entity.userName))
            holder.tvName.setText(entity.userName);
        // 公司职位
//        if (!TextUtils.isEmpty(entity.company)
//                && !TextUtils.isEmpty(entity.position)) {
//            holder.tvJob.setText(entity.company + "/" + entity.position);
//        } else if (!TextUtils.isEmpty(entity.company)
//                && TextUtils.isEmpty(entity.position)) {
//            holder.tvJob.setText(entity.company);
//        } else if (TextUtils.isEmpty(entity.company)
//                && !TextUtils.isEmpty(entity.position)) {
//            holder.tvJob.setText("/" + entity.position);
//        }
//        // 场景方案公司职位
//        if (!TextUtils.isEmpty(entity.merchantName)
//                && !TextUtils.isEmpty(entity.industryName)) {
//            holder.tvPlanName.setText(entity.merchantName + "/" + entity.industryName);
//
//        } else if (!TextUtils.isEmpty(entity.merchantName)
//                && TextUtils.isEmpty(entity.industryName)) {
//            holder.tvPlanName.setText(entity.merchantName);
//
//        } else if (TextUtils.isEmpty(entity.merchantName)
//                && !TextUtils.isEmpty(entity.industryName)) {
//            holder.tvPlanName.setText("/" + entity.industryName);
//        }
//        // 方案内容
//        if (!TextUtils.isEmpty(entity.note))
//            holder.tvPlanContent.setText(entity.note);
        // 点赞数
        holder.tvLike.setText(entity.goodNum + "");
        // 评论数
        holder.tvComment.setText(entity.commentNum + "");

        if (entity.sex == 0)
            holder.ivSex.setImageResource(R.mipmap.man_icon);
        else
            holder.ivSex.setImageResource(R.mipmap.woman);

        if(!TextUtils.isEmpty(entity.createtime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));
        //---------------------------------------------------
        if (!TextUtils.isEmpty(entity.pic)) {
            holder.gv.setVisibility(View.VISIBLE);
            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
            holder.gv.setAdapter(adapter);
        } else
            holder.gv.setVisibility(View.GONE);
        //---------------------------------------------------
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
        @Bind(R.id.view_divider)
        View viewDivider;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.iv_sex)
        ImageView ivSex;
//        @Bind(R.id.iv_slash)
//        ImageView ivSlash;
//        @Bind(R.id.tv_job)
//        TextView tvJob;
        @Bind(R.id.iv_right)
        ImageView ivRight;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
//        @Bind(R.id.tv_plan_name)
//        TextView tvPlanName;
//        @Bind(R.id.tv_plan_content)
//        TextView tvPlanContent;
        @Bind(R.id.gv)
        FixedGridView gv;
        @Bind(R.id.tv_time)
        TextView tvTime;
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
//        @Bind(R.id.ll_job)
//        LinearLayout llJob;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
