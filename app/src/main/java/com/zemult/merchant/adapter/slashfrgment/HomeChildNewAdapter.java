package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * HomeChildNewAdapter
 *
 * @author djy
 * @time 2016/11/7 13:57
 */
public class HomeChildNewAdapter extends BaseListAdapter<M_Merchant> {

    private boolean isNoData, unshowTop;
    private int mHeight;

    public HomeChildNewAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
    }

    // 设置数据 任务
    public void setData(List<M_Merchant> list, boolean isLoadMore) {
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

    public void unshowTop() {
        unshowTop = true;
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
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_home_child_new, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (position == 0 && !unshowTop) {
            holder.llTop.setVisibility(View.VISIBLE);
            holder.viewTop.setVisibility(View.GONE);
        } else {
            holder.llTop.setVisibility(View.GONE);
            holder.viewTop.setVisibility(View.VISIBLE);
        }
        M_Merchant entity = getItem(position);
        initData(holder, entity);

        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, M_Merchant entity) {
        // 商家封面
        if (!TextUtils.isEmpty(entity.pic))
            mImageManager.loadUrlImage(entity.pic, holder.ivCover, "@450h");
        else
            holder.ivCover.setImageResource(R.color.bg_f0);
        // 商家名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
        // 人均消费
        holder.tvMoney.setText("人均￥" + Convert.getMoneyString(entity.perMoney));
        // 距中心点距离(米)
        if (!StringUtils.isEmpty(entity.distance)) {
            if (entity.distance.length() > 3) {
                double d = Double.valueOf(entity.distance);
                holder.tvDistance.setText(d / 1000 + "km");
            } else
                holder.tvDistance.setText(entity.distance + "m");
        }
        // 人数
        if (TextUtils.isEmpty(entity.saleUserHeads))
            holder.tvNum.setText("暂无服务管家");
        else
            holder.tvNum.setText(entity.saleuserNum + "人提供约服");

        // 是否有熟人-(关注的人)(0:否1:是)--游客默认为0
        if (entity.isFan == 1)
            holder.ivShuren.setVisibility(View.VISIBLE);
        else
            holder.ivShuren.setVisibility(View.GONE);

        // 营销经理们的头像
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerview.setLayoutManager(linearLayoutManager);
        //设置适配器
        HomePeopleAdapter adapter = new HomePeopleAdapter(mContext, entity.saleUserHeads);
        holder.recyclerview.setAdapter(adapter);
    }

    static class ViewHolder {
        @Bind(R.id.ll_top)
        LinearLayout llTop;
        @Bind(R.id.view_top)
        View viewTop;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.iv_qianyue)
        ImageView ivQianyue;
        @Bind(R.id.card_view)
        CardView cardView;
        @Bind(R.id.iv_shuren)
        ImageView ivShuren;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.recyclerview)
        RecyclerView recyclerview;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
