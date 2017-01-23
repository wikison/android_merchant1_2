package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import java.util.List;

/**
 * Created by admin on 2016/6/13.
 */
public class PresentAdapter extends
        RecyclerView.Adapter<PresentAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<M_Present> mDatas;
    private ImageManager imageManager;
    private Context mContext;
    private Activity mActivity;
    private boolean fromSend;
    private int selectedId;

    public void fromSend() {
        fromSend = true;
    }

    public PresentAdapter(Context context, List<M_Present> datats) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mActivity = (Activity) context;
        mDatas = datats;
        imageManager = new ImageManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        ImageView iv, ivSelect;
        TextView tvNum, tvName, tvExchangePrice;
        RelativeLayout ll;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_present,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.iv = (ImageView) view.findViewById(R.id.iv);
        viewHolder.ivSelect = (ImageView) view.findViewById(R.id.iv_select);
        viewHolder.tvNum = (TextView) view.findViewById(R.id.tv_num);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.tvExchangePrice = (TextView) view.findViewById(R.id.tv_exchanege_price);
        viewHolder.ll = (RelativeLayout) view.findViewById(R.id.ll);

        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final M_Present present = mDatas.get(position);
        // 礼物名称
        if (!TextUtils.isEmpty(present.name))
            holder.tvName.setText(fromSend ? present.name + " ￥" + Convert.getMoneyString(present.price) : present.name);
        // 礼物图片
        if (present.name.contains("兰博基尼")) {
            holder.iv.setImageResource(R.mipmap.che_icon);

        } else if (present.name.contains("钻戒")) {
            holder.iv.setImageResource(R.mipmap.zuanjie_icon);

        } else if (present.name.contains("钱包")) {
            holder.iv.setImageResource(R.mipmap.qianbao_icon);

        } else if (present.name.contains("花")) {
            holder.iv.setImageResource(R.mipmap.hua_icon);

        } else {
            holder.iv.setImageResource(R.mipmap.liu_cion);
        }
        // 礼物的数量
        holder.tvNum.setText("x" + present.num);
        // 礼物的兑换价格
        holder.tvExchangePrice.setText("(可兑换￥" + Convert.getMoneyString(present.exchangePrice) + ")");

        if (fromSend == true) {
            holder.tvNum.setVisibility(View.GONE);
            holder.tvExchangePrice.setVisibility(View.GONE);

            if (selectedId == present.presentId)
                holder.ivSelect.setVisibility(View.VISIBLE);
            else
                holder.ivSelect.setVisibility(View.INVISIBLE);

            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedId == present.presentId) {
                        selectedId = 0;
                        if (mOnItemClickLitener != null) {
                            mOnItemClickLitener.onItemClick(present);
                        }
                    } else {
                        selectedId = present.presentId;
                        if (mOnItemClickLitener != null) {
                            mOnItemClickLitener.onItemClick(present);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener {
        void onItemClick(M_Present m_present);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
