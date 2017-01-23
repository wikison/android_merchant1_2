package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 消费详情--type8--礼物兑换
 *
 * @author djy
 * @time 2017/1/22 9:38
 */
public class PresentExchangeAdapter extends BaseListAdapter<M_Present> {

    public PresentExchangeAdapter(Context context, List<M_Present> list) {
        super(context, list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_present_exchange, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Present entity = getItem(position);
        if(position == getCount() -1)
            holder.divider.setVisibility(View.GONE);
        else
            holder.divider.setVisibility(View.VISIBLE);

        // 名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name + "x" + entity.num);
        // 兑换价格
        holder.tvPrice.setText(Convert.getMoneyString(entity.price));

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_price)
        TextView tvPrice;
        @Bind(R.id.divider)
        View divider;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
