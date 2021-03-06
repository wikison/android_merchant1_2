package com.zemult.merchant.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
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

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.User2ReservationEditInvitationRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.ShareText;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

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
    String shareurl,sharecontent,sharetitle,sharename="";
    User2ReservationEditInvitationRequest user2ReservationEditInvitationRequest;
    int reservationId;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_share_appointment);
    }

    @Override
    public void init() {
        lhTvTitle.setText("生成邀请函");

        WebSettings wSet = webview.getSettings();
        wSet.setJavaScriptCanOpenWindowsAutomatically(true);
        wSet.setJavaScriptEnabled(true);
        wSet.setDomStorageEnabled(true);
        webview.setWebViewClient(new MyWebViewClient());
        shareurl=getIntent().getStringExtra("shareurl");
        sharecontent=getIntent().getStringExtra("sharecontent");
        sharetitle=getIntent().getStringExtra("sharetitle");
        reservationId=getIntent().getIntExtra("reservationId",0);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(shareurl);


        popwindow = new SharePopwindow(ShareAppointmentActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(ShareAppointmentActivity.this, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        user2_reservation_editInvitation();
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl+"&type=1")
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.WECHAT:
                        user2_reservation_editInvitation();
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl+"&type=1")
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        user2_reservation_editInvitation();
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl+"&type=1")
                                .withMedia(shareImage).withTitle(sharetitle)
                                .share();
                        break;
                    case SharePopwindow.QQ:
                        user2_reservation_editInvitation();
                        new ShareAction(ShareAppointmentActivity.this)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText(sharecontent)
                                .withTargetUrl(shareurl+"&type=1")
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
            if (url.contains("doShare")) {//   js://doShare?name=
                Uri uri = Uri.parse(url);
//                try {
//                    sharename= URLDecoder.decode(uri.getQueryParameter("name"),"UTF-8");
                    sharename=uri.getQueryParameter("name");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                String oldname="【" + SlashHelper.userManager().getUserinfo().getName() + "】";
                sharecontent=sharecontent.replace(oldname,"【" + sharename + "】");
                sharetitle=sharetitle.replace(oldname,"【" + sharename + "】");


                if (popwindow.isShowing())
                    popwindow.dismiss();
                else
                    popwindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置


            }
           else  if (url.contains("baidumap://map/?")){
            }
            else{
                view.loadUrl(url);
            }

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

                break;
        }
    }


    //生成邀请函(服务单)
    private void user2_reservation_editInvitation() {
        try {
            if (user2ReservationEditInvitationRequest != null) {
                user2ReservationEditInvitationRequest.cancel();
            }
            User2ReservationEditInvitationRequest.Input input = new User2ReservationEditInvitationRequest.Input();
            input.reservationId = reservationId;
            input.convertJosn();

            user2ReservationEditInvitationRequest = new User2ReservationEditInvitationRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    if (((CommonResult) response).status == 1) {
                        setResult(RESULT_OK);
                        finish();

                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(user2ReservationEditInvitationRequest);
        } catch (Exception e) {
        }
    }

    UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {

            com.umeng.socialize.utils.Log.d("plat", "platform" + platform);
            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享成功", Toast.LENGTH_SHORT).show();
            }
            popwindow.dismiss();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享失败", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShareAppointmentActivity.this, ShareText.shareMediaToCN(platform) + " 分享取消了", Toast.LENGTH_SHORT).show();
            popwindow.dismiss();
        }
    };
}
