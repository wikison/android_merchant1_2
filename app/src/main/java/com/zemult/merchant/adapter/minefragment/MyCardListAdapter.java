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
 * Created by admin on 2016/7/25.
 */
public class MyCardListAdapter extends BaseListAdapter<M_Voucher> {
    int[] colorInt = new int[]{R.mipmap.quan_icon_one,R.mipmap.quan_icon_two,R.mipmap.quan_icon_three,R.mipmap.quan_icon_four};

    public MyCardListAdapter(Context context, List<M_Voucher> list) {
        super(context, list);
    }

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
            convertView = mInflater.inflate(R.layout.canuse_item, null, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        M_Voucher m_voucher = getData().get(position);

        mImageManager.loadCircleImage(m_voucher.head, holder.iv);//头像
        holder.moneyTv.setText("￥ " + m_voucher.money);//抵扣金额
        holder.nameTv.setText(""+m_voucher.name);//商家名称
        holder.tvConsume.setText("消费满"+m_voucher.minMoney+"使用");//最低消费额
        holder.tvTime.setText("有效期至"+m_voucher.endtime);//截止时间

       if(m_voucher.state==0){
      holder.statusIv.setVisibility(View.INVISIBLE);//无

           int colorIndex = position % colorInt.length;
           holder.backgroundIv.setBackgroundResource(colorInt[colorIndex]);
       }
        else if(m_voucher.state==1){
           //已使用
           holder.statusIv.setVisibility(View.VISIBLE);
           holder.backgroundIv.setBackgroundResource(R.mipmap.quan_icon_hui);
           holder.statusIv.setBackgroundResource(R.mipmap.yishiyong_btn);
       }
        else if(m_voucher.state==2){
        //已过期

           holder.statusIv.setVisibility(View.VISIBLE);
           holder.backgroundIv.setBackgroundResource(R.mipmap.quan_icon_hui);
           holder.statusIv.setBackgroundResource(R.mipmap.yiguoqi_btn);
       }
        else{
           holder.statusIv.setVisibility(View.INVISIBLE);//无
       }


        return convertView;
    }

    static class ViewHolder {
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
        @Bind(R.id.status_iv)
        ImageView statusIv;
        @Bind(R.id.card_view)
        CardView cardView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
