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
    private ViewStateClickListener onViewStateClickListener;

    public void setOnViewClickListener(ViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void setOnViewMerchantClickListener(ViewMerchantClickListener onViewMerchantClickListener) {
        this.onViewMerchantClickListener = onViewMerchantClickListener;
    }

    public void setOnViewStateClickListener(ViewStateClickListener onViewStateClickListener) {
        this.onViewStateClickListener = onViewStateClickListener;
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
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_user_merchant_head, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (!TextUtils.isEmpty(entity.merchantPic))
            imageManager.loadRoundImage(entity.merchantPic, holder.cardImg, 24, Color.WHITE, 10, "@300h");

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
        // 签约商户
        if (entity.reviewstatus == 2)
            holder.ivQianyue.setVisibility(View.VISIBLE);
        else
            holder.ivQianyue.setVisibility(View.GONE);

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
        if (!TextUtils.isEmpty(entity.merchantName))
            holder.tvName.setText(entity.merchantName);


        holder.tvSeven.setText(entity.sumScore / 7 + "");
        String strPosition = (entity.position == null ? "" : (entity.position.equals("无") ? "" : entity.position));
        holder.tvServicePosition.setText(strPosition);

        holder.tvServicePlan.setText(entity.planName);
        holder.tvComment.setText(entity.commentNum + "条评价");

        initTags(holder, entity);
        dealState(holder, entity);
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
        if (!TextUtils.isEmpty(entity.merchantName))
            holder.tvName.setText(entity.merchantName);
        holder.tvSeven.setText(entity.sumScore / 7 + "");
        String strPosition = (entity.position == null ? "" : (entity.position.equals("无") ? "" : entity.position));
        holder.tvServicePosition.setText(strPosition);
        if (entity.unSureOrderNum > 0) {
            holder.llServiceRecord.setVisibility(View.VISIBLE);
            holder.tvUnsureNum.setText("待确认服务单" + entity.unSureOrderNum + "条");
        } else {
            holder.llServiceRecord.setVisibility(View.GONE);
            holder.tvUnsureNum.setText("");
        }
        holder.tvServicePlan.setText(entity.planName);
        holder.tvComment.setText(entity.commentNum + "条评价");
        initTags(holder, entity);
        dealState(holder, entity);
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

        holder.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onCommentList(entity);
            }
        });

        holder.rlServicePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onServicePlanList(entity);
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

        holder.rlServiceRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onServiceList(entity);
            }
        });

        holder.rlRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onServiceHistoryList(entity);
            }
        });

        holder.tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewStateClickListener != null)
                    onViewStateClickListener.onStateManage(entity);
            }
        });

        holder.rlComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onCommentList(entity);
            }
        });

        holder.rlServicePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null)
                    onViewClickListener.onServicePlanList(entity);
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

    //处理状态
    private void dealState(Object object, M_Merchant entity) {
        if (object instanceof ViewHolderDetail) {
            ViewHolderDetail holder = (ViewHolderDetail) object;
            if (entity.state == 0) {
                holder.ivState.setImageResource(R.mipmap.kongxian);
                holder.tvState.setText("空闲");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_idle));
            } else if (entity.state == 1) {
                holder.ivState.setImageResource(R.mipmap.xiuxi_icon);
                holder.tvState.setText("休息");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_black_999));
            } else if (entity.state == 2) {
                holder.ivState.setImageResource(R.mipmap.manglu);
                holder.tvState.setText("忙碌");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_busy));
            }
        } else if (object instanceof ViewHolderSelfDetail) {
            ViewHolderSelfDetail holder = (ViewHolderSelfDetail) object;
            if (entity.state == 0) {
                holder.ivState.setImageResource(R.mipmap.kongxian);
                holder.tvState.setText("空闲");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_idle));
            } else if (entity.state == 1) {
                holder.ivState.setImageResource(R.mipmap.xiuxi_icon);
                holder.tvState.setText("休息");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_black_999));
            } else if (entity.state == 2) {
                holder.ivState.setImageResource(R.mipmap.manglu);
                holder.tvState.setText("忙碌");
                holder.tvState.setTextColor(mContext.getResources().getColor(R.color.font_busy));
            }
        }


    }

    static class ViewHolder {
        @Bind(R.id.card_img)
        ImageView cardImg;
        @Bind(R.id.tv_distance)
        TextView tvDistance;
        @Bind(R.id.tv_money)
        TextView tvMoney;
        @Bind(R.id.iv_qianyue)
        ImageView ivQianyue;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderDetail {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_service_position)
        TextView tvServicePosition;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.rl_detail)
        RelativeLayout rlDetail;
        @Bind(R.id.ll_detail)
        LinearLayout llDetail;
        @Bind(R.id.tv_text_state)
        TextView tvTextState;
        @Bind(R.id.iv_state)
        ImageView ivState;
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.tv_service_plan)
        TextView tvServicePlan;
        @Bind(R.id.rl_service_plan)
        RelativeLayout rlServicePlan;
        @Bind(R.id.tv_seven)
        TextView tvSeven;
        @Bind(R.id.rl_seven)
        RelativeLayout rlSeven;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.rl_comment)
        RelativeLayout rlComment;

        ViewHolderDetail(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ViewHolderSelfDetail {
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_service_position)
        TextView tvServicePosition;
        @Bind(R.id.rg_ta_service)
        FNRadioGroup rgTaService;
        @Bind(R.id.rl_detail)
        RelativeLayout rlDetail;
        @Bind(R.id.ll_detail)
        LinearLayout llDetail;
        @Bind(R.id.tv_text_state)
        TextView tvTextState;
        @Bind(R.id.iv_state)
        ImageView ivState;
        @Bind(R.id.tv_state)
        TextView tvState;
        @Bind(R.id.tv_text_service_plan)
        TextView tvTextServicePlan;
        @Bind(R.id.tv_design_plan)
        TextView tvDesignPlan;
        @Bind(R.id.tv_service_plan)
        TextView tvServicePlan;
        @Bind(R.id.rl_service_plan)
        RelativeLayout rlServicePlan;
        @Bind(R.id.tv_comment)
        TextView tvComment;
        @Bind(R.id.rl_comment)
        RelativeLayout rlComment;
        @Bind(R.id.tv_text_seven)
        TextView tvTextSeven;
        @Bind(R.id.tv_add_seven)
        TextView tvAddSeven;
        @Bind(R.id.tv_seven)
        TextView tvSeven;
        @Bind(R.id.tv_unsure_num)
        TextView tvUnsureNum;
        @Bind(R.id.ll_service_record)
        LinearLayout llServiceRecord;
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

        //查看交易记录
        void onServiceHistoryList(M_Merchant entity);

        //评价
        void onCommentList(M_Merchant entity);

        //服务方案
        void onServicePlanList(M_Merchant entity);
    }

    public interface ViewMerchantClickListener {
        void onMerchantManage(M_Merchant entity);
    }

    public interface ViewStateClickListener {
        void onStateManage(M_Merchant entity);
    }

}
