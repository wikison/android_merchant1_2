package com.zemult.merchant.adapter.minefragment;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DensityUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wikison on 2016/6/16.
 */
public class MerchantManagerAdpater extends BaseListAdapter<M_Userinfo> {
    private boolean showDel;
    private View view;

    public MerchantManagerAdpater(Context context, List<M_Userinfo> list) {
        super(context, list, (Activity) context);
    }

    public void refresh(boolean showDel) {
        this.showDel = showDel;
        notifyDataSetChanged();
    }

    public void delone(int pos) {
        getData().remove(pos);
        notifyDataSetChanged();
    }

    // 设置数据
    public void setData(List<M_Userinfo> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 正常数据
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_merchant_manager, null);
            holder = new ViewHolder(convertView);

            ViewGroup.LayoutParams lp = holder.llContent.getLayoutParams();
            lp.width = DensityUtil.getWindowWidth(mActivity);

            holder.hsv.setTag(holder);
            holder.ivDel.setTag(holder);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        M_Userinfo entity = getItem(position);
        // 设置数据
        initData(holder, entity);

        if (showDel) {
            holder.ivDel.setVisibility(View.VISIBLE);
        } else {
            holder.ivDel.setVisibility(View.GONE);
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
                if (onDelClickListener != null)
                    onDelClickListener.onDelClick(pos);
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

    private void initData(ViewHolder holder, M_Userinfo entity) {
        // 用户头像
        if (!TextUtils.isEmpty(entity.getUserHead()))
            mImageManager.loadCircleImage(entity.getUserHead(), holder.ivHead);
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.getUserName()))
            holder.tvName.setText(entity.getUserName());
        holder.tvMoney.setText("交易额 " + Convert.getMoneyString(entity.saleMoney) + "   共" + entity.saleNum + "笔交易");
    }


    /**
     * 买单点击接口
     */
    private OnDelClickListener onDelClickListener;

    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        this.onDelClickListener = onDelClickListener;
    }

    public interface OnDelClickListener {
        void onDelClick(int position);
    }


    static class ViewHolder {
        @Bind(R.id.iv_del)
        ImageView ivDel;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.del)
        Button del;
        @Bind(R.id.ll_action)
        LinearLayout llAction;
        @Bind(R.id.hsv)
        HorizontalScrollView hsv;
        @Bind(R.id.ll_content)
        LinearLayout llContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
