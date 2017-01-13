package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Industry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 16/4/20.
 */
public class HeaderChannelAdapter extends BaseListAdapter<M_Industry> {

    public HeaderChannelAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_channel, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Industry entity = getItem(position);

        // 行业名称
        if(!TextUtils.isEmpty(entity.name))
            holder.tvTitle.setText(entity.name);
        // 行业图标
        if(!TextUtils.isEmpty(entity.icon))
            mImageManager.loadCircleImage(entity.icon, holder.ivImage);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.tv_tips)
        TextView tvTips;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
