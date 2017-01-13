package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
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
 * 0162全部场景页
 */
public class AllChangjingAdapter extends BaseListAdapter<M_Merchant> {

    public AllChangjingAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list, boolean isLoadMore){
        if(!isLoadMore){
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_one_changjing, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Merchant merchant = getData().get(position);
        // 场景头像 地址
        if(!TextUtils.isEmpty(merchant.head)){
            mImageManager.loadCircleHasBorderImage(merchant.head, holder.iv
                    ,mContext.getResources().getColor(R.color.divider_c1), 1);
        }else {
            holder.iv.setImageResource(R.mipmap.merchant_online);
        }
        // 场景名称
        if(!TextUtils.isEmpty(merchant.name))
            holder.tv.setText(merchant.name);
        // 距离(center不为空时计算)
        if(!TextUtils.isEmpty(merchant.distance)
                && !"0".equals(merchant.distance)){
            float distance = Float.valueOf(merchant.distance)/1000;
            holder.tvDistance.setText(distance + "km");
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.tv)
        TextView tv;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.iv_right)
        ImageView ivRight;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
