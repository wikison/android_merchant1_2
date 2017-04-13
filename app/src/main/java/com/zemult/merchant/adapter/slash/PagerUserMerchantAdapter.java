package com.zemult.merchant.adapter.slash;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.LinkagePager;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.FNRadioGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * Created by Wikison on 2017/3/15.
 */

public class PagerUserMerchantAdapter extends PagerAdapter {
    int type = 0;// 0图片 1详细信息
    boolean isSelf = false;
    Context mContext;
    private List<M_Merchant> merchantList = new ArrayList<M_Merchant>();
    ImageManager imageManager;
    private List<WeakReference<LinearLayout>> viewList;
    private LinkagePager pager;
    LayoutInflater inflater = null;
    private ViewClickListener onViewClickListener;
    private ViewMerchantClickListener onViewMerchantClickListener;
    private ViewTagClickListener onViewTagClickListener;

    public void setOnViewClickListener(ViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setOnViewMerchantClickListener(ViewMerchantClickListener onViewMerchantClickListener) {
        this.onViewMerchantClickListener = onViewMerchantClickListener;
    }

    public void setOnViewTagClickListener(ViewTagClickListener onViewTagClickListener) {
        this.onViewTagClickListener = onViewTagClickListener;
    }


    public PagerUserMerchantAdapter(Context context, List<M_Merchant> merchantList, int type, boolean isSelf) {
        mContext = context;
        this.merchantList = merchantList;
        this.type = type;
        this.isSelf = isSelf;
        imageManager = new ImageManager(context);
        viewList = new ArrayList<WeakReference<LinearLayout>>();
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        M_Merchant entity = merchantList.get(position);
        if (pager == null) {
            pager = (LinkagePager) container;
        }

        switch (type) {
            case 0:
                view = initHeadView(null, entity);
                pager.addView(view);
                break;
            case 1:
                if (viewList.size() > 0) {
                    if (viewList.get(0) != null) {
                        view = initDetailView(viewList.get(0).get(), entity);
                        viewList.remove(0);
                    }
                }
                if (isSelf) {
                    view = initSelfDetailView(null, entity);

                } else {
                    view = initDetailView(null, entity);
                }

                pager.addView(view);
                break;
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return merchantList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


    private View initHeadView(View view, M_Merchant entity) {
        ViewHolder viewHolder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_user_merchant_head, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(entity.merchantPic))
            imageManager.loadRoundImage(entity.merchantPic, viewHolder.cardImg, 24, Color.WHITE, 10, "@300h");

        return view;
    }

    private View initDetailView(View view, M_Merchant entity) {
        ViewHolderDetail holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_user_merchant_detail, null);
            holder = new ViewHolderDetail(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolderDetail) view.getTag();
        }
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

        holder.tvSeven.setText(entity.sumScore / 7 + "");
        holder.tvCommentNum.setText(entity.commentNum + "条评价");

        initTags(holder, entity);
        initListener(holder, entity);

        return view;
    }

    private View initSelfDetailView(View view, M_Merchant entity) {
        ViewHolderSelfDetail holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_self_user_merchant_detail, null);
            holder = new ViewHolderSelfDetail(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolderSelfDetail) view.getTag();
        }
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
        holder.tvSeven.setText(entity.sumScore / 7 + "");
        holder.tvCommentNum.setText(entity.commentNum + "条评价");
        if (entity.unSureOrderNum > 0) {
            holder.tvUnsureNum.setText("待确认服务单" + entity.unSureOrderNum + "条");
        } else {
            holder.tvUnsureNum.setText("");
        }
        initTags(holder, entity);
        initListener(holder, entity);

        return view;
    }

    private void initListener(ViewHolderDetail holder, final M_Merchant entity) {
        holder.rlDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onDetail(entity);
            }
        });
        holder.llDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onDetail(entity);
            }
        });

    }

    private void initListener(ViewHolderSelfDetail holder, final M_Merchant entity) {
        holder.llDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewMerchantClickListener != null)
                    onViewMerchantClickListener.onMerchantManage(entity);
            }
        });

        holder.rlService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewTagClickListener != null)
                    onViewTagClickListener.onTagManage(entity);
            }
        });

        holder.rlServiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onServiceList(entity);
            }
        });


    }


    private void initTags(ViewHolderSelfDetail holder, M_Merchant entity) {
        holder.rgTaService.setChildMargin(0, 24, 24, 0);
        holder.rgTaService.removeAllViews();
        if (!StringUtils.isBlank(entity.tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                holder.rgTaService.setVisibility(View.VISIBLE);

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

    private void initTags(ViewHolderDetail holder, M_Merchant entity) {
        holder.rgTaService.setChildMargin(0, 24, 24, 0);
        holder.rgTaService.removeAllViews();
        if (!StringUtils.isBlank(entity.tags)) {
            List<String> tagList = new ArrayList<String>(Arrays.asList(entity.tags.split(",")));
            int iShowSize = tagList.size();
            if (iShowSize > 0) {
                holder.rgTaService.setVisibility(View.VISIBLE);

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

    static class ViewHolder {
        @Bind(R.id.card_img)
        ImageView cardImg;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderDetail {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.rl_detail)
        RelativeLayout rlDetail;
        @Bind(R.id.ll_detail)
        LinearLayout llDetail;
        @Bind(R.id.tv_seven)
        TextView tvSeven;
        @Bind(R.id.tv_activity)
        TextView tvActivity;
        @Bind(R.id.rl_activity)
        RelativeLayout rlActivity;
        @Bind(R.id.tv_comment_num)
        TextView tvCommentNum;
        @Bind(R.id.rl_service_comment)
        RelativeLayout rlServiceComment;

        ViewHolderDetail(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderSelfDetail {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.rl_detail)
        RelativeLayout rlDetail;
        @Bind(R.id.ll_detail)
        LinearLayout llDetail;
        @Bind(R.id.tv_seven)
        TextView tvSeven;
        @Bind(R.id.tv_text_service)
        TextView tvTextService;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.tv_service)
        TextView tvService;
        @Bind(R.id.rl_service)
        RelativeLayout rlService;
        @Bind(R.id.tv_activity)
        TextView tvActivity;
        @Bind(R.id.rl_activity)
        RelativeLayout rlActivity;
        @Bind(R.id.tv_comment_num)
        TextView tvCommentNum;
        @Bind(R.id.rl_service_comment)
        RelativeLayout rlServiceComment;
        @Bind(R.id.tv_unsure_num)
        TextView tvUnsureNum;
        @Bind(R.id.iv_service_record)
        ImageView ivServiceRecord;
        @Bind(R.id.rl_service_record)
        RelativeLayout rlServiceRecord;
        @Bind(R.id.rl_record)
        RelativeLayout rlRecord;

        ViewHolderSelfDetail(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface ViewClickListener {
        //查看详情
        void onDetail(M_Merchant entity);
        //查看服务单列表
        void onServiceList(M_Merchant entity);
    }

    public interface ViewMerchantClickListener {
        void onMerchantManage(M_Merchant entity);
    }

    public interface ViewTagClickListener {
        void onTagManage(M_Merchant entity);
    }

}
