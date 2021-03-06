package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.BaseListAdapter;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.view.FNRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2016/12/28.
 */

public class TaMerchantAdapter extends BaseListAdapter<M_Merchant> {
    Context mContext;

    public TaMerchantAdapter(Context context, List<M_Merchant> list) {
        super(context, list);
        mContext = context;
    }

    // 设置数据 任务
    public void setData(List<M_Merchant> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_his_merchant, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        M_Merchant entity = getItem(position);
        initData(holder, entity);
        initListener(holder, position);
        return convertView;
    }

    private void initListener(final ViewHolder holder, final int position) {
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAllClickListener != null)
                    onAllClickListener.onAllClick(position);
            }
        });
    }

    private void initTags(ViewHolder holder, M_Merchant entity) {
        holder.rgTaService.setChildMargin(0, 24, 24, 0);
        holder.rgTaService.removeAllViews();
        if (!StringUtils.isBlank(entity.tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                holder.rgTaService.setVisibility(View.VISIBLE);

                RadioButton rbTitle = new RadioButton(mContext);
                rbTitle.setTextSize(15);
                rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                rbTitle.setPadding(0, 0, 8, 0);
                rbTitle.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                rbTitle.setTextColor(0xff282828);
                rbTitle.setText("服务项目");
                holder.rgTaService.addView(rbTitle);

                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(mContext);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTitle.setGravity(Gravity.CENTER_VERTICAL);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    holder.rgTaService.addView(rbTag);

                }
            } else {
                holder.rgTaService.setVisibility(View.GONE);
            }
        }

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
            mImageManager.loadUrlImage(entity.pic, holder.ivCover, "@320h");
        else
            holder.ivCover.setImageResource(R.mipmap.user_icon);
        // 商家名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvName.setText(entity.name);
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

        if (entity.commentNumber != 0) {
            holder.rb5.setStar(entity.comment / entity.commentNumber);
        } else {
            holder.rb5.setStar(0);
        }
        holder.tvComment.setText(entity.commentNumber + "人评价");
        holder.tvService.setText("服务" + entity.saleNum + "人次");
        if (!StringUtils.isBlank(entity.createTime)) {
            holder.tvAddTime.setText("于" + entity.createTime.substring(0, 10) + "加入");
        } else {

        }

        initTags(holder, entity);
    }

    /**
     * 点击Item
     */
    private OnAllClickListener onAllClickListener;

    public void setOnAllClickListener(OnAllClickListener onAllClickListener) {
        this.onAllClickListener = onAllClickListener;
    }

    public interface OnAllClickListener {
        void onAllClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.rb_5)
        RatingBar rb5;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.tv_service)
        TextView tvService;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.card_view)
        FrameLayout cardView;
        @Bind(R.id.tv_add_time)
        TextView tvAddTime;
        @Bind(R.id.ll_root)
        RoundLinearLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
