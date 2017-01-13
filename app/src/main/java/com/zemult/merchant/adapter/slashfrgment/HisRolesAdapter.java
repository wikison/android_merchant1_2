package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.util.ImageManager;

import java.util.List;

/**
 * Created by admin on 2016/6/13.
 */
public class HisRolesAdapter extends
        RecyclerView.Adapter<HisRolesAdapter.ViewHolder>
{

    private LayoutInflater mInflater;
    private List<M_Industry> mDatas;
    private ImageManager imageManager;
    private Context mContext;


    public void setData(List<M_Industry> list){
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }


    public HisRolesAdapter(Context context, List<M_Industry> datats)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mDatas = datats;
        imageManager = new ImageManager(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView iv;
        TextView tvLevel;
        TextView tv;
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.item_his_slash,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.iv = (ImageView) view.findViewById(R.id.iv);
        viewHolder.tvLevel = (TextView) view.findViewById(R.id.tv_level);
        viewHolder.tv = (TextView) view.findViewById(R.id.tv);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
        M_Industry industry = mDatas.get(position);
        // 场景头像 地址
        if (!TextUtils.isEmpty(industry.icon)) {
            imageManager.loadCircleHasBorderImage(industry.icon, holder.iv, 0xffb5b5b5, 1);
        }
        holder.tvLevel.setText("Lv." + industry.level);
        // 场景名称
        if (!TextUtils.isEmpty(industry.name))
            holder.tv.setText(industry.name);
    }

}
