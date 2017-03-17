package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Bill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 赞赏红包
 * @author djy
 * @time 2017/2/5 11:26
 */
public class SendRewardAdapter extends BaseListAdapter<M_Bill> {
    private Set<Integer> selectedPos =new HashSet<Integer>();

    public SendRewardAdapter(Context context, List<M_Bill> list) {
        super(context, list);
    }

    public void setSelected(Set<Integer>  pos){
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

        holder.tv.setText(getItem(position).name);
        holder.tvYuan.setText(getItem(position).money+"");
        if(selectedPos.contains(position) ){
            holder.tvYuan.setBackground(mContext.getResources().getDrawable(R.mipmap.zshb_selected));
        }else {
            holder.tvYuan.setBackground(mContext.getResources().getDrawable(R.mipmap.zshb_unselected));
        }


        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv)
        TextView tv;
        @Bind(R.id.tv_yuan)
        TextView tvYuan;
        @Bind(R.id.rl)
        LinearLayout rl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
