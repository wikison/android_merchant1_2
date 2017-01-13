package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.FilterEntity;
import com.zemult.merchant.model.M_Industry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ClassifyAdapter extends BaseListAdapter<M_Industry> {

    private M_Industry selectedEntity;

    public ClassifyAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    public void setSelectedEntity(M_Industry filterEntity) {
        this.selectedEntity = filterEntity;
        for (M_Industry entity : getData()) {
            entity.setSelected(entity.id == selectedEntity.id);
        }
        notifyDataSetChanged();
    }

    public M_Industry getSelectedEntity(){
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
        holder.devider.setVisibility(View.GONE);

        M_Industry entity = getItem(position);

        holder.tvTitle.setText(entity.name);
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
