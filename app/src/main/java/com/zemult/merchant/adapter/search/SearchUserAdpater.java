package com.zemult.merchant.adapter.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.util.SlashHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wikison on 2016/6/16.
 */
public class SearchUserAdpater extends BaseListAdapter<M_Userinfo> {

    private boolean isFromMerchant, isFromBlackList;
    private boolean isNoData;
    private int mHeight;

    public SearchUserAdpater(Context context, List<M_Userinfo> list) {
        super(context, list);
    }

    public void setFromMerchant() {
        isFromMerchant = true;
    }
    public void setFromBlackList() {
        isFromBlackList = true;
    }

    // 设置数据
    public void setData(List<M_Userinfo> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        isNoData = false;
        if (list.size() == 1 && list.get(0).isNoData()) {
            // 暂无数据布局
            isNoData = list.get(0).isNoData();
            mHeight = list.get(0).getHeight();
        }
        notifyDataSetChanged();
    }

    // 设置数据
    public void setData(List<M_Userinfo> listLatest, List<M_Userinfo> listAll) {
        if (listLatest.isEmpty()) {
            M_Userinfo userinfo = new M_Userinfo();
            userinfo.setUserId(-1);
            listLatest = new ArrayList<>();
            listLatest.add(userinfo);
        }
        listLatest.get(0).showLatest = true;
        if (!listAll.isEmpty()) {
            listAll.get(0).showAll = true;
            listAll.addAll(0, listLatest);
        }
        clearAll();
        addALL(listAll);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 暂无数据
        if (isNoData) {
            convertView = mInflater.inflate(R.layout.item_no_data_layout, null);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_no_data);
            rootView.setLayoutParams(params);
            return convertView;
        }
        // 正常数据
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_search_user, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (position == 0 && !isFromMerchant)
            holder.llTop.setVisibility(View.VISIBLE);
        else
            holder.llTop.setVisibility(View.GONE);

        M_Userinfo entity = getItem(position);
        // 设置数据
        initData(holder, entity);
        initListner(holder, position);
        return convertView;
    }

    private void initData(ViewHolder holder, M_Userinfo entity) {
        // 用户头像
        if (!TextUtils.isEmpty(entity.getUserHead()))
            mImageManager.loadCircleImage(entity.getUserHead(), holder.ivHead, "@80w_80h_1e");
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.getUserName()))
            holder.tvUserName.setText(entity.getUserName());
        // 完成任务的用户性别(0男,1女)
        switch (entity.getUserSex()) {
            case 0:
                holder.ivSex.setImageResource(R.mipmap.man_icon);
                break;
            case 1:
                holder.ivSex.setImageResource(R.mipmap.girl_icon);
                break;
        }
        holder.tvBuyNum.setText(entity.saleNum + "人找TA买单");

        if (isFromMerchant) {
            if (SlashHelper.userManager().getUserId() == entity.getUserId())
                holder.tvBuy.setVisibility(View.GONE);
            else
                holder.tvBuy.setVisibility(View.VISIBLE);

            holder.ivSex.setVisibility(View.GONE);
        } else {
            if (entity.saleUserNum > 0) {
                holder.tvTip.setVisibility(View.VISIBLE);
                holder.tvBuyNum.setVisibility(View.VISIBLE);
            } else {
                holder.tvTip.setVisibility(View.GONE);
                holder.tvBuyNum.setVisibility(View.GONE);
            }
        }
        if(isFromBlackList){
            holder.tvTip.setVisibility(View.GONE);
            holder.tvBuyNum.setVisibility(View.GONE);
            holder.ivSex.setVisibility(View.GONE);
        }

        if (entity.showLatest || entity.showAll) {
            holder.tvFenlei.setVisibility(View.VISIBLE);
            if(entity.showLatest)
                holder.tvFenlei.setText("最近联系");
            else
                holder.tvFenlei.setText("全部");
        }else
            holder.tvFenlei.setVisibility(View.GONE);


        if(entity.getUserId() == -1 ){
            holder.tvNoData.setVisibility(View.VISIBLE);
            holder.llUser.setVisibility(View.GONE);
        }
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListner(ViewHolder holder, final int position) {
        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserDetailClickListener != null)
                    onUserDetailClickListener.onUserDetailClick(position);
            }
        });
        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAllClickListener != null)
                    onAllClickListener.onAllClick(position);
            }
        });
        holder.tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBuyClickListener != null)
                    onBuyClickListener.onBuyClick(position);
            }
        });
    }

    /**
     * 用户详情点击接口
     */
    private OnUserDetailClickListener onUserDetailClickListener;

    public void setOnUserDetailClickListener(OnUserDetailClickListener onUserDetailClickListener) {
        this.onUserDetailClickListener = onUserDetailClickListener;
    }

    public interface OnUserDetailClickListener {
        void onUserDetailClick(int position);
    }

    /**
     * 买单点击接口
     */
    private OnBuyClickListener onBuyClickListener;

    public void setOnBuyClickListener(OnBuyClickListener onBuyClickListener) {
        this.onBuyClickListener = onBuyClickListener;
    }

    public interface OnBuyClickListener {
        void onBuyClick(int position);
    }

    private OnAllClickListener onAllClickListener;

    public void setOnAllClickListener(OnAllClickListener onAllClickListener) {
        this.onAllClickListener = onAllClickListener;
    }

    public interface OnAllClickListener {
        void onAllClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.ll_top)
        LinearLayout llTop;
        @Bind(R.id.tv_fenlei)
        TextView tvFenlei;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.iv_sex)
        ImageView ivSex;
        @Bind(R.id.tv_buy_num)
        TextView tvBuyNum;
        @Bind(R.id.tv_tip)
        TextView tvTip;
        @Bind(R.id.tv_buy)
        RoundTextView tvBuy;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
        @Bind(R.id.tv_no_data)
        TextView tvNoData;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
