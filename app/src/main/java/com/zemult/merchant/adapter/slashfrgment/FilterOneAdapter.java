package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.FilterEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterOneAdapter extends BaseListAdapter<FilterEntity> {

    private FilterEntity selectedEntity;

    public FilterOneAdapter(Context context, List<FilterEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterEntity filterEntity) {
        this.selectedEntity = filterEntity;
        for (FilterEntity entity : getData()) {
            entity.setSelected(entity.getKey().equals(selectedEntity.getKey()));
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_one, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FilterEntity entity = getItem(position);

        holder.tvTitle.setText(entity.getKey());
        holder.ivHead.setImageResource(entity.getMipmap());
        if (entity.isSelected()) {
            holder.ivHead.setImageResource(entity.getMipmapSelected());
            holder.ivSelect.setVisibility(View.VISIBLE);
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.ivHead.setImageResource(entity.getMipmap());
            holder.ivSelect.setVisibility(View.INVISIBLE);
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_666));
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.iv_select)
        ImageView ivSelect;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
