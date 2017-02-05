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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/12/28.
 */

public class TaMerchantChooseAdapter extends BaseListAdapter<M_Merchant> {
    Context mContext;

    private List<M_Merchant> merchantList = new ArrayList<M_Merchant>();
    private List<M_Merchant> merchantCanList = new ArrayList<M_Merchant>();
    private List<M_Merchant> merchantCannotList = new ArrayList<M_Merchant>();
    List<Map<String, Object>> groupIndex = new ArrayList<>();

    public TaMerchantChooseAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
        mContext = context;
    }

    // 设置数据 任务
    public void setData(List<M_Merchant> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }

        for (M_Merchant m : list) {
            if (m.reviewstatus == 2) {
                merchantCanList.add(m);
            } else {
                merchantCannotList.add(m);
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("number", merchantCanList.size());
        map.put("value", "已支持买单的商户");
        groupIndex.add(map);

        map = new HashMap<String, Object>();
        map.put("number", merchantCannotList.size());
        map.put("value", "暂不支持买单的商户");
        groupIndex.add(map);


        merchantList.addAll(merchantCanList);
        merchantList.addAll(merchantCannotList);

        addALL(merchantList);
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
        initData(holder, entity, position);
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
    private void initData(ViewHolder holder, M_Merchant m, int position) {
        Map<String, Object> map = groupIndex.get(0);
        Map<String, Object> map1 = groupIndex.get(1);

        holder.tvTitle.setText(m.name);
        if (m.reviewstatus == 2) {
            holder.rtvStatus.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.llHead.setVisibility(View.VISIBLE);
                holder.tvHeadTitle.setText((String) map.get("value"));
            } else {
                holder.llHead.setVisibility(View.GONE);
            }
        } else {
            holder.rtvStatus.setVisibility(View.GONE);
            if (position == 0 || position == (int) map.get("number")) {
                holder.llHead.setVisibility(View.VISIBLE);
                holder.tvHeadTitle.setText((String) map1.get("value"));
            } else {
                holder.llHead.setVisibility(View.GONE);
            }
        }


        holder.tvPerPrice.setText("人均 " + (int)m.perMoney);
        if (!StringUtils.isEmpty(m.distance)) {
            if (m.distance.length() > 3) {
                double d = Double.valueOf(m.distance);
                holder.tvDistance.setText(d / 1000 + "km");
            } else
                holder.tvDistance.setText(m.distance + "m");
        }
        holder.tvAddress.setText("地址: " + m.address);
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
        @Bind(R.id.tv_head_title)
        TextView tvHeadTitle;
        @Bind(R.id.ll_head)
        LinearLayout llHead;
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
