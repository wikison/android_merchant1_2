package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 查看角色任务的其它用户完成记录列表
 *
 * @author djy
 * @time 2016/7/28 14:45
 */
public class TaskCompleteAdapter extends BaseListAdapter<M_Task> {
    int cashType;
    int type;
    private OnItemClickListener onItemClickListener;
    private OnHeadClickListener onHeadClickListener;

    public TaskCompleteAdapter(Context context, List<M_Task> list) {
        super(context, list);
    }

    public TaskCompleteAdapter(Context context, List<M_Task> list, int type, int cashType) {
        super(context, list);
        this.type = type;
        this.cashType = cashType;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_task_complete, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 最后一项样式不同
        if (position == getData().size() - 1) {
            holder.dividerBottom.setVisibility(View.VISIBLE);
            holder.dividerCommon.setVisibility(View.GONE);
        } else {
            holder.dividerBottom.setVisibility(View.GONE);
            holder.dividerCommon.setVisibility(View.VISIBLE);
        }

        final M_Task entity = getItem(position);

        // 评论用户昵称
        if (!TextUtils.isEmpty(entity.userName))
            holder.tvName.setText(entity.userName);
        // 评论用户头像
        if (!TextUtils.isEmpty(entity.userHead))
            mImageManager.loadCircleImage(entity.userHead, holder.ivHead);


        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onHeadClickListener != null)
                    onHeadClickListener.onHeadClick(position);
            }
        });
        holder.rlContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    // 交易任务不可点击
                    if (entity.type != 3) {
                        onItemClickListener.onItemClick(position);
                    }
            }
        });

        if (type == 0) {
            holder.ivRight.setVisibility(View.VISIBLE);
            holder.tvFinishTask.setVisibility(View.VISIBLE);
            holder.tvBonuses.setVisibility(View.VISIBLE);
            // 完成时间
            if (!TextUtils.isEmpty(entity.completeTime)) {
                holder.tvTime.setVisibility(View.VISIBLE);
                holder.tvTime.setText(DateTimeUtil.strPubDiffTime(entity.completeTime));
            }
            if (cashType == 1) {
                if (entity.bonuseMoney > 0) {
                    holder.tvBonuses.setText("获得红包" + entity.bonuseMoney + "元");
                } else {
                    holder.tvBonuses.setText("");
                }
            } else if (cashType == 2) {
                if (entity.isVoucher == 1) {
                    holder.tvBonuses.setText("获得优惠券");
                } else {
                    holder.tvBonuses.setText("");
                }
            } else {
                holder.tvBonuses.setVisibility(View.GONE);
            }
        } else if (type == 3) {
            holder.tvTime.setVisibility(View.GONE);
            holder.tvFinishTask.setVisibility(View.GONE);
            holder.tvName.setText(entity.userName + "完成交易额" + (entity.payMoney == 0 ? "0" : entity.payMoney) + "元");
            holder.tvBonuses.setText("获得佣金" + (entity.commissionMoney == 0 ? "0" : entity.commissionMoney) + "元");
            holder.ivRight.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnHeadClickListener(OnHeadClickListener onHeadClickListener) {
        this.onHeadClickListener = onHeadClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnHeadClickListener {
        void onHeadClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.rl_content)
        RelativeLayout rlContent;
        @Bind(R.id.divider_common)
        LinearLayout dividerCommon;
        @Bind(R.id.divider_bottom)
        View dividerBottom;
        @Bind(R.id.tv_bonuses)
        TextView tvBonuses;
        @Bind(R.id.tv_finish_task)
        TextView tvFinishTask;
        @Bind(R.id.iv_right)
        ImageView ivRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
