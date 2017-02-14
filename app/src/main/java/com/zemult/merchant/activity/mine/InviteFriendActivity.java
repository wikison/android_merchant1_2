package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.QrImageUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;

import butterknife.Bind;
import butterknife.OnClick;

public class InviteFriendActivity extends BaseActivity {

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
    @Bind(R.id.iv_share_img)
    ImageView ivShareImg;
    @Bind(R.id.btn_share)
    Button btnShare;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    private Context mContext;
    private Activity mActivity;

    Bitmap bitmap;
    private SharePopwindow sharePopWindow;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_invite_friend);
    }

    @Override
    public void init() {

        mContext = this;
        mActivity = this;

        initView();
        initListener();

        getNetworkData();
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(getResources().getString(R.string.title_invite_friend) + getResources().getString(R.string.app_name));
        btnShare.setText("邀请好友");
        String URL_SHARE_APP = SlashHelper.getSettingString(SlashHelper.APP.Key.URL_SHARE_APP, Constants.APP_DOWNLOAD_URL);
        bitmap = QrImageUtil.createQRImage(URL_SHARE_APP, DensityUtil.dip2px(this, 240),
                DensityUtil.dip2px(this, 240));
        if (bitmap != null)
            ivShareImg.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    private void initListener() {
        sharePopWindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(mContext, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("您的好友【"+ SlashHelper.userManager().getUserinfo().getName()+"】正在使用YOVOLL约服APP，商务消费、找人服务首选平台，赶快也下载一个用用~")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("约服-找个喜欢的人来服务")
                                .share();
                        break;
                }
            }
        });
    }

    /**
     * 访问网络接口
     */
    private void getNetworkData() {

    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.btn_share: {
                if (sharePopWindow.isShowing())
                    sharePopWindow.dismiss();
                else
                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
            }
            break;


        }
    }


    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            sharePopWindow.dismiss();
            ToastUtil.showMessage("分享取消");
        }
    };

}
