package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
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

public class FilterRightAdapter extends BaseListAdapter<M_Merchant> {

    private M_Merchant selectedEntity;

    public FilterRightAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list){
        clearAll();
        addALL(list);

        M_Merchant all_changjing = new M_Merchant();
        all_changjing.merchantId = -1;
        all_changjing.name = "全部场景";
        getData().add(0, all_changjing);

        setSelectedEntity(getItem(0));
    }

    public void setSelectedEntity(M_Merchant filterEntity) {
        this.selectedEntity = filterEntity;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_right, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Merchant entity = getItem(position);

        holder.tvTitle.setText(entity.name);
        if (entity.merchantId == selectedEntity.merchantId) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_999));
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.tv_title)
        TextView tvTitle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
