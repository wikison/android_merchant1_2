package com.zemult.merchant.activity.slash;

import android.net.Uri;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.User2ReservationEditInvitationRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class SharePreInviteActivity extends BaseActivity {

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
    @Bind(R.id.btn_share)
    RoundTextView btnShare;
    @Bind(R.id.rl_share)
    RelativeLayout rlShare;
    User2ReservationEditInvitationRequest user2ReservationEditInvitationRequest;
    String preId, invitor, activityName, time, feedbackTime, note;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_share_appointment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("");

        WebSettings wSet = webview.getSettings();
        wSet.setJavaScriptCanOpenWindowsAutomatically(true);
        wSet.setJavaScriptEnabled(true);
        wSet.setDomStorageEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(Constants.SHARE_PREINVITATION_ADD + SlashHelper.userManager().getUserId());
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("sharePreInvite")) { //   js://doShare?name=
                Uri uri = Uri.parse(url);
                preId = uri.getQueryParameter("preId");
                invitor = uri.getQueryParameter("invitor");
                activityName = uri.getQueryParameter("activityName");
                time = uri.getQueryParameter("time");
                feedbackTime = uri.getQueryParameter("feedbackTime");
                note = uri.getQueryParameter("note");

            }
            shareToWX();

            return true;
        }

    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_share:
                shareToWX();
                break;
        }
    }

    private void shareToWX() {
        if (!AppUtils.isWeixinAvailable(this)) {
            ToastUtil.showMessage("你还没有安装微信");
            return;
        }
        UMImage shareImage;
        shareImage = new UMImage(this, R.mipmap.icon_share);

        //分享到微信
        new ShareAction(SharePreInviteActivity.this)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener)
                .withText("您的好友【" + invitor + "】拟于" + time.substring(0, 4) + "年" + time.substring(5, 7) + "月" + time.substring(8, 10) + "日" + time.substring(11, 16) + "举行" + activityName + ", 请确认")
                .withTargetUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx22ea2af5e7d47cb1&redirect_uri=http://www.yovoll.com/dzyx/app/share_preInvitation_info.do?preId=" + preId + "&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect")
                .withMedia(shareImage)
                .withTitle("您的好友【" + invitor + "】发起了一个" + activityName)
                .share();
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(SharePreInviteActivity.this, ShareText.shareMediaToCN(platform) + " 收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SharePreInviteActivity.this, ShareText.shareMediaToCN(platform) + " 分享成功", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SharePreInviteActivity.this, ShareText.shareMediaToCN(platform) + " 分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(SharePreInviteActivity.this, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };


}
