package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_News;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wikison on 2016/6/18.
 */
public class MyRecordAdapter extends BaseListAdapter<M_News> {

    /**
     * 方案详情点击接口
     */
    private OnPlanDetailClickListener onPlanDetailClickListener;
    private OnDelClickListener onDelClickListener;

    public MyRecordAdapter(Context context, List<M_News> list) {
        super(context, list);
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

    public void setNoData() {
        clearAll();
        notifyDataSetChanged();
    }

    public void setData(List<M_News> list, boolean isNewMerchant, boolean isLoadMore) {

        if (isNewMerchant || !isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_record, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

//        // 删除按钮可见
//        holder.tvDel.setVisibility(View.VISIBLE);

        M_News entity = getData().get(position);

        // 设置数据
        initData(holder, entity);
        // 设置监听器
        initListener(holder, position);

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
//        if (!TextUtils.isEmpty(entity.industryName)
//                && !TextUtils.isEmpty(entity.merchantName)) {
//            holder.tvPlanName.setText(entity.industryName + "/" + entity.merchantName);
//        } else if (!TextUtils.isEmpty(entity.industryName)
//                && TextUtils.isEmpty(entity.merchantName)) {
//            holder.tvPlanName.setText(entity.industryName);
//        } else if (TextUtils.isEmpty(entity.industryName)
//                && !TextUtils.isEmpty(entity.merchantName)) {
//            holder.tvPlanName.setText("/" + entity.merchantName);
//        }
//        // 方案内容
//        if (!TextUtils.isEmpty(entity.note))
//            holder.tvPlanContent.setText(entity.note);
//        // 点赞数
//        holder.tvLike.setText(entity.goodNum + "");
//        // 评论数
//        holder.tvComment.setText(entity.commentNum + "");
//        // 发布时间
//        if (!TextUtils.isEmpty(entity.createtime))
//            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));
//        //---------------------------------------------------
//        if (!TextUtils.isEmpty(entity.pic)) {
//            holder.gv.setVisibility(View.VISIBLE);
//            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, entity.pic);
//            holder.gv.setAdapter(adapter);
//        } else
//            holder.gv.setVisibility(View.GONE);
//        //---------------------------------------------------
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListener(ViewHolder holder, final int position) {
//        holder.llContent.setOnClickListener(new MyClickListner(position));
//        holder.tvDel.setOnClickListener(new MyClickListner(position));
//        holder.gv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (onPlanDetailClickListener != null)
//                        onPlanDetailClickListener.onPlanDetailClick(position);
//                }
//                return false;
//            }
//        });
    }

    public void setOnPlanDetailClickListener(OnPlanDetailClickListener onPlanDetailClickListener) {
        this.onPlanDetailClickListener = onPlanDetailClickListener;
    }

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public interface OnPlanDetailClickListener {
        void onPlanDetailClick(int position);
    }

    public interface OnDelClickListener {
        void onDelClick(int position);
    }

    static class ViewHolder {
//        @Bind(R.id.view_divider)
//        View viewDivider;
//        @Bind(R.id.tv_plan_name)
//        TextView tvPlanName;
//        @Bind(R.id.tv_plan_content)
//        TextView tvPlanContent;
//        @Bind(R.id.gv)
//        FixedGridView gv;
//        @Bind(R.id.tv_time)
//        TextView tvTime;
//        @Bind(R.id.tv_del)
//        TextView tvDel;
//        @Bind(R.id.tv_comment)
//        TextView tvComment;
//        @Bind(R.id.ll_comment)
//        LinearLayout llComment;
//        @Bind(R.id.tv_like)
//        TextView tvLike;
//        @Bind(R.id.ll_like)
//        LinearLayout llLike;
//        @Bind(R.id.ll_content)
//        LinearLayout llContent;
//        @Bind(R.id.ll_root_view)
//        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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
}

