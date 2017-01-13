package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Vote;
import com.zemult.merchant.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeVoteAdapter extends BaseAdapter {
    int allWidth;
    float itemWidth;
    protected Context mContext;
    private List<M_Vote> mList;

    /**
     * @param context
     * @param list
     * @param maxSize
     */
    public HomeVoteAdapter(Context context, List<M_Vote> list, int maxSize) {
        mContext = context;

        if (list.size() > maxSize) {
            List<M_Vote> newList = new ArrayList<>();
            for (int i = 0; i < maxSize; i++) {
                newList.add(list.get(i));
            }
            mList = newList;
        } else
            mList = list;

        allWidth = DensityUtil.getWindowWidth((Activity) mContext) - DensityUtil.dip2px(mContext, 12);
    }

    public void setData(List<M_Vote> list, int maxSize) {
        mList.clear();
        for (int i = 0; i < maxSize; i++) {
            mList.add(list.get(i));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public M_Vote getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_vote, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Vote entity = getItem(position);

        if (!TextUtils.isEmpty(entity.voteNote))
            holder.tvVote.setText((position + 1) + "." + entity.voteNote);
        if (!TextUtils.isEmpty(entity.voteDiscount)) {
            holder.tvPercent.setText(entity.voteDiscount.replace(".00", ""));
            if (entity.voteDiscount.contains("%")) {
                float percent = (Float.valueOf(entity.voteDiscount.replace("%", "")));

                itemWidth = (float) allWidth * (percent / 100);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.llCover.getLayoutParams();
                lp.width = (int) itemWidth;
                holder.llCover.setLayoutParams(lp);
            }
        }

        if(entity.isSelected()){
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.llCover.setBackgroundColor(Color.parseColor("#fde59c"));
        }else {
            holder.ivCheck.setVisibility(View.INVISIBLE);
            holder.llCover.setBackgroundColor(Color.parseColor("#e4f8ff"));
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.ll_cover)
        LinearLayout llCover;
        @Bind(R.id.iv_check)
        ImageView ivCheck;
        @Bind(R.id.tv_vote)
        TextView tvVote;
        @Bind(R.id.tv_percent)
        TextView tvPercent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
