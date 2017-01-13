package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.view.FNRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/11/12.
 * 营销管理Adapter
 */

public class SaleMerchantAdapter extends BaseListAdapter<M_Merchant> {

    private List<M_Merchant> mDatas = new ArrayList<M_Merchant>();

    private void initListener(ViewHolder holder, M_Merchant merchant) {
        holder.tvName.setOnClickListener(new MyClickListener(merchant));
        holder.rlMyService.setOnClickListener(new MyClickListener(merchant));
        holder.fnMyService.setOnClickListener(new MyClickListener(merchant));
        holder.tvServiceRight.setOnClickListener(new MyClickListener(merchant));
        holder.tvQr.setOnClickListener(new MyClickListener(merchant));
        holder.tvShare.setOnClickListener(new MyClickListener(merchant));
        holder.tvDelete.setOnClickListener(new MyClickListener(merchant));
    }

    ItemMerchantClickListener itemMerchantClickListener;
    ItemServiceClickListener itemServiceClickListener;
    ItemQRClickListener itemQRClickListener;
    ItemShareClickListener itemShareClickListener;
    ItemDeleteClickListener itemDeleteClickListener;

    public SaleMerchantAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }


    public void setData(List<M_Merchant> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    public void removeOne(M_Merchant merchant) {
        mDatas.remove(merchant);
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_sale_merchant, null, false);
            holder = new ViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        mDatas = getData();
        final M_Merchant m = mDatas.get(position);

        holder.tvName.setText(m.name);
        holder.tvSaleUserMoney.setText(String.format("%s", Convert.getMoneyString(m.saleMoney)));
        holder.tvSaleNum.setText(String.format("共计%s笔交易", m.saleNum));
        if (m.reviewstatus != 2) {
            holder.tvQr.setVisibility(View.INVISIBLE);
            holder.tvShare.setVisibility(View.GONE);
            holder.vDivider.setVisibility(View.INVISIBLE);
            //holder.rlQrShare.setVisibility(View.INVISIBLE);
        } else {
            holder.tvQr.setVisibility(View.VISIBLE);
            holder.tvShare.setVisibility(View.VISIBLE);
            holder.vDivider.setVisibility(View.VISIBLE);
            //holder.rlQrShare.setVisibility(View.VISIBLE);
        }
        initTags(holder, m);
        initListener(holder, m);
        return convertView;
    }

    private void initTags(SaleMerchantAdapter.ViewHolder holder, M_Merchant m) {
        holder.fnMyService.setChildMargin(0, 0, 24, 0);
        holder.fnMyService.removeAllViews();
        if (!StringUtils.isBlank(m.tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(m.tags.split(",")));
            int iShowSize = tagList.size();
            iShowSize = iShowSize > 3 ? 3 : iShowSize;
            if (iShowSize > 0) {
                holder.fnMyService.setVisibility(View.VISIBLE);

                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(mContext);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTag.setPadding(24, 8, 24, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    holder.fnMyService.addView(rbTag);

                }
            } else {
                holder.fnMyService.setVisibility(View.GONE);
            }
        }


    }

    public interface ItemMerchantClickListener {
        void onItemClick(M_Merchant merchant);
    }

    public void setOnItemMerchantClickListener(ItemMerchantClickListener itemMerchantClickListener) {
        this.itemMerchantClickListener = itemMerchantClickListener;
    }

    public interface ItemServiceClickListener {
        void onItemClick(M_Merchant merchant);
    }

    public void setOnItemServiceClickListener(ItemServiceClickListener itemServiceClickListener) {
        this.itemServiceClickListener = itemServiceClickListener;
    }

    public interface ItemQRClickListener {
        void onItemClick(M_Merchant merchant);
    }

    public void setOnItemQRClickListener(ItemQRClickListener itemQRClickListener) {
        this.itemQRClickListener = itemQRClickListener;
    }

    public interface ItemShareClickListener {
        void onItemClick(M_Merchant merchant);
    }

    public void setOnItemShareClickListener(ItemShareClickListener itemShareClickListener) {
        this.itemShareClickListener = itemShareClickListener;
    }

    public interface ItemDeleteClickListener {
        void onItemClick(M_Merchant merchant);
    }

    public void setOnItemDeleteClickListener(ItemDeleteClickListener itemDeleteClickListener) {
        this.itemDeleteClickListener = itemDeleteClickListener;
    }

    class MyClickListener implements View.OnClickListener {
        M_Merchant merchant;

        public MyClickListener(M_Merchant merchant) {
            this.merchant = merchant;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_name:
                    if (itemMerchantClickListener != null)
                        itemMerchantClickListener.onItemClick(merchant);
                    break;
                case R.id.rl_my_service:
                case R.id.fn_my_service:
                case R.id.tv_service_right:
                    if (itemServiceClickListener != null)
                        itemServiceClickListener.onItemClick(merchant);
                    break;

                case R.id.tv_qr:
                    if (itemQRClickListener != null)
                        itemQRClickListener.onItemClick(merchant);
                    break;
                case R.id.tv_share:
                    if (itemShareClickListener != null)
                        itemShareClickListener.onItemClick(merchant);
                    break;
                case R.id.tv_delete:
                    if (itemDeleteClickListener != null)
                        itemDeleteClickListener.onItemClick(merchant);
                    break;

            }

        }
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_my_service)
        TextView tvMyService;
        @Bind(R.id.fn_my_service)
        FNRadioGroup fnMyService;
        @Bind(R.id.tv_service_right)
        TextView tvServiceRight;
        @Bind(R.id.rl_my_service)
        RelativeLayout rlMyService;
        @Bind(R.id.tv_sale_user_money)
        TextView tvSaleUserMoney;
        @Bind(R.id.tv_sale_num)
        TextView tvSaleNum;
        @Bind(R.id.tv_qr)
        TextView tvQr;
        @Bind(R.id.tv_share)
        TextView tvShare;
        @Bind(R.id.v_divider)
        View vDivider;
        @Bind(R.id.rl_qr_share)
        RelativeLayout rlQrShare;
        @Bind(R.id.tv_delete)
        TextView tvDelete;
        @Bind(R.id.ll_root_view)
        LinearLayout llRootView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

