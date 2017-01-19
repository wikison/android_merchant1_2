package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 联想适配器
 *
 * @author djy
 * @time 2017/1/19 10:05
 */
public class ThinkingAdapter extends BaseListAdapter<M_Merchant> {

    public ThinkingAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list){
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_thinking, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Merchant merchant = getData().get(pos);
        // 场景名称
        if (!TextUtils.isEmpty(merchant.name))
            holder.tvName.setText(merchant.name);
        // 距离(center不为空时计算)
        if (!TextUtils.isEmpty(merchant.distance)
                && !"0".equals(merchant.distance)) {
            float distance = Float.valueOf(merchant.distance) / 1000;
            holder.tvDistance.setText(distance + "km");
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_fdj)
        ImageView ivFdj;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_distance)
        TextView tvDistance;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
