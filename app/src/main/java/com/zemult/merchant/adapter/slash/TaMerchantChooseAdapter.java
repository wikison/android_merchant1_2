package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/12/28.
 */

public class TaMerchantChooseAdapter extends BaseListAdapter<M_Merchant> {
    Context mContext;

    public TaMerchantChooseAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
        mContext = context;
    }

    // 设置数据 任务
    public void setData(List<M_Merchant> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        M_Merchant entity = getItem(position);
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.layout_item_merchant, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }
        initData(holder, entity);
        initListener(holder, position);
        return convertView;
    }

    private void initListener(final ViewHolder holder, final int position) {
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAllClickListener != null)
                    onAllClickListener.onAllClick(position);
            }
        });
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param m
     */
    private void initData(ViewHolder holder, M_Merchant m) {
        holder.tvTitle.setText(m.name);
        if (m.reviewstatus == 2) {
            holder.rtvStatus.setVisibility(View.VISIBLE);
        } else {
            holder.rtvStatus.setVisibility(View.GONE);
        }
        holder.tvPerPrice.setText(Convert.getMoneyString(m.perMoney));
        holder.tvDistance.setText(m.distance);
        holder.tvAddress.setText(m.address);
    }

    /**
     * 点击Item
     */
    private OnAllClickListener onAllClickListener;

    public void setOnAllClickListener(OnAllClickListener onAllClickListener) {
        this.onAllClickListener = onAllClickListener;
    }

    public interface OnAllClickListener {
        void onAllClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.rtv_status)
        RoundTextView rtvStatus;
        @Bind(R.id.tv_per_price)
        TextView tvPerPrice;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.ll_root)
        RoundLinearLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
