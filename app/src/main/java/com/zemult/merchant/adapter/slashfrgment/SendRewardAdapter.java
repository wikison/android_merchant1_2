package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 赞赏红包
 * @author djy
 * @time 2017/2/5 11:26
 */
public class SendRewardAdapter extends BaseListAdapter<String> {
    private int selectedPos = -1;

    public SendRewardAdapter(Context context, List<String> list) {
        super(context, list);
    }

    public void setSelected(int pos){
        selectedPos = pos;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reward, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        holder.tv.setText(getItem(position));
        if(selectedPos == position){
            holder.rl.getDelegate().setBackgroundColor(0xffd84e43);
            holder.tv.setTextColor(mContext.getResources().getColor(R.color.white));
            holder.tvYuan.setTextColor(mContext.getResources().getColor(R.color.white));
        }else {
            holder.rl.getDelegate().setBackgroundColor(0xffffffff);
            holder.tv.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
            holder.tvYuan.setTextColor(mContext.getResources().getColor(R.color.bg_head_red));
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv)
        TextView tv;
        @Bind(R.id.tv_yuan)
        TextView tvYuan;
        @Bind(R.id.rl)
        RoundLinearLayout rl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
