package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Industry;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FilterLeftAdapter extends BaseListAdapter<M_Industry> {

    private M_Industry selectedEntity;

    public FilterLeftAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    public void setData(List<M_Industry> list) {
        M_Industry all_jiaose = new M_Industry();
        all_jiaose.industryId = -1;
        all_jiaose.name = "全部角色";
        list.add(0, all_jiaose);

        clearAll();
        addALL(list);

        // 用户详情页的用户任务会被发消息遮挡，故加几个空数据
        M_Industry empty = new M_Industry();
        empty.industryId = 0;
        empty.name = "";
        getData().add(empty);
        getData().add(empty);
        getData().add(empty);

        notifyDataSetChanged();
    }

    public void setSelectedEntity(M_Industry filterEntity) {
        this.selectedEntity = filterEntity;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filter_left, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Industry entity = getItem(position);

        holder.tvTitle.setText(entity.name);
        if (entity.industryId == selectedEntity.industryId) {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else {
            holder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.font_black_888));
        }

        if (entity.industryId == 0)
            holder.divider.setVisibility(View.GONE);
        else
            holder.divider.setVisibility(View.VISIBLE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.divider)
        View divider;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
