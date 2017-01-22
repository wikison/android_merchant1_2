package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Reservation;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/12/28.
 */

public class ChooseReservationAdapter extends BaseListAdapter<M_Reservation> {
    Context mContext;

    public String getSelectId() {
        return selectId;
    }

    public void setSelectId(String selectId) {
        this.selectId = selectId;
    }

    String selectId;

    public ChooseReservationAdapter(Context context, List<M_Reservation> list) {
        super(context, list);
        mContext = context;
    }

    // 设置数据 任务
    public void setData(List<M_Reservation> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }

        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        M_Reservation entity = getItem(position);
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_choose_reservation, null);
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
    private void initData(ViewHolder holder, M_Reservation m) {
        holder.tvNo.setText("预约单号: "+m.number);
        holder.tvTime.setText("订单时间: "+m.reservationTime.substring(0, 16));
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
        @Bind(R.id.ll_root)
        LinearLayout llRoot;
        @Bind(R.id.cb)
        CheckBox cb;
        @Bind(R.id.tv_no)
        TextView tvNo;
        @Bind(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
