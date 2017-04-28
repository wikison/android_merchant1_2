package com.zemult.merchant.adapter.slashfrgment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.app.view.MeasuredGridView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Plan;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.imagepicker.ImagePickerAdapter;
import com.zemult.merchant.view.FixedGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * 服务方案
 *
 * @author djy
 * @time 2017/4/27 9:00
 */
public class ServicePlanAdapter extends BaseListAdapter<M_Plan> {
    private int saleUserId;

    public ServicePlanAdapter(Context context, List<M_Plan> list) {
        super(context, list);
    }

    public void setSaleUserId(int saleUserId) {
        this.saleUserId = saleUserId;
    }

    public void setData(List<M_Plan> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    // 删除单条记录
    public void delOneRecord(int pos) {
        getData().remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_service_plan, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        final M_Plan plan = getData().get(position);
        // 标题
        if (!StringUtils.isBlank(plan.name))
            holder.tvName.setText(plan.name);
        // 内容
        if (!StringUtils.isBlank(plan.note)){
            holder.tvContent.setText(plan.note);
            holder.tvContent.setVisibility(View.VISIBLE);
        } else
            holder.tvContent.setVisibility(View.GONE);

        if(saleUserId == SlashHelper.userManager().getUserId()){
            // 状态
            if(plan.state == 0){
                holder.tvClose.setVisibility(View.VISIBLE);
                holder.tvOpen.setVisibility(View.GONE);
            }else {
                holder.tvClose.setVisibility(View.GONE);
                holder.tvOpen.setVisibility(View.VISIBLE);
            }
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null)
                        onItemClickListener.onEditClick(plan);
                }
            });
            holder.llRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(onItemClickListener != null)
                        onItemClickListener.onLongClick(plan.planId, position);
                    return true;
                }
            });
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null)
                        onItemClickListener.onChooseClick(plan);
                }
            });
        }else {
            holder.tvClose.setVisibility(View.GONE);
            holder.tvOpen.setVisibility(View.GONE);
            holder.ivEdit.setVisibility(View.GONE);
        }

        if(!StringUtils.isBlank(plan.pics)){
            holder.gridview.setVisibility(View.VISIBLE);

            if(Arrays.asList(plan.pics.split(",")).size() == 4){
                holder.gridview.setNumColumns(2);

                holder.gridview.getLayoutParams().width = DensityUtil.dip2px(mContext, 165);
            }else{
                holder.gridview.setNumColumns(3);
                holder.gridview.getLayoutParams().width = DensityUtil.dip2px(mContext, 250);
            }

            PhotoFix3Adapter adapter = new PhotoFix3Adapter(mContext, plan.pics, 9);
            adapter.setOnImageClickListener(new PhotoFix3Adapter.OnImageClickListener() {
                @Override
                public void onImageClick(int pos, List<String> photos) {
                    AppUtils.toImageDetial((Activity) mContext, pos, photos, new ArrayList<String>());
                }
            });
            holder.gridview.setAdapter(adapter);
        }else
            holder.gridview.setVisibility(View.GONE);

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_open)
        RoundTextView tvOpen;
        @Bind(R.id.tv_close)
        RoundTextView tvClose;
        @Bind(R.id.iv_edit)
        ImageView ivEdit;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.gridview)
        FixedGridView gridview;
        @Bind(R.id.ll_root)
        LinearLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener{
        void onEditClick(M_Plan plan);
        void onChooseClick(M_Plan plan);
        void onLongClick(int planId, int pos);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
