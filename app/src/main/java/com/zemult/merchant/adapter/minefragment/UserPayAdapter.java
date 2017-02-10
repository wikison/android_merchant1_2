package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/11/12.
 * 订单列表Adapter
 */

public class UserPayAdapter extends BaseListAdapter<M_Bill> {

    int pagePosition;


    private List<M_Bill> mDatas = new ArrayList<M_Bill>();

    private void initListener(ViewHolder holder, M_Bill m_bill) {
        holder.ivSaleCover.setOnClickListener(new MyClickListener(m_bill));
        holder.tvSaleName.setOnClickListener(new MyClickListener(m_bill));
        holder.rtvToPay.setOnClickListener(new MyClickListener(m_bill));
    }

    ItemSaleUserClickListener itemSaleUserClickListener;
    ItemToPayClickListener itemToPayClickListener;
    ItemRootClickListener itemRootClickListener;

    public UserPayAdapter(Context context, List<M_Bill> list, int pagePosition) {
        super(context, list);
        this.pagePosition = pagePosition;

    }


    public void setData(List<M_Bill> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    //刷新单条记录
    public void refreshOneRecord(M_Bill m_bill, int pos) {
        getData().set(pos, m_bill);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user_pay, null, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mDatas = getData();
        final M_Bill m = mDatas.get(position);


        if (m.type == 0) {
            holder.tvSaleName.setText("服务管家: " + m.saleUserName);
            if (!TextUtils.isEmpty(m.saleUserHead)) {
                //加载带外边框的
                mImageManager.loadCircleHasBorderImage(m.saleUserHead, holder.ivSaleCover, mContext.getResources().getColor(R.color.gainsboro), 1);
            }
            switch (m.state) {
                case 0:
                    holder.tvState.setText("待付款");
                    holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
                    holder.rtvToPay.setText(String.format("去支付 (还剩%s)", DateTimeUtil.strLeftTime(m.createtime, 30)));
                    holder.llToPay.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    if (pagePosition == 0) {
                        if (m.payMoney >= 100 && m.isComment == 0) {
                            holder.tvState.setText("待评价");
                            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
                        } else {
                            holder.tvState.setText("已完成");
                            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
                        }
                    } else {
                        holder.tvState.setText("待评价");
                        holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
                    }

                    holder.llToPay.setVisibility(View.GONE);
                    break;
                case 2:
                    holder.tvState.setText("已失效");
                    holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_black_999));
                    holder.llToPay.setVisibility(View.GONE);
                    break;
                case 3:
                    holder.tvState.setText("已取消");
                    holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
                    holder.llToPay.setVisibility(View.GONE);
                    break;
            }

        } else if (m.type == 3) {
            holder.tvSaleName.setText("赠送对象: " + m.toUserName);
            holder.tvState.setText("送礼物");
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
            if (!TextUtils.isEmpty(m.toUserHead)) {
                //加载带外边框的
                mImageManager.loadCircleHasBorderImage(m.toUserHead, holder.ivSaleCover, mContext.getResources().getColor(R.color.gainsboro), 1);
            }
        }else if (m.type == 4) {
            holder.tvSaleName.setText("赠送对象: " + m.toUserName);
            holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_main));
            holder.tvState.setText("送红包");
            if (!TextUtils.isEmpty(m.toUserHead)) {
                //加载带外边框的
                mImageManager.loadCircleHasBorderImage(m.toUserHead, holder.ivSaleCover, mContext.getResources().getColor(R.color.gainsboro), 1);
            }
        }


        holder.tvMoney.setText("-" + Convert.getMoneyString(m.payMoney));
        holder.tvDate.setText(m.createtime.substring(5, 10));
        holder.tvTime.setText(m.createtime.substring(11, 16));

        initListener(holder, m);
        return convertView;
    }

    public interface ItemRootClickListener {
        void onItemClick(M_Bill m_bill);
    }

    public void setOnItemRootClickListener(ItemRootClickListener itemRootClickListener) {
        this.itemRootClickListener = itemRootClickListener;
    }

    public interface ItemSaleUserClickListener {
        void onItemClick(M_Bill m_bill);
    }

    public void setOnItemSaleUserClickListener(ItemSaleUserClickListener itemSaleUserClickListener) {
        this.itemSaleUserClickListener = itemSaleUserClickListener;
    }

    public interface ItemToPayClickListener {
        void onItemClick(M_Bill m_bill);
    }

    public void setOnItemToPayClickListener(ItemToPayClickListener itemToPayClickListener) {
        this.itemToPayClickListener = itemToPayClickListener;
    }

    class MyClickListener implements View.OnClickListener {
        M_Bill m_bill;

        public MyClickListener(M_Bill m_bill) {
            this.m_bill = m_bill;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_root_view:
                    if (itemRootClickListener != null)
                        itemRootClickListener.onItemClick(m_bill);
                    break;
                case R.id.iv_sale_cover:
                case R.id.tv_sale_name:
                    if (itemSaleUserClickListener != null)
                        itemSaleUserClickListener.onItemClick(m_bill);
                    break;
                case R.id.rtv_to_pay:
                    if (itemToPayClickListener != null)
                        itemToPayClickListener.onItemClick(m_bill);
                    break;

            }

        }
    }


    static class ViewHolder {
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.iv_sale_cover)
        ImageView ivSaleCover;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_sale_name)
        TextView tvSaleName;
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.rtv_to_pay)
        RoundTextView rtvToPay;
        @Bind(R.id.ll_to_pay)
        LinearLayout llToPay;
        @Bind(R.id.rtv_root)
        RoundLinearLayout rtvRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

