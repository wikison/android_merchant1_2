package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.util.Convert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/11/12.
 * 约客服务记录列表Adapter
 */

public class UserSalePayAdapter extends BaseListAdapter<M_Bill> {

    private List<M_Bill> mDatas = new ArrayList<M_Bill>();

    public UserSalePayAdapter(Context context, List<M_Bill> list) {
        super(context, list);
    }

    public void setData(List<M_Bill> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user_sale_pay, null, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mDatas = getData();
        final M_Bill m = mDatas.get(position);

        if (!TextUtils.isEmpty(m.userHead)) {
            //加载带外边框的
            mImageManager.loadCircleHasBorderImage(m.userHead, holder.ivUserHead, mContext.getResources().getColor(R.color.gainsboro), 1);
        }
        holder.tvUserName.setText(m.userName);
        holder.tvMerchantName.setText(m.merchantName);
        holder.tvCreateTime.setText(m.createtime.substring(0, 16));
        holder.tvSaleMoney.setText(Convert.getMoneyString(m.payMoney));

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_user_head)
        ImageView ivUserHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.iv_merchant_head)
        ImageView ivMerchantHead;
        @Bind(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @Bind(R.id.tv_sale_money)
        TextView tvSaleMoney;
        @Bind(R.id.tv_create_time)
        TextView tvCreateTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

