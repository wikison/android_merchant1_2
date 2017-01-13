package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Comment;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.EmojiParser;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/6/8.
 */
public class PlanCommentAdapter extends BaseListAdapter<M_Comment> {
    private boolean unshowLike;

    public PlanCommentAdapter(Context context, List<M_Comment> list) {
        super(context, list);
    }

    public PlanCommentAdapter(Context context, List<M_Comment> list, boolean unshowLike) {
        super(context, list);
        this.unshowLike = unshowLike;
    }

    public void setData(List<M_Comment> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    public void delComment(int pos) {
        getData().remove(pos);
        notifyDataSetChanged();
    }

    public void commentLike(int pos, int isGood) {
        getData().get(pos).isGood = isGood;
        if(isGood == 0)
            --getData().get(pos).goodNum;
        else
            ++getData().get(pos).goodNum;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_plan_comment, null);
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

        final M_Comment comment = getItem(position);

        // 评论内容
        if (!TextUtils.isEmpty(comment.note))
            holder.tvNote.setText(EmojiParser.getInstance(mContext).getExpressionString(comment.note));
        // 评论时间
        if (!TextUtils.isEmpty(comment.createtime))
            holder.tvTime.setText(DateTimeUtil.strPubDiffTime(comment.createtime));
        if (comment.type == 0) {
            holder.tvName.setText(comment.userName);
        } else {
            holder.tvName.setText(Html.fromHtml(comment.userName + "   " + "<font color='#999999'><small>回复</small></font>" + "   " + comment.ruserName));
        }
        // 评论用户头像
        if (!TextUtils.isEmpty(comment.userHead))
            mImageManager.loadCircleImage(comment.userHead, holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);

        holder.llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(position);
            }
        });
        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onHeadClickListener != null)
                    onHeadClickListener.onHeadClick(position);
            }
        });

        holder.llZan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onZanClickListener != null)
                    onZanClickListener.onZanClick(position, comment.isGood);
            }
        });

        if(unshowLike){
            holder.llZan.setVisibility(View.GONE);
        }else {
            holder.tvZanNum.setText(comment.goodNum + "");
            if (comment.isGood == 1)
                holder.ivZan.setImageResource(R.mipmap.zan_icon_sel);
            else
                holder.ivZan.setImageResource(R.mipmap.zan_icon_nor);
        }

        return convertView;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnHeadClickListener {
        void onHeadClick(int position);
    }

    private OnHeadClickListener onHeadClickListener;

    public void setOnHeadClickListener(OnHeadClickListener onHeadClickListener) {
        this.onHeadClickListener = onHeadClickListener;
    }
    public interface OnZanClickListener {
        void onZanClick(int position, int isGood);
    }

    private OnZanClickListener onZanClickListener;

    public void setOnZanClickListener(OnZanClickListener onZanClickListener) {
        this.onZanClickListener = onZanClickListener;
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_note)
        TextView tvNote;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_zan)
        ImageView ivZan;
        @Bind(R.id.tv_zan_num)
        TextView tvZanNum;
        @Bind(R.id.divider_common)
        View dividerCommon;
        @Bind(R.id.divider_bottom)
        View dividerBottom;
        @Bind(R.id.ll_content)
        LinearLayout llContent;
        @Bind(R.id.ll_zan)
        LinearLayout llZan;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
