package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 我是商户适配器
 *
 * @author djy
 * @time 2016/12/29 9:17
 */
public class BusinessManAdapter extends BaseListAdapter<M_Merchant> {

    public BusinessManAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list) {
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_business_man, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Merchant entity = getItem(position);

        // 商家名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
        // 地址
        if (!TextUtils.isEmpty(entity.address))
            holder.tvAddress.setText(entity.address);
        if(entity.daiqiyue){
            holder.llRight.setVisibility(View.GONE);
            holder.tvDaiqianyue.setVisibility(View.VISIBLE);
            holder.llQianyue.setVisibility(View.GONE);
        }else {
            holder.llRight.setVisibility(View.VISIBLE);
            holder.tvDaiqianyue.setVisibility(View.GONE);
            holder.llQianyue.setVisibility(View.VISIBLE);
        }

        holder.llRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null)
                    itemClickListener.rightClick(position);
            }
        });
        holder.tvManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null)
                    itemClickListener.manageClick(position);
            }
        });
        holder.tvBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener!=null)
                    itemClickListener.billClick(position);
            }
        });
        return convertView;
    }

    public interface OnBusinessMenItemClickListener{
        void rightClick(int pos);
        void manageClick(int pos);
        void billClick(int pos);
    }
    private OnBusinessMenItemClickListener itemClickListener;

    public void setItemClickListener(OnBusinessMenItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_daiqianyue)
        TextView tvDaiqianyue;
        @Bind(R.id.ll_right)
        LinearLayout llRight;
        @Bind(R.id.tv_manage)
        TextView tvManage;
        @Bind(R.id.tv_bill)
        TextView tvBill;
        @Bind(R.id.ll_qianyue)
        LinearLayout llQianyue;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
