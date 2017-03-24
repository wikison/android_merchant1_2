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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.GalleryAdapter;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.ImageManager;

import java.util.List;

/**
 * Created by admin on 2016/6/13.
 */
public class AllIndustryAdapter extends
        RecyclerView.Adapter<AllIndustryAdapter.ViewHolder>
{

    private LayoutInflater mInflater;
    private List<M_Industry> mDatas;
    private ImageManager imageManager;
    private Context mContext;
    private Activity mActivity;
    private int selectedId;


    public void setSelectedId(int selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setData(List<M_Industry> list){
        mDatas.clear();
        mDatas.addAll(list);

        selectedId = mDatas.get(0).id;
        notifyDataSetChanged();
    }

    public AllIndustryAdapter(Context context, List<M_Industry> datats)
    {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mActivity = (Activity) context;
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
        TextView tv;
        RelativeLayout ll;
        View viewSelect;
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
        View view = mInflater.inflate(R.layout.item_industry,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.tv = (TextView) view.findViewById(R.id.tv);
        viewHolder.ll = (RelativeLayout) view.findViewById(R.id.ll);
        viewHolder.viewSelect = (View) view.findViewById(R.id.view_select);

        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

        M_Industry industry = mDatas.get(position);
        // 场景名称
        if (!TextUtils.isEmpty(industry.name))
            holder.tv.setText(industry.name);

        if(industry.id == selectedId){
            holder.viewSelect.setVisibility(View.VISIBLE);
            holder.tv.setTextColor(0xffe6bb7c);
        } else{
            holder.viewSelect.setVisibility(View.GONE);
            holder.tv.setTextColor(0xff666666);
        }

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedId == mDatas.get(position).id)
                    return;

                setSelectedId(mDatas.get(position).id);
                if(mOnItemClickLitener != null){
                    mOnItemClickLitener.onItemClick(mDatas.get(position).id);
                }
            }
        });
    }

    /**
     * ItemClick的回调接口
     *
     */
    public interface OnItemClickLitener
    {
        void onItemClick(int industryId);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

}
