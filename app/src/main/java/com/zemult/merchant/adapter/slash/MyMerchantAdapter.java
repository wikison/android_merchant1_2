package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.ImageManager;

import java.util.List;

/**
 * Created by wikison on 2016/6/17.
 */
public class MyMerchantAdapter extends
        RecyclerView.Adapter<MyMerchantAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<M_Merchant> mDatas;
    private ImageManager imageManager;
    private M_Merchant selectedEntity;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MyMerchantAdapter(Context context, List<M_Merchant> datas) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = datas;
        imageManager = new ImageManager(context);
    }

    public void setData(List<M_Merchant> list) {
        mDatas.clear();
        mDatas.addAll(list);
        if (list != null && list.size() > 0) {
            selectedEntity = list.get(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_business_changjing,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mRoot = (LinearLayout) view.findViewById(R.id.ll_root);
        viewHolder.mImg = (ImageView) view.findViewById(R.id.iv_changjing);
        viewHolder.mImg_selected = (ImageView) view.findViewById(R.id.iv_changjing_selected);
        viewHolder.mName = (TextView) view.findViewById(R.id.tv_changjing_name);
        viewHolder.mDistance = (TextView) view.findViewById(R.id.tv_changjing_distance);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        M_Merchant entity = mDatas.get(position);
        imageManager.loadCircleImage(entity.head, holder.mImg);
        if (!TextUtils.isEmpty(entity.distance)
                && !"0".equals(entity.distance)) {
            float distance = Float.valueOf(entity.distance) / 1000;
            holder.mDistance.setText(distance + "km");
        }

        if (!TextUtils.isEmpty(entity.shortName))
            holder.mName.setText(entity.shortName);

        if (selectedEntity != null
                && entity.merchantId == selectedEntity.merchantId) {
            holder.mDistance.setTextColor(mContext.getResources().getColor(R.color.orange));
            holder.mName.setTextColor(mContext.getResources().getColor(R.color.orange));
            holder.mImg_selected.setVisibility(View.VISIBLE);
        } else {
            holder.mImg_selected.setVisibility(View.GONE);
            holder.mDistance.setTextColor(mContext.getResources().getColor(R.color.font_black_888));
            holder.mName.setTextColor(mContext.getResources().getColor(R.color.font_black_333));
        }

        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEntity != mDatas.get(position)) {
                    selectedEntity = mDatas.get(position);
                    notifyDataSetChanged();

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position, selectedEntity);
                    }
                }
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, M_Merchant entity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        ImageView mImg_selected;
        TextView mName;
        TextView mDistance;
        LinearLayout mRoot;

        public ViewHolder(View arg0) {
            super(arg0);
        }
    }

}
