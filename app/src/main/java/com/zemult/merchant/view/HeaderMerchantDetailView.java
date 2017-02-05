package com.zemult.merchant.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.util.Convert;

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
    @Bind(R.id.tv_pic_num)
    RoundTextView tvPicNum;
    @Bind(R.id.tv_qianyue)
    RoundTextView tvQianyue;


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
        // 封面
        if (!TextUtils.isEmpty(merchantInfo.pic))
            mImageManager.loadUrlImageWithDefaultImg(merchantInfo.pic, ivCover, "@320h", R.mipmap.merchant_default_cover);
        else
            ivCover.setImageResource(R.mipmap.merchant_default_cover);

        tvPicNum.setText(merchantInfo.picNum + "张");
        // 名字
        if (!TextUtils.isEmpty(merchantInfo.name))
            tvName.setText(merchantInfo.name);
        // 详细地址
        if (!TextUtils.isEmpty(merchantInfo.address))
            tvAddress.setText(merchantInfo.address);
        tvNum.setText(merchantInfo.payNum + "人到店消费");
        tvMoney.setText((int)(merchantInfo.perMoney) + "元");
        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headerClickListener != null)
                    headerClickListener.onCoverClick();
            }
        });
        if(merchantInfo.reviewstatus == 2)
            tvQianyue.setVisibility(View.VISIBLE);
    }

    public interface OnHeaderClickListener {
        void onCoverClick();
    }

    private OnHeaderClickListener headerClickListener;

    public void setOnHeaderClickListener(OnHeaderClickListener headerClickListener) {
        this.headerClickListener = headerClickListener;
    }

}