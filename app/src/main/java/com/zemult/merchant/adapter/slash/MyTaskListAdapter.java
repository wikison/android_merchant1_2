package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/7/29.
 */
public class MyTaskListAdapter extends BaseListAdapter<M_Task> {

    private int pagePosition = 0;
    /**
     * 任务按钮点击
     */
    private OnTextTaskClickListener onTextTaskClickListener;

    public MyTaskListAdapter(Context context, List<M_Task> mTaskList) {
        super(context, mTaskList);
    }

    public MyTaskListAdapter(Context context, List<M_Task> mTaskList, int pagePosition) {
        super(context, mTaskList);
        this.pagePosition = pagePosition;
    }

    public void setData(List<M_Task> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_task, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Task entity = getItem(position);

        // 设置数据
        initData(holder, entity);
        // 设置监听器
        initListener(holder, position);
        return convertView;
    }

    private void initData(ViewHolder holder, M_Task entity) {
        //字符串_剩余数量信息显示
        switch (entity.type) {
            case 0:
                holder.ivTypeIcon.setImageResource(R.mipmap.fengxiang_icon);
                break;
            case 1:
                holder.ivTypeIcon.setImageResource(R.mipmap.yuying_icon);
                break;
            case 2:
                holder.ivTypeIcon.setImageResource(R.mipmap.toupiao_icon);
                break;
            case 3:
                holder.ivTypeIcon.setImageResource(R.mipmap.xiaofei_icon);
                break;
        }

        switch (entity.cashType) {
            case 0:
                holder.tvBonuses.setVisibility(View.GONE);
                holder.tvVoucher.setVisibility(View.GONE);
                break;
            case 1:
                holder.tvBonuses.setVisibility(View.VISIBLE);
                holder.tvVoucher.setVisibility(View.GONE);
                break;
            case 2:
                holder.tvBonuses.setVisibility(View.GONE);
                holder.tvVoucher.setVisibility(View.VISIBLE);
                holder.tvVoucher.setText(String.format("代金券%s元", entity.voucherMoney));
                break;
        }

        switch (pagePosition) {
            case 0:
                holder.rtvButton.setText(mContext.getResources().getString(R.string.text_receive_task));
                break;
            case 1:
                holder.rtvButton.setText(mContext.getResources().getString(R.string.text_do_task));
                break;
            case 2:
            case 3:
                holder.rtvButton.setText(mContext.getResources().getString(R.string.text_view_task));
                break;
        }

        holder.tvTitle.setText(entity.title);
        holder.tvExp.setText(String.format("经验X%s", String.valueOf(entity.experience)));
        if (pagePosition != 3) {
            holder.tvNote.setText(String.format("%sde任务 %s结束", entity.industryName,
                    "| " + DateTimeUtil.strPubEndDiffTime(entity.endtime)));
        } else {
            holder.tvNote.setText(String.format("%sde任务 %s结束", entity.industryName,
                    "| " + DateTimeUtil.strPubDiffTime(entity.endtime)));
        }

        mImageManager.loadCircleHasBorderImage(entity.userHead, holder.ivPublishIcon, mContext.getResources().getColor(R.color.border_color), 1);
        holder.tvPublishName.setText(entity.userName);
        holder.tvPublishTime.setText(DateTimeUtil.strPubDiffTime(entity.createtime) + "发布");
    }

    private void initListener(ViewHolder holder, int position) {
        holder.rtvButton.setOnClickListener(new MyClickListener(position));
    }

    public void setOnTextTaskClickListener(OnTextTaskClickListener onTextTaskClickListener) {
        this.onTextTaskClickListener = onTextTaskClickListener;
    }

    public interface OnTextTaskClickListener {
        void onTextTaskClick(int position);
    }

    class MyClickListener implements View.OnClickListener {
        int position;

        public MyClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onTextTaskClickListener != null)
                onTextTaskClickListener.onTextTaskClick(position);
        }
    }

    class ViewHolder {
        @Bind(R.id.iv_type_icon)
        ImageView ivTypeIcon;
        @Bind(R.id.tv_task_type)
        TextView tvTitle;
        @Bind(R.id.tv_task_state)
        TextView tvNote;
        @Bind(R.id.tv_exp)
        TextView tvExp;
        @Bind(R.id.tv_bonuses)
        TextView tvBonuses;
        @Bind(R.id.tv_voucher)
        TextView tvVoucher;
        @Bind(R.id.rll_bonus)
        RoundRelativeLayout rllBonus;
        @Bind(R.id.rtv_button)
        RoundTextView rtvButton;
        @Bind(R.id.iv_publish_icon)
        ImageView ivPublishIcon;
        @Bind(R.id.tv_publish_name)
        TextView tvPublishName;
        @Bind(R.id.tv_publish_time)
        TextView tvPublishTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

