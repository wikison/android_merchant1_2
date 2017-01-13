package com.zemult.merchant.adapter.minefragment;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.util.DensityUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商家管理--电话
 * @author djy
 * @time 2016/8/9 9:11
 */
public class MerchantPhoneAdapter extends BaseListAdapter<String>{
    private boolean showDel;
    private View view;

    public MerchantPhoneAdapter(Context context, List<String> list, Activity activity) {
        super(context, list, activity);
    }

    public void refresh(boolean showDel) {
        this.showDel = showDel;
        notifyDataSetChanged();
    }

    public void addOne() {
        add("");
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_merchant_phone, null);
            holder = new ViewHolder(convertView);

            ViewGroup.LayoutParams lp = holder.llContent.getLayoutParams();
            lp.width = DensityUtil.getWindowWidth(mActivity);

            holder.hsv.setTag(holder);
            holder.ivDel.setTag(holder);

            // 让ViewHolder持有一个TextWathcer，动态更新position来防治数据错乱；不能将position定义成final直接使用，必须动态更新
            holder.textWatcher = new MyTextWatcher();
            holder.etPhone.addTextChangedListener(holder.textWatcher);
            holder.updatePosition(position);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            //动态更新TextWathcer的position
            holder.updatePosition(position);
        }

        String entity = getItem(position);
        holder.etPhone.setText(entity);

        if (showDel) {
            holder.ivDel.setVisibility(View.VISIBLE);
            holder.etPhone.setEnabled(true);
        } else {
            holder.ivDel.setVisibility(View.GONE);
            holder.etPhone.setEnabled(false);
            holder.hsv.smoothScrollTo(0, 0);
        }

        // 设置监听事件
        holder.ivDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view != null) {
                    ViewHolder oldViewHolder = (ViewHolder) view.getTag();
                    if (oldViewHolder.llAction.getVisibility() == View.VISIBLE) {
                        oldViewHolder.hsv.smoothScrollTo(0, 0);
                    }
                }
                holder.hsv.smoothScrollTo(holder.llAction.getWidth(), 0);
                view = v;
            }
        });

        final int pos = position;
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.hsv.smoothScrollTo(0, 0);
                getData().remove(pos);
                notifyDataSetChanged();
            }
        });

        holder.hsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if (view != null) {
                            ViewHolder oldViewHolder = (ViewHolder) view.getTag();
                            if (oldViewHolder.llAction.getVisibility() == View.VISIBLE) {
                                oldViewHolder.hsv.smoothScrollTo(0, 0);
                            }
                        }
                        view = v;
                }
                return true;
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_del)
        ImageView ivDel;
        @Bind(R.id.et_phone)
        EditText etPhone;
        @Bind(R.id.ll_content)
        LinearLayout llContent;
        @Bind(R.id.del)
        Button del;
        @Bind(R.id.ll_action)
        LinearLayout llAction;
        @Bind(R.id.hsv)
        HorizontalScrollView hsv;

        MyTextWatcher textWatcher;
        //动态更新TextWathcer的position
        public void updatePosition(int position) {
            textWatcher.updatePosition(position);
        }

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class MyTextWatcher implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        public MyTextWatcher() {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            getData().set(position, s.toString());
        }
    }

}
