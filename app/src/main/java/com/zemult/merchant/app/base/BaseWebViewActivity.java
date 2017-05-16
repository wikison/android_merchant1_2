package com.zemult.merchant.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.MerchantEnter2Activity;
import com.zemult.merchant.aip.mine.User2ReservationInfoRequest;
import com.zemult.merchant.app.view.ProgressWebView;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;

import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;


/**
 * 使用条款和隐私策略
 */
public class BaseWebViewActivity extends MBaseActivity {

    String url, titlename, showShare, reservationId;
    private SharePopwindow sharePopWindow;
    private Context mContext;
    private Activity mActivity;

    M_Reservation mReservation = null;
    User2ReservationInfoRequest user2ReservationInfoRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        url = getIntent().getStringExtra("url");
        titlename = getIntent().getStringExtra("titlename") == null ? "" : getIntent().getStringExtra("titlename");
        showShare = getIntent().getStringExtra("share") == null ? "" : getIntent().getStringExtra("share");
        reservationId = getIntent().getStringExtra("reservationId") == null ? "" : getIntent().getStringExtra("reservationId");
        View appView = getLayoutInflater().inflate(
                R.layout.activity_clause_tactics, null);
        ProgressWebView wView = (ProgressWebView) appView.findViewById(R.id.webvRules);
        WebSettings wSet = wView.getSettings();
        wSet.setJavaScriptEnabled(true);
        wView.setWebViewClient(new MyWebViewClient());
        wView.loadUrl(url);
        appMainView.addView(appView, layoutParams);
        setTitleLeftButton("");
        setTitleText(titlename);
        showSharePopWindow();
        if (!StringUtils.isBlank(reservationId)) {
            userReservationInfo();
        }

    }

    private void userReservationInfo() {
        if (user2ReservationInfoRequest != null) {
            user2ReservationInfoRequest.cancel();
        }
        User2ReservationInfoRequest.Input input = new User2ReservationInfoRequest.Input();
        input.reservationId = reservationId;
        input.convertJosn();
        user2ReservationInfoRequest = new User2ReservationInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((M_Reservation) response).status == 1) {
                    mReservation = (M_Reservation) response;
                    if (showShare.equals("true")) {
                        showTitleRightImageButton(R.mipmap.fenxiang_icon, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mReservation != null) {
                                    if (sharePopWindow.isShowing())
                                        sharePopWindow.dismiss();
                                    else
                                        sharePopWindow.showAtLocation(appMainView, Gravity.BOTTOM, 0, 0);
                                }
                            }
                        });
                    }
                } else {
                }

            }
        });
        sendJsonRequest(user2ReservationInfoRequest);
    }

    private void showSharePopWindow() {
        sharePopWindow = new SharePopwindow(mContext, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(mContext, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约")
                                .withTargetUrl(url)
                                .withMedia(shareImage)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                                        "，诚挚邀请，期待您的赴约。")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约")
                                .withTargetUrl(url)
                                .withMedia(shareImage)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                                        "，诚挚邀请，期待您的赴约。")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约")
                                .withTargetUrl(url)
                                .withMedia(shareImage)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                                        "，诚挚邀请，期待您的赴约。")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(mActivity)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withTitle("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】邀您赴约")
                                .withTargetUrl(url)
                                .withMedia(shareImage)
                                .withText("您的好友【" + SlashHelper.userManager().getUserinfo().getName() + "】刚刚预定了" + mReservation.reservationTime + mReservation.merchantName +
                                        "，诚挚邀请，期待您的赴约。")
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

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String info = "";
            //此处根据需求处理逻辑...
            if (url.startsWith(Constants.SCHEME_PREFIX)) {
                info = url.substring(Constants.SCHEME_PREFIX.length());
                if (info.equalsIgnoreCase("merchantAdd")) {
                    if (!noLogin(BaseWebViewActivity.this)) {
                        Intent intent = new Intent();
                        intent.setClass(BaseWebViewActivity.this, MerchantEnter2Activity.class);
                        startActivity(intent);
                    }

                }

            } else {
                view.loadUrl(url);

            }
            return true;
        }

    }


}
