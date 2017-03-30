package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.view.FixedListView;
import com.zemult.merchant.view.FlowLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/2/8.
 */
public class MySaleMerchantAdapter extends BaseListAdapter<M_Merchant> {
    ItemMerchantClickListener itemMerchantClickListener;
    ItemServiceClickListener itemServiceClickListener;
    ItemQRClickListener itemQRClickListener;
    ItemShareClickListener itemShareClickListener;
    ItemDeleteClickListener itemDeleteClickListener;

    public MySaleMerchantAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    public void setData(List<M_Merchant> list) {
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_my_sale_merchant, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Merchant merchant = getData().get(position);
        holder.tvCategory.setText(merchant.industryName);
        if (merchant.merchantList != null
                && !merchant.merchantList.isEmpty()) {
            holder.flvMerchant.setVisibility(View.VISIBLE);
            holder.flvMerchant.setAdapter(new SaleMerchantAdapter(merchant));
        } else
            holder.flvMerchant.setVisibility(View.GONE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_category)
        TextView tvCategory;
        @Bind(R.id.flv_merchant)
        FixedListView flvMerchant;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class SaleMerchantAdapter extends BaseAdapter {
        private M_Merchant merchant;
        private List<M_Merchant> merchantList = new ArrayList<>();

        private SaleMerchantAdapter(M_Merchant merchant) {
            this.merchant = merchant;
            merchantList = merchant.merchantList;

        }

        @Override
        public int getCount() {
            return merchantList.size();
        }

        @Override
        public Object getItem(int position) {
            return merchantList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sale_merchant, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(R.string.app_name, holder);
            } else {
                holder = (ViewHolder) convertView.getTag(R.string.app_name);
            }

            M_Merchant m = merchantList.get(position);

            // 商家封面
            if (!TextUtils.isEmpty(m.pic))
                mImageManager.loadUrlImage(m.pic, holder.ivCover, "@320h");
            else
                holder.ivCover.setImageResource(R.mipmap.user_icon);
            // 商家名称
            if (!TextUtils.isEmpty(m.name))
                holder.tvName.setText(m.name);
            // 人均消费
            holder.tvMoney.setText("人均￥" + (int) (m.perMoney));
            // 距中心点距离(米)
            if (!StringUtils.isEmpty(m.distance)) {
                if (m.distance.length() > 3) {
                    double d = Double.valueOf(m.distance);
                    holder.tvDistance.setText(d / 1000 + "km");
                } else
                    holder.tvDistance.setText(m.distance + "m");
            }

            holder.tvMySeven.setText("7天服务指数" + m.saleUserSumScore);

            if (m.commentNumber != 0) {
                holder.rb5.setStar(m.comment / m.commentNumber);
            } else {
                holder.rb5.setStar(0);
            }
            holder.tvComment.setText(m.commentNumber + "人评价");
            holder.tvService.setText("服务" + m.saleNum + "人次");
            holder.tvSaleUserMoney.setText(String.format("%s", Convert.getMoneyString(m.saleMoney)));
            holder.tvSaleNum.setText(String.format("共计%s笔交易", m.saleNum));
            if (m.reviewstatus != 2) {
                holder.llRate.setVisibility(View.GONE);
                holder.llTrade.setVisibility(View.GONE);
                holder.rlQrShare.setVisibility(View.GONE);
                holder.llNoAdd.setVisibility(View.VISIBLE);
            } else {
                holder.llRate.setVisibility(View.VISIBLE);
                holder.llTrade.setVisibility(View.VISIBLE);
                holder.rlQrShare.setVisibility(View.VISIBLE);
                holder.llNoAdd.setVisibility(View.GONE);
            }
            initTags(holder, m);

            holder.tvName.setOnClickListener(new MyClickListener(m));
            holder.rlMyService.setOnClickListener(new MyClickListener(m));
            holder.fnMyService.setOnClickListener(new MyClickListener(m));
            holder.tvServiceRight.setOnClickListener(new MyClickListener(m));
            holder.tvQr.setOnClickListener(new MyClickListener(m));
            holder.tvShare.setOnClickListener(new MyClickListener(m));
            holder.tvDelete.setOnClickListener(new MyClickListener(m));

            return convertView;
        }

        private void initTags(ViewHolder holder, M_Merchant m) {
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

         class ViewHolder {
            @Bind(R.id.iv_cover)
            ImageView ivCover;
            @Bind(R.id.tv_name)
            TextView tvName;
            @Bind(R.id.tv_money)
            TextView tvMoney;
            @Bind(R.id.tv_distance)
            TextView tvDistance;
            @Bind(R.id.rb_5)
            RatingBar rb5;
            @Bind(R.id.tv_comment)
            TextView tvComment;
            @Bind(R.id.tv_service)
            TextView tvService;
            @Bind(R.id.tv_my_seven)
            TextView tvMySeven;
            @Bind(R.id.tv_seven_right)
            TextView tvSevenRight;
            @Bind(R.id.rl_my_seven)
            RelativeLayout rlMySeven;
            @Bind(R.id.ll_rate)
            LinearLayout llRate;
            @Bind(R.id.tv_my_service)
            TextView tvMyService;
            @Bind(R.id.fn_my_service)
            FlowLayout fnMyService;
            @Bind(R.id.tv_service_right)
            TextView tvServiceRight;
            @Bind(R.id.rl_my_service)
            RelativeLayout rlMyService;
            @Bind(R.id.tv_sale_user_money)
            TextView tvSaleUserMoney;
            @Bind(R.id.tv_sale_num)
            TextView tvSaleNum;
            @Bind(R.id.ll_trade)
            LinearLayout llTrade;
            @Bind(R.id.tv_qr)
            TextView tvQr;
            @Bind(R.id.tv_share)
            TextView tvShare;
            @Bind(R.id.v_divider)
            View vDivider;
            @Bind(R.id.rl_qr_share)
            RelativeLayout rlQrShare;
            @Bind(R.id.ll_no_add)
            LinearLayout llNoAdd;
            @Bind(R.id.tv_delete)
            TextView tvDelete;
            @Bind(R.id.card_view)
            CardView cardView;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
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
}
