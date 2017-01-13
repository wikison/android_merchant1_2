package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
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
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ImageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/6/13.
 */
public class HomePeopleAdapter extends
        RecyclerView.Adapter<HomePeopleAdapter.ViewHolder>
{

    private LayoutInflater mInflater;
    private List<String> mDatas;
    private ImageManager imageManager;


    public HomePeopleAdapter(Context context, String heads)
    {
        mInflater = LayoutInflater.from(context);
        initPhotos(heads);
        imageManager = new ImageManager(context);
    }

    private void initPhotos(String pic){
        mDatas = new ArrayList<>();
        if(TextUtils.isEmpty(pic)){
            mDatas.add("");
            return;
        }

        if (pic.contains(",")) {
            String[] photosarray = pic.split(",");

            for (int i = 0; i < photosarray.length; i++) {
                mDatas.add(photosarray[i]);
            }
        } else {
            mDatas.add(pic);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View arg0)
        {
            super(arg0);
        }

        ImageView iv;
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
        View view = mInflater.inflate(R.layout.item_home_people,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.iv = (ImageView) view.findViewById(R.id.iv_head);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(!TextUtils.isEmpty(mDatas.get(position)))
            imageManager.loadCircleImage(mDatas.get(position), holder.iv, "@70w_70h_1e");
        else
            holder.iv.setImageResource(R.mipmap.yueke_icon);
    }
}
