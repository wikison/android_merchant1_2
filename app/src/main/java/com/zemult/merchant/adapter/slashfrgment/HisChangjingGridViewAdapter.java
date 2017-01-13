package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.text.TextUtils;
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

/**
 * 用户详情页-他的场景
 */
public class HisChangjingGridViewAdapter extends BaseListAdapter<M_Merchant> {

    public HisChangjingGridViewAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_his_jiangjing, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        M_Merchant merchant = getData().get(pos);
        // 场景头像 地址
        if(!TextUtils.isEmpty(merchant.head)){
            mImageManager.loadCircleHasBorderImage(merchant.head, holder.iv
                    ,mContext.getResources().getColor(R.color.divider_c1), 2);
        }
        // 场景名称
        if(!TextUtils.isEmpty(merchant.shortName))
            holder.tvPlace.setText(merchant.shortName);
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
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.tv_place)
        TextView tvPlace;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
