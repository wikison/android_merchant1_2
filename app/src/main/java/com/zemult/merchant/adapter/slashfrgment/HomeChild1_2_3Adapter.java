package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.view.FNRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * HomeChild1_2_3Adapter
 *
 * @author djy
 * @time 2017/3/28 11:27
 */
public class HomeChild1_2_3Adapter extends BaseListAdapter<M_Merchant> {

    private boolean isNoData;
    private int mHeight;

    public HomeChild1_2_3Adapter(Context context, List<M_Merchant> list) {
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
            convertView = mInflater.inflate(R.layout.item_home_child_1_2_3, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (getCount() == 1)
            holder.oneDataShow.setVisibility(View.VISIBLE);
        else
            holder.oneDataShow.setVisibility(View.GONE);
        M_Merchant entity = getItem(position);
        initData(holder, entity, position);
        initTags(holder, entity);

        return convertView;
    }

    /**
     * 设置数据
     *
     * @param holder
     * @param entity
     */
    private void initData(ViewHolder holder, final M_Merchant entity, final int position) {
        // 服务管家的用户昵称
        if (!TextUtils.isEmpty(entity.saleUserName))
            holder.tvUserName.setText(entity.saleUserName);
        // 服务管家的用户头像
        mImageManager.loadCircleHead(entity.saleUserHead, holder.ivHead);
        // 商家名称
        if (!TextUtils.isEmpty(entity.name))
            holder.tvMerchantName.setText(entity.name);
        // 人均消费
        holder.tvMoney.setText("人均" + (int) (entity.perMoney) + "元");
        // 距中心点距离(米)
        if (!StringUtils.isEmpty(entity.distance)) {
            if (entity.distance.length() > 3) {
                double d = Double.valueOf(entity.distance);
                holder.tvDistance.setText(d / 1000 + "km");
            } else
                holder.tvDistance.setText(entity.distance + "m");
        }
        // 服务管家的顾客数
        holder.tvNum.setText(entity.saleUserFanNum + "关注");
        holder.ivService.setImageResource(entity.getExperienceImg());
        holder.tvService.setText(entity.getExperienceText() + "管家");
        // 前7日的服务分总和
        holder.tvZhishu.setText("7天服务指数" + (entity.saleUserSumScore / 7));
        // 签约商户
        if (entity.reviewstatus == 2)
            holder.ivQianyue.setVisibility(View.VISIBLE);
        else
            holder.ivQianyue.setVisibility(View.GONE);
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
                mImageManager.loadRoundImage2((String) path, imageView, 24, "@450h");
            }
        });
        holder.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, entity.saleUserId);
                intent.putExtra(UserDetailActivity.MERCHANT_ID, entity.merchantId);
                mContext.startActivity(intent);
            }
        });
        holder.banner.setIndicatorGravity(BannerConfig.CENTER).start();

        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, entity.saleUserId);
                intent.putExtra(UserDetailActivity.MERCHANT_ID, entity.merchantId);
                mContext.startActivity(intent);
            }
        });
    }


    private void initTags(ViewHolder holder, M_Merchant entity) {
        if (TextUtils.isEmpty(entity.saleUserTags))
            holder.rgTaService.setVisibility(View.GONE);
        else {
            holder.rgTaService.setVisibility(View.VISIBLE);
            holder.rgTaService.setChildMargin(0, 0, 24, 36);
            holder.rgTaService.removeAllViews();
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.saleUserTags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffffffff);  // 边框内部颜色
                    drawable.setStroke(1, 0xffdcdcdc);

                    RadioButton rbTag = new RadioButton(mContext);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff999999);
                    rbTag.setText(tagList.get(i).toString());

                    holder.rgTaService.addView(rbTag);
                }
            }
        }
    }

    static class ViewHolder {
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.iv_service)
        ImageView ivService;
        @Bind(R.id.tv_service)
        TextView tvService;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.tv_merchant_name)
        TextView tvMerchantName;
        @Bind(R.id.tv_job)
        TextView tvJob;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.banner)
        Banner banner;
        @Bind(R.id.iv_qianyue)
        ImageView ivQianyue;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.tv_zhishu)
        TextView tvZhishu;
        @Bind(R.id.one_data_show)
        View oneDataShow;
        @Bind(R.id.ll_root)
        LinearLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
