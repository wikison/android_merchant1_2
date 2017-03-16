package com.zemult.merchant.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.DensityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 商家详情页头部
 *
 * @author djy
 * @time 2016/12/27 9:39
 */
public class HeaderMerchantDetailView extends HeaderViewInterface<M_Merchant> {

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
    @Bind(R.id.tv_qianyue)
    RoundTextView tvQianyue;
    @Bind(R.id.ll_ad_container)
    LinearLayout llAdContainer;


    public HeaderMerchantDetailView(Activity context) {
        super(context);
    }

    @Override
    protected void getView(M_Merchant m_merchant, ListView listView) {
        View view = mInflate.inflate(R.layout.include_merchant_detial_top, listView, false);
        listView.addHeaderView(view);
        ButterKnife.bind(this, view);
    }

    public void dealWithTheView(final M_Merchant merchantInfo) {
        if(merchantInfo.picNum == 0){
            ivCover.setVisibility(View.VISIBLE);
            llAdContainer.setVisibility(View.GONE);
            // 封面
            if (!TextUtils.isEmpty(merchantInfo.pic))
                mImageManager.loadUrlImageWithDefaultImg(merchantInfo.pic, ivCover, "@340h", R.mipmap.merchant_default_cover);
            else
                ivCover.setImageResource(R.mipmap.merchant_default_cover);
        }else {
            ivCover.setVisibility(View.GONE);
            llAdContainer.setVisibility(View.VISIBLE);
            // 设置广告数据 加入到smoothListView的headerView
            List<M_Ad> advertList = new ArrayList<>();
            String[] array = merchantInfo.pics.split(",");
            for(String s : array){
                M_Ad ad = new M_Ad();
                ad.setImg(s);
                advertList.add(ad);
            }
            HeaderAdViewView headerAdViewView = new HeaderAdViewView(mContext, DensityUtil.dip2px(mContext, 200));
            headerAdViewView.setRotate(false);
            headerAdViewView.fillView(advertList, llAdContainer);
        }
        // 名字
        if (!TextUtils.isEmpty(merchantInfo.name))
            tvName.setText(merchantInfo.name);
        // 详细地址
        if (!TextUtils.isEmpty(merchantInfo.address))
            tvAddress.setText(merchantInfo.address);
        tvNum.setText(merchantInfo.payNum + "人找人服务");
        tvMoney.setText((int) (merchantInfo.perMoney) + "");
        if (merchantInfo.reviewstatus == 2)
            tvQianyue.setVisibility(View.VISIBLE);
    }

}