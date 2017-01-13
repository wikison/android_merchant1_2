package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchBusinessAdapter extends BaseListAdapter<M_Merchant> {

    public SearchBusinessAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list, boolean isLoadMore) {
        if (!isLoadMore)
            clearAll();

        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_business, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Merchant entity = getItem(position);

        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
        if (!TextUtils.isEmpty(entity.head))
            mImageManager.loadCircleImage(entity.head, holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.merchant_unline);

        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
        if (!TextUtils.isEmpty(entity.address))
            holder.tvAddress.setText(entity.address);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_address)
        TextView tvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
