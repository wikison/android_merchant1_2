package com.zemult.merchant.adapter.multipleroles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.FilterEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReportAdapter extends BaseListAdapter<FilterEntity> {

    private FilterEntity selectedEntity;

    public ReportAdapter(Context context, List<FilterEntity> list) {
        super(context, list);
    }

    public void setSelectedEntity(FilterEntity filterEntity) {
        this.selectedEntity = filterEntity;
        for (FilterEntity entity : getData()) {
            entity.setSelected(entity.getKey().equals(selectedEntity.getKey()));
        }
        notifyDataSetChanged();
    }

    public FilterEntity getSelectedEntity(){
        return selectedEntity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_report, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position == getCount() - 1)
            holder.devider.setVisibility(View.GONE);
        else
            holder.devider.setVisibility(View.VISIBLE);

        FilterEntity entity = getItem(position);

        holder.tvTitle.setText(entity.getKey());
        if (entity.isSelected())
            holder.ivSelect.setVisibility(View.VISIBLE);
        else
            holder.ivSelect.setVisibility(View.INVISIBLE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.iv_select)
        ImageView ivSelect;
        @Bind(R.id.devider)
        View devider;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
