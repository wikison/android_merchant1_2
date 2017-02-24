package com.zemult.merchant.adapter.slashfrgment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import com.hedgehog.ratingbar.RatingBar;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.view.FNRadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商户详情页适配器
 *
 * @author djy
 * @time 2016/12/27 10:00
 */
public class MerchantDetailAdpater extends BaseListAdapter<M_Userinfo> {

    private boolean isNoData, halfScreen;
    private int mHeight;
    private int noDividerPos = -1;

    public MerchantDetailAdpater(Context context, List<M_Userinfo> list) {
        super(context, list);
    }

    // 设置数据
    public void setData(List<M_Userinfo> list, boolean isLoadMore) {
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

    public void setHalfScreen(boolean halfScreen) {
        this.halfScreen = halfScreen;
    }

    // 设置数据
    public void setData(List<M_Userinfo> listFan, List<M_Userinfo> listAll) {
        List<M_Userinfo> data = new ArrayList<>();
        if (listFan == null || listFan.isEmpty()) {
//            M_Userinfo userinfo = new M_Userinfo();
//            userinfo.setUserId(-1);
//            listFan = new ArrayList<>();
//            listFan.add(userinfo);
        } else {
            noDividerPos = listFan.size() - 1;
            listFan.get(0).showLatest = true;
            data.addAll(listFan);
        }

        if (listAll == null || listAll.isEmpty()) {
//            M_Userinfo userinfo = new M_Userinfo();
//            userinfo.setUserId(-1);
//            listAll = new ArrayList<>();
//            listAll.add(userinfo);
        } else {
            listAll.get(0).showAll = true;
            data.addAll(listAll);
        }

        clearAll();
        addALL(data);
        isNoData = false;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 暂无数据
        if (isNoData) {
            if (halfScreen)
                convertView = mInflater.inflate(R.layout.item_no_merchant_data_layout, null);
            else
                convertView = mInflater.inflate(R.layout.item_no_data_layout, null);

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeight);
            RelativeLayout rootView = ButterKnife.findById(convertView, R.id.rl_no_data);
            rootView.setLayoutParams(params);
            return convertView;
        }
        // 正常数据
        final ViewHolder holder;
        if (convertView != null && convertView instanceof LinearLayout) {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        } else {
            convertView = mInflater.inflate(R.layout.item_merchant_detail, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        }

        if (noDividerPos == position)
            holder.divider.setVisibility(View.GONE);
        else
            holder.divider.setVisibility(View.VISIBLE);

        M_Userinfo entity = getItem(position);
        // 设置数据
        initData(holder, entity);
        initListner(holder, position);
        initTags(holder, entity);
        return convertView;
    }

    private void initData(ViewHolder holder, M_Userinfo entity) {
        // 用户头像
        if (!TextUtils.isEmpty(entity.getUserHead()))
            mImageManager.loadCircleImage(entity.getUserHead(), holder.ivHead, "@120w_120h_1e");
        else
            holder.ivHead.setImageResource(R.mipmap.user_icon);
        // 用户名
        if (!TextUtils.isEmpty(entity.getUserName()))
            holder.tvUserName.setText(entity.getUserName());

        if (entity.showLatest || entity.showAll) {
            holder.llFenlei.setVisibility(View.VISIBLE);
            if (entity.showLatest)
                holder.tvFenlei.setText("熟人");
            else
                holder.tvFenlei.setText("全部");
        } else
            holder.llFenlei.setVisibility(View.GONE);

        if (entity.getUserId() == -1) {
            holder.tvNoData.setVisibility(View.VISIBLE);
            holder.llUser.setVisibility(View.GONE);
        } else {
            holder.tvNoData.setVisibility(View.GONE);
            holder.llUser.setVisibility(View.VISIBLE);
        }

        if (entity.commentNumber > 0) {
            float starFloat = (float) entity.comment / (float) entity.commentNumber;
            float starInt = (float) (entity.comment / entity.commentNumber);
            holder.ratingbar.setStar(starFloat - starInt >= 0.5 ? starInt + 0.5f : starInt);
        } else
            holder.ratingbar.setStar(0);

        holder.tvNum.setText("服务" + entity.saleNum + "人次");
        holder.tvComment.setText(entity.commentNumber + "人评价");

        holder.ivService.setImageResource(entity.getExperienceImg());
        holder.tvService.setText(entity.getExperienceText());
        holder.ivStatus.setImageResource(entity.getStatusImg(entity.getUserState()));
        holder.tvStatus.setTextColor(entity.getStatusTextColor(entity.getUserState()));
        holder.tvStatus.setText(entity.getStatusText(entity.getUserState()));
    }

    /**
     * 设置监听器
     *
     * @param holder
     * @param position
     */
    private void initListner(ViewHolder holder, final int position) {
        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMerchantDetailClick != null)
                    onMerchantDetailClick.onUserClick(position);
            }
        });
        holder.ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMerchantDetailClick != null)
                    onMerchantDetailClick.onHeadClick(position);
            }
        });
    }

    private void initTags(ViewHolder holder, M_Userinfo entity) {
        if (TextUtils.isEmpty(entity.tags))
            holder.rgTaService.setVisibility(View.GONE);
        else {
            holder.rgTaService.setVisibility(View.VISIBLE);
            holder.rgTaService.setChildMargin(0, 0, 24, 36);
            holder.rgTaService.removeAllViews();
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                for (int i = 0; i < iShowSize; i++) {
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE); // 画框
                    drawable.setCornerRadii(new float[]{50,
                            50, 50, 50, 50, 50, 50, 50});
                    drawable.setColor(0xffe8e8e8);  // 边框内部颜色
                    RadioButton rbTag = new RadioButton(mContext);
                    rbTag.setBackgroundDrawable(drawable); // 设置背景（效果就是有边框及底色）
                    rbTag.setTextSize(12);
                    rbTag.setPadding(22, 8, 22, 8);
                    rbTag.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
                    rbTag.setTextColor(0xff464646);
                    rbTag.setText(tagList.get(i).toString());

                    holder.rgTaService.addView(rbTag);
                }
            }
        }
    }


    private OnMerchantDetailClick onMerchantDetailClick;

    public void setOnMerchantDetailClick(OnMerchantDetailClick onMerchantDetailClick) {
        this.onMerchantDetailClick = onMerchantDetailClick;
    }

    public interface OnMerchantDetailClick {
        void onUserClick(int position);

        void onHeadClick(int position);
    }

    static class ViewHolder {
        @Bind(R.id.tv_fenlei)
        TextView tvFenlei;
        @Bind(R.id.ll_fenlei)
        LinearLayout llFenlei;
        @Bind(R.id.tv_no_data)
        TextView tvNoData;
        @Bind(R.id.iv_head)
        ImageView ivHead;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.iv_service)
        ImageView ivService;
        @Bind(R.id.tv_service)
        TextView tvService;
        @Bind(R.id.iv_status)
        ImageView ivStatus;
        @Bind(R.id.tv_status)
        TextView tvStatus;
        @Bind(R.id.ratingbar)
        RatingBar ratingbar;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.tv_num)
        TextView tvNum;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.ll_user)
        LinearLayout llUser;
        @Bind(R.id.divider)
        View divider;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
