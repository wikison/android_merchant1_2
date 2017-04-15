package com.zemult.merchant.activity.mine;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.QrImageUtil;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SharePopwindow;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by wikison on 2016/6/21.
 */
public class MyQr4OrderActivity extends BaseActivity {

    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.set_btn)
    Button setBtn;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.iv_merchant_head)
    ImageView ivMerchantHead;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.ll_merchant_head)
    LinearLayout llMerchantHead;
    @Bind(R.id.iv_qr)
    ImageView ivQr;
    int userSaleId, merchantId;

    String merchantHead, merchantName;
    Bitmap bitmap;
    String qrInfo;
    String saleHead,saleName,shareUrl;
    double money,experience;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    String sharecontent,sharetitle;
    SharePopwindow sharePopWindow;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_qr_order);
    }

    @Override
    public void init() {
        initData();
        initView();

        sharePopWindow = new SharePopwindow(MyQr4OrderActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(MyQr4OrderActivity.this, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.WECHAT:
                        new ShareAction(MyQr4OrderActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareUrl)
                                .withMedia(shareImage)
                                .withTitle(sharetitle)
                                .share();
                        break;
                }
            }
        });
        sharePopWindow.showType(true,false,false,false);
    }


    private void initData() {
        userSaleId= getIntent().getIntExtra("saleUserId", 0);
        merchantId= getIntent().getIntExtra("merchantId", 0);
        money=getIntent().getDoubleExtra("money",0);
        experience=getIntent().getDoubleExtra("experience",0);
        saleHead=getIntent().getStringExtra("saleHead");
        saleName=getIntent().getStringExtra("saleName");
        merchantName=getIntent().getStringExtra("merchantName");
        setBtn.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        imageManager.loadCircleImage(saleHead, ivHead);
        tvName.setText(saleName);
        tvLevel.setText(Convert.getExperienceText(experience));
        Drawable drawable = getResources().getDrawable(Convert.getExperienceImg(SlashHelper.userManager().getUserinfo().getExperience()));
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvLevel.setCompoundDrawables(drawable, null, null, null);
            llMerchantHead.setVisibility(View.VISIBLE);
            setBtn.setVisibility(View.VISIBLE);
            lhTvTitle.setText("交易二维码");
            moneyTv.setVisibility(View.VISIBLE);
            moneyTv.setText("￥ " + money);
            tvMerchantName.setText(merchantName);
            qrInfo = "merchantId=" + merchantId + "&userId=" + userSaleId + "&price=" + money;
            bitmap = QrImageUtil.createQRImage(Constants.QR_PAY_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));

            shareUrl="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx22ea2af5e7d47cb1&redirect_uri=http://www.yovoll.com/dzyx/wappay/wappay_index_wx.do?client="+merchantId + "," + userSaleId+"&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect";


        if (bitmap != null)
            ivQr.setImageBitmap(bitmap);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.set_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.set_btn:
//                https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx22ea2af5e7d47cb1&redirect_uri=http://www.yovoll.com/dzyx/wappay/wappay_index_wx.do?client=xx1,xx2&response_type=code&scope=snsapi_userinfo&state=0#wechat_redirect
//                说明:xx1为merchantId,xx2为saleUserId
                sharecontent="我是" + merchantName + "的服务管家【" +saleName + "】, 我为您提供更快捷的结账服务...";
                sharetitle="约服-找个喜欢的人来服务";


                if (sharePopWindow.isShowing())
                    sharePopWindow.dismiss();
                else
                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);

                break;
        }
    }



    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(MyQr4OrderActivity.this, ShareText.shareMediaToCN(platform) + " 收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyQr4OrderActivity.this, ShareText.shareMediaToCN(platform) + " 分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(MyQr4OrderActivity.this, ShareText.shareMediaToCN(platform) + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(MyQr4OrderActivity.this, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

}
