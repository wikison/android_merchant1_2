package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_News;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.FixedGridView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 0165 全部记录页
 */
public class AllRecordAdapter extends BaseListAdapter<M_News> {
    private boolean isNoData;
    private int mHeight;
    private int userId;

    public AllRecordAdapter(Context context, List<M_News> list, int userId) {
        super(context, list);
        this.userId = userId;
    }

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

    // 刷新单条记录
    public void refreshOneRecord(M_News newsInfo, int pos) {
        getData().get(pos).goodNum = newsInfo.goodNum;
        getData().get(pos).commentNum = newsInfo.commentNum;

        notifyDataSetChanged();
    }
    // 删除单条记录
    public void delOneRecord(int pos){
        getData().remove(pos);
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

        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.item_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        if (position == 0)
            holder.viewDivider.setVisibility(View.GONE);
        else
            holder.viewDivider.setVisibility(View.VISIBLE);

        // 只有看自己的全部记录时候 才能显示删除
        if(userId == SlashHelper.userManager().getUserId())
            holder.tvDel.setVisibility(View.VISIBLE);

        M_News entity = getData().get(position);

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
//
//
//        // 方案内容
//        if (!TextUtils.isEmpty(entity.note))
//            holder.tvPlanContent.setText(entity.note);
        // 点赞数
        holder.tvLike.setText(entity.goodNum + "");
        // 评论数
        holder.tvComment.setText(entity.commentNum + "");

        if (!TextUtils.isEmpty(entity.createtime))
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
        holder.tvDel.setOnClickListener(new MyClickListner(position));
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
                case R.id.tv_del:
                    if (onDelClickListener != null)
                        onDelClickListener.onDelClick(position);
                    break;
            }
        }
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
     * 方案删除点击接口
     */
    public interface OnDelClickListener {
        void onDelClick(int position);
    }
    private OnDelClickListener onDelClickListener;

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    static class ViewHolder {
        @Bind(R.id.view_divider)
        View viewDivider;
//        @Bind(R.id.tv_plan_name)
//        TextView tvPlanName;
//        @Bind(R.id.tv_plan_content)
//        TextView tvPlanContent;
        @Bind(R.id.gv)
        FixedGridView gv;
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
