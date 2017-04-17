package com.zemult.merchant.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Pic;
import com.zemult.merchant.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;

/**
 * 商家详情页头部
 *
 * @author djy
 * @time 2016/12/27 9:39
 */
public class HeaderMerchantDetailView extends HeaderViewInterface2<M_Merchant> {

    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.ll_ad_container)
    LinearLayout llAdContainer;
    @Bind(R.id.ll_lv_top)
    LinearLayout llLvTop;


    public HeaderMerchantDetailView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(M_Merchant m_merchant, ViewGroup viewGroup) {
        View view = mInflate.inflate(R.layout.include_merchant_detial_top, viewGroup, false);
        if (viewGroup instanceof ListView) {
            ListView listView = (ListView) viewGroup;
            listView.addHeaderView(view);
        } else {
            viewGroup.addView(view);
        }
        ButterKnife.bind(this, view);
    }

    public void dealWithTheView(final M_Merchant merchantInfo) {
        // 名字
        if (!TextUtils.isEmpty(merchantInfo.name))
            tvName.setText(merchantInfo.name);
        // 详细地址
        if (!TextUtils.isEmpty(merchantInfo.address))
            tvAddress.setText(merchantInfo.address);
        tvNum.setText(merchantInfo.payNum + "人找人服务");
        tvMoney.setText((int) (merchantInfo.perMoney) + "");
    }

    public void unShowLvTop(boolean unshowLvTop) {
       if (unshowLvTop)
           llLvTop.setVisibility(View.GONE);
    }

    public void dealWithTheView(final M_Merchant merchantInfo, final List<M_Pic> picList) {
        if (picList == null || picList.isEmpty()) {
            ivCover.setVisibility(View.VISIBLE);
            llAdContainer.setVisibility(View.GONE);
            // 封面
            if (!StringUtils.isBlank(merchantInfo.pic))
                mImageManager.loadUrlImageWithDefaultImg(merchantInfo.pic, ivCover, "@450h", R.mipmap.merchant_default_cover);
            else
                ivCover.setImageResource(R.mipmap.merchant_default_cover);
        } else {
            ivCover.setVisibility(View.GONE);
            llAdContainer.setVisibility(View.VISIBLE);
            // 设置广告数据 加入到smoothListView的headerView
            List<M_Ad> advertList = new ArrayList<>();

            for (M_Pic pic : picList) {
                M_Ad ad = new M_Ad();
                ad.setImg(pic.picPath);
                ad.setName(pic.picName);
                advertList.add(ad);
            }
            HeaderAdViewView headerAdViewView = new HeaderAdViewView(mContext, DensityUtil.dip2px(mContext, 200));
            headerAdViewView.setShowType(3);
            headerAdViewView.setRotate(false);
            headerAdViewView.setShowTitle(true);
            headerAdViewView.fillView(advertList, llAdContainer);

            headerAdViewView.setImageOnClick(new HeaderAdViewView.ImageOnClick() {
                @Override
                public void imageOnclick(int postion) {
                    if (imageOnClick != null)
                        imageOnClick.imageOnclick(picList.get(postion));
                }
            });
        }
    }

    public interface ImageOnClick {
        void imageOnclick(M_Pic pic);
    }

    private ImageOnClick imageOnClick;

    public void setImageOnClick(ImageOnClick imageOnClick) {
        this.imageOnClick = imageOnClick;
    }
}