package com.zemult.merchant.adapter.minefragment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Voucher;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/8/2.
 */
public class MerchantCardsAdapter extends BaseListAdapter<M_Voucher> {
    public MerchantCardsAdapter(Context context, List<M_Voucher> list) {
        super(context, list);
    }
    int[] colorInt = new int[]{R.mipmap.quan_icon_one,R.mipmap.quan_icon_two,R.mipmap.quan_icon_three,R.mipmap.quan_icon_four};
    public void setData(List<M_Voucher> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_shangjiacards, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int colorIndex = position % colorInt.length;
        holder.backgroundIv.setBackgroundResource(colorInt[colorIndex]);
        M_Voucher m_voucher = getData().get(position);
       holder.createtimeTv.setText("于"+m_voucher.createtime+"发布");
        mImageManager.loadCircleImage(m_voucher.head, holder.iv);//头像
        holder.moneyTv.setText("￥ " + m_voucher.money);//抵扣金额
        holder.nameTv.setText(""+m_voucher.name);//商家名称
        holder.tvConsume.setText("消费满"+m_voucher.minMoney+"使用");//最低消费额
        holder.tvTime.setText("有效期至"+ m_voucher.endtime);//截止时间
        if(m_voucher.num==0){
            holder.useNumTv.setText("");
        }
       else{
            holder.useNumTv.setText("共"+m_voucher.num+"张     已使用"+m_voucher.useNum+"张");
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.createtime_tv)
        TextView createtimeTv;
        @Bind(R.id.background_iv)
        ImageView backgroundIv;
        @Bind(R.id.iv)
        ImageView iv;
        @Bind(R.id.money_tv)
        TextView moneyTv;
        @Bind(R.id.name_tv)
        TextView nameTv;
        @Bind(R.id.tv_consume)
        TextView tvConsume;
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.useNum_tv)
        TextView useNumTv;
        @Bind(R.id.card_view)
        CardView cardView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
