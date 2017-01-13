package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
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
 * 0162全部场景页
 */
public class AllSlashAdapter extends BaseListAdapter<M_Industry> {

    public AllSlashAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    public void setData(List<M_Industry> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_one_slash, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        if(position == 0)
            holder.viewTop.setVisibility(View.VISIBLE);
        else
            holder.viewTop.setVisibility(View.GONE);

        M_Industry industry = getData().get(position);
        // 场景头像 地址
        if (!TextUtils.isEmpty(industry.icon))
            mImageManager.loadCircleHasBorderImage(industry.icon, holder.iv, 0xffdcdcdc, 1);
        else
            holder.iv.setImageResource(R.mipmap.user_icon);
        // 场景名称
        if (!TextUtils.isEmpty(industry.name))
            holder.tv.setText(industry.name);
        // 角色等级
        holder.tvUserLevel.setText(industry.level + "");
        // 标签说明
        if (!TextUtils.isEmpty(industry.tag))
            holder.tvDescribe.setText(industry.tag);

        if (industry.isSelected())
            holder.ivSelected.setVisibility(View.VISIBLE);
        else
            holder.ivSelected.setVisibility(View.INVISIBLE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.view_top)
        View viewTop;
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv)
        TextView tv;
        @Bind(R.id.tv_user_level)
        TextView tvUserLevel;
        @Bind(R.id.tv_describe)
        TextView tvDescribe;
        @Bind(R.id.iv_selected)
        ImageView ivSelected;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
