package com.zemult.merchant.activity;

import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.view.SharePopwindow;

import butterknife.Bind;
import butterknife.OnClick;

public class ShareAppointmentActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.webview)
    WebView webview;
    @Bind(R.id.ll_root)
    RelativeLayout llRoot;
    private SharePopwindow popwindow;
    String shareurl,sharecontent,sharetitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_share_appointment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("生成邀请函");

        WebSettings wSet = webview.getSettings();
        wSet.setJavaScriptEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        shareurl=getIntent().getStringExtra("shareurl");
        sharecontent=getIntent().getStringExtra("sharecontent");
        sharetitle=getIntent().getStringExtra("sharetitle");
        webview.loadUrl(shareurl);


        popwindow = new SharePopwindow(ShareAppointmentActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(ShareAppointmentActivity.this, R.mipmap.icon_launcher);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl)
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.WECHAT:
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl)
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl)
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.QQ:
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl)
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                }
            }
        });

    }


    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
             view.loadUrl(url);
            return true;
        }

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_share:
                if (popwindow.isShowing())
                    popwindow.dismiss();
                else
                    popwindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置
                break;
        }
    }




    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享成功啦", Toast.LENGTH_SHORT).show();
            }
            popwindow.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享失败啦", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }
    };
}
