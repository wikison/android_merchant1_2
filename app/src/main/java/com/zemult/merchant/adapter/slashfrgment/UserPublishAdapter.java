package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 任务记录adapter
 *
 * @author djy
 * @time 2016/7/25 14:58
 */
public class UserPublishAdapter extends BaseListAdapter<M_Task> {

    private boolean isNoData, loadMoreEnable = true;
    private int mHeight;

    public UserPublishAdapter(Context context, List<M_Task> list) {
        super(context, list);
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
            convertView = mInflater.inflate(R.layout.item_user_detail_child, null);
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
        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, M_Task entity) {
        // 发布时间
        if (!TextUtils.isEmpty(entity.createtime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime));
        // 子标题
        if (!TextUtils.isEmpty(entity.industryName))
            holder.tvRight.setText(entity.industryName + "de任务   |   " + entity.recordNum + "人参与");
        // 评论数
        holder.tvComment.setText(entity.commentNum + "");
        // 内容
        if (!TextUtils.isEmpty(entity.title)) {
            CharSequence text = "";
            if (entity.recordNum >= 50) {
                text = entity.title + "   ★";
                if (entity.cashType == 1)
                    text = text + " ★";
            } else if (entity.cashType == 1)
                text = entity.title + "   ★";
            else {
                holder.tvTitle.setText(entity.title);
                return;
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            String rexgString = "★";
            Pattern pattern = Pattern.compile(rexgString);
            Matcher matcher = pattern.matcher(text);

            int num = 1;
            while (matcher.find()) {
                if (num == 2)
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.jiang_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else if (entity.recordNum >= 50)
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.re_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                else
                    builder.setSpan(
                            new ImageSpan(mContext, R.mipmap.jiang_icon, ImageSpan.ALIGN_BASELINE), matcher.start(), matcher
                                    .end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                num++;
            }
            holder.tvTitle.setText(builder);
        }
    }

    static class ViewHolder {
        @Bind(R.id.view_top)
        LinearLayout viewTop;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_right)
        TextView tvRight;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.ll_comment)
        LinearLayout llComment;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;
        @Bind(R.id.view_bottom)
        View viewBottom;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
