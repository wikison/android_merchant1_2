package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.EditFilter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Wikison on 2016/12/28.
 */

public class PositionAdapter extends BaseListAdapter<M_Reservation> {
    Context mContext;
    M_Reservation selectPosition;

    public PositionAdapter(Context context, List<M_Reservation> list) {
        super(context, list);
        mContext = context;
    }

    public void setSelectedPosition(M_Reservation selectedPosition) {
        this.selectPosition = selectedPosition;

        for (M_Reservation entity : getData()) {
            if (entity.name.equals(selectedPosition.name)) {
                entity.setChecked(true);
            }
        }
        notifyDataSetChanged();
    }

    public M_Reservation getSelectedPosition() {
        return selectPosition;
    }

    //是否有选中的
    private boolean noneSelect(List<M_Reservation> list) {
        boolean result = false;
        for (M_Reservation entity : list) {
            if (entity.isChecked()) {
                result = true;
                break;
            }
        }

        return result;
    }

    // 设置数据
    public void setData(List<M_Reservation> list) {
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        M_Reservation entity = getItem(position);
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_position, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);

        }
        EditFilter.WordFilter(holder.etName, 10);
        initData(holder, entity, position);
        return convertView;
    }


    /**
     * 设置数据
     *
     * @param holder
     * @param m
     */
    private void initData(ViewHolder holder, M_Reservation m, int position) {
        if (position != getCount() - 1) {
            holder.tvName.setText(m.name);
            holder.rllName.setVisibility(View.GONE);
        } else {
            holder.tvName.setText("其他");
            holder.rllName.setVisibility(View.VISIBLE);
            holder.tvName.setText(m.name);
        }
    }

    static class ViewHolder {
        @Bind(R.id.cb)
        CheckBox cb;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.et_name)
        EditText etName;
        @Bind(R.id.rll_name)
        RoundLinearLayout rllName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
