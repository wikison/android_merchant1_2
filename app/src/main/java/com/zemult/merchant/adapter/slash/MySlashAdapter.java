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

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.util.ImageManager;

import java.util.List;

/**
 * Created by wikison on 2016/6/17.
 */
public class MySlashAdapter extends
        RecyclerView.Adapter<MySlashAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<M_UserRole> mDatas;
    private ImageManager imageManager;
    private M_UserRole selectedEntity;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public MySlashAdapter(Context context, List<M_UserRole> datas) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = datas;
        imageManager = new ImageManager(context);
    }

    public void setData(List<M_UserRole> list) {
        mDatas.clear();
        mDatas.addAll(list);
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
        View view = mInflater.inflate(R.layout.rv_item_my_role,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mRoot = (LinearLayout) view.findViewById(R.id.ll_root);
        viewHolder.mImg = (ImageView) view.findViewById(R.id.iv_role);
        viewHolder.mImg_selected = (ImageView) view.findViewById(R.id.iv_role_selected);
        viewHolder.mName = (TextView) view.findViewById(R.id.tv_name);
        viewHolder.mLevel = (RoundTextView) view.findViewById(R.id.tv_level);

        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        M_UserRole entity = mDatas.get(position);
        imageManager.loadCircleHasBorderImage(entity.icon, holder.mImg, mContext.getResources().getColor(R.color.divider_dc), 1);

        if (!TextUtils.isEmpty(entity.name))
            holder.mName.setText(entity.name);
        if (!TextUtils.isEmpty(entity.level)) {
            holder.mLevel.setText(String.format("Lv.%s", entity.level));
        }
        holder.mName.setTextColor(mContext.getResources().getColor(R.color.orange));
//        if (selectedEntity != null
//                && entity.industryId == selectedEntity.industryId) {
//            holder.mImg_selected.setVisibility(View.VISIBLE);
//        } else {
//            holder.mImg_selected.setVisibility(View.GONE);
//            holder.mName.setTextColor(mContext.getResources().getColor(R.color.font_black_333));
//        }

        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedEntity = mDatas.get(position);
//                notifyDataSetChanged();

                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, selectedEntity);
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
        void onItemClick(int position, M_UserRole entity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        ImageView mImg_selected;
        RoundTextView mLevel;
        TextView mName;
        LinearLayout mRoot;

        public ViewHolder(View arg0) {
            super(arg0);
        }
    }

}
