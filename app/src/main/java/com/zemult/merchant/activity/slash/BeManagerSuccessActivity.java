package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeManagerSuccessActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    private SharePopwindow sharePopWindow;
    private String name;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_be_manager_success);
    }

    @Override
    public void init() {
        lhTvTitle.setText("成为服务管家");
        name = getIntent().getStringExtra(TabManageActivity.NAME);
        tvName.setText('"' + name + '"' );

        sharePopWindow = new SharePopwindow(BeManagerSuccessActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(BeManagerSuccessActivity.this, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【"+ name+"】的服务管家，可以为您提供优质的消费服务，快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【"+ name+"】的服务管家，可以为您提供优质的消费服务，快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【"+ name+"】的服务管家，可以为您提供优质的消费服务，快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【"+ name+"】的服务管家，可以为您提供优质的消费服务，快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;
                }
            }
        });
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_share, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.btn_ok:
                Intent intent = new Intent(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS);
                sendBroadcast(intent);

                onBackPressed();
                break;
            case R.id.btn_share:
                if (sharePopWindow.isShowing())
                    sharePopWindow.dismiss();
                else
                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                break;

        }
    }
}
