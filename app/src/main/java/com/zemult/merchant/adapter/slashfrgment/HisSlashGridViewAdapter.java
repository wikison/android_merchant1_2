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
 * 用户详情页-他的斜杠
 */
public class HisSlashGridViewAdapter extends BaseListAdapter<M_Industry> {
    public HisSlashGridViewAdapter(Context context) {
        super(context);
    }

    public HisSlashGridViewAdapter(Context context, List<M_Industry> list) {
        super(context, list);
    }

    public void setData(List<M_Industry> list){
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_his_slash, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Industry industry = getData().get(pos);
        // 场景头像 地址
        if (!TextUtils.isEmpty(industry.icon)) {
            mImageManager.loadCircleHasBorderImage(industry.icon, holder.iv, 0xffb5b5b5, 1);
        }
        holder.tvLevel.setText("Lv." + industry.level);
        // 场景名称
        if (!TextUtils.isEmpty(industry.name))
            holder.tv.setText(industry.name);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv_level)
        TextView tvLevel;
        @Bind(R.id.tv)
        TextView tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
