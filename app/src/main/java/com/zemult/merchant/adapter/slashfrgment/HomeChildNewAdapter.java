package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;

import java.util.ArrayList;
import java.util.Arrays;
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

    private boolean isNoData;
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


    // 删除单条记录
    public void delOneRecord(int pos) {
        getData().remove(pos);
        notifyDataSetChanged();
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

        M_Merchant entity = getItem(position);
        initData(holder, entity, position);

        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, final M_Merchant entity, final int position) {
        // 设置广告数据
        List<String> adList = new ArrayList<>();
        if (StringUtils.isBlank(entity.pics)) {
            adList.add(entity.pic);
        } else {
            adList = Arrays.asList(entity.pics.split(","));
        }

        holder.banner.setImages(adList);
        holder.banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                mImageManager.loadRoundImage((String) path, imageView, 24, "@450h");
            }
        });
        holder.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (itemClickListener != null)
                    itemClickListener.onBannerClick(entity);
            }
        });
        holder.banner.setIndicatorGravity(BannerConfig.CENTER).start();

        // 商家名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
        // 商家地址
        if (!TextUtils.isEmpty(entity.address))
            holder.tvAddress.setText(entity.address);
        // 人均消费
        holder.tvMoney.setText("人均￥" + (int) (entity.perMoney));
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
            holder.tvNum.setText("服务管家加盟中...");
        else
            holder.tvNum.setText("服务管家" + entity.saleuserNum + "人");

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
        holder.recyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (itemClickListener != null)
//                    itemClickListener.onRvHeadClick(position);
                if (itemClickListener != null)
                    itemClickListener.onBannerClick(entity);
                return true;
            }
        });

        if (entity.reviewstatus == 2)
            holder.ivQianyue.setVisibility(View.VISIBLE);
        else
            holder.ivQianyue.setVisibility(View.GONE);
    }

    public interface ItemClickListener {
        void onBannerClick(M_Merchant m_merchant);
        void onRvHeadClick(int pos);
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_address)
        TextView tvAddress;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.banner)
        Banner banner;
        @Bind(R.id.iv_qianyue)
        ImageView ivQianyue;
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
