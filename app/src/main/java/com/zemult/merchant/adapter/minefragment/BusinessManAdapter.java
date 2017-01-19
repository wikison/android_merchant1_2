package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

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
        // 商家封面
        if (!TextUtils.isEmpty(entity.pic))
            mImageManager.loadUrlImage(entity.pic, holder.ivCover, "@450h");
        else
            holder.ivCover.setImageResource(R.mipmap.merchant_default_cover);

        if (entity.daiqiyue) {
            holder.rlDaiqianyue.setVisibility(View.VISIBLE);
            holder.llQianyue.setVisibility(View.GONE);
            holder.llXiaofei.setVisibility(View.GONE);
        } else {
            holder.rlDaiqianyue.setVisibility(View.GONE);
            holder.llQianyue.setVisibility(View.VISIBLE);
            holder.llXiaofei.setVisibility(View.VISIBLE);
            // 人均消费
            holder.tvMoney.setText("人均￥" + Convert.getMoneyString(entity.perMoney));
            // 距中心点距离(米)
            if (!StringUtils.isEmpty(entity.distance)) {
                if (entity.distance.length() > 3) {
                    double d = Double.valueOf(entity.distance);
                    holder.tvDistance.setText(d / 1000 + "km");
                } else
                    holder.tvDistance.setText(entity.distance + "m");
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.rightClick(position);
            }
        });
        holder.rlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.manageClick(position);
            }
        });
        holder.rlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.billClick(position);
            }
        });
        return convertView;
    }

    public interface OnBusinessMenItemClickListener {
        void rightClick(int pos);

        void manageClick(int pos);

        void billClick(int pos);
    }

    private OnBusinessMenItemClickListener itemClickListener;

    public void setItemClickListener(OnBusinessMenItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    static class ViewHolder {
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.rl_left)
        RelativeLayout rlLeft;
        @Bind(R.id.rl_right)
        RelativeLayout rlRight;
        @Bind(R.id.ll_qianyue)
        LinearLayout llQianyue;
        @Bind(R.id.rl_daiqianyue)
        RelativeLayout rlDaiqianyue;
        @Bind(R.id.card_view)
        CardView cardView;
        @Bind(R.id.ll_xiaofei)
        LinearLayout llXiaofei;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
