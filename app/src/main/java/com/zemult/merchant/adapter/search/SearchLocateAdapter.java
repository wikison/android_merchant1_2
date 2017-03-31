package com.zemult.merchant.adapter.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2017/3/30.
 */
public class SearchLocateAdapter extends BaseListAdapter<PoiItem> {

    private List<PoiItem> mDatas = new ArrayList<PoiItem>();
    boolean showFirst = false;

    public SearchLocateAdapter(Context context, List<PoiItem> list, boolean showFirst) {
        super(context, list);
        this.mDatas = list;
        this.showFirst = showFirst;
    }


    public void setData(List<PoiItem> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View container, ViewGroup parent) {
        ViewHolder holder = null;
        if (container == null) {
            container = mInflater.inflate(R.layout.item_location_info, null, false);
            holder = new ViewHolder(container);

            container.setTag(holder);
        } else {
            holder = (ViewHolder) container.getTag();
        }

        PoiItem poiItem = mDatas.get(position);
        holder.tvAddress.setText(poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet());

        if (showFirst) {
            if (position == 0) {
                holder.tvNow.setVisibility(View.VISIBLE);
                holder.tvAddress.setTextColor(mContext.getResources().getColor(R.color.font_black_28));
            } else {
                holder.tvNow.setVisibility(View.GONE);
                holder.tvAddress.setTextColor(mContext.getResources().getColor(R.color.font_black_999));

            }
        }else{
            holder.tvAddress.setTextColor(mContext.getResources().getColor(R.color.font_black_999));
        }

        holder.tvPoi.setText(poiItem.getTitle());

        return container;
    }


    static class ViewHolder {
        @Bind(R.id.tv_now)
        TextView tvNow;
        @Bind(R.id.tv_poi)
        TextView tvPoi;
        @Bind(R.id.tv_address)
        TextView tvAddress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

