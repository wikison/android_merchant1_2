package com.zemult.merchant.activity.slash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.aip.slash.User2RefreshSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SharePopwindow;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

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
    @Bind(R.id.iv_head)
    ImageView ivHead;
    private SharePopwindow sharePopWindow;
    private String name;
    private String bookPhones;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_be_manager_success);
    }

    @Override
    public void init() {
        lhTvTitle.setText("成为服务管家");
        name = getIntent().getStringExtra(TabManageActivity.NAME);
        tvName.setText('"' + name + '"');
        if (name.length() > 10)
            tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);

        sharePopWindow = new SharePopwindow(BeManagerSuccessActivity.this, new SharePopwindow.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UMImage shareImage = new UMImage(BeManagerSuccessActivity.this, R.mipmap.icon_share);

                switch (position) {
                    case SharePopwindow.SINA:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.SINA)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【" + name + "】的服务管家,可以为您提供优质的消费服务,快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;

                    case SharePopwindow.WECHAT:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【" + name + "】的服务管家,可以为您提供优质的消费服务,快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;
                    case SharePopwindow.WECHAT_FRIEND:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【" + name + "】的服务管家,可以为您提供优质的消费服务,快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;

                    case SharePopwindow.QQ:
                        new ShareAction(BeManagerSuccessActivity.this)
                                .setPlatform(SHARE_MEDIA.QQ)
                                .setCallback(umShareListener)
                                .withText("我刚刚申请了【" + name + "】的服务管家,可以为您提供优质的消费服务,快来看看...")
                                .withTargetUrl(Constants.APP_DOWNLOAD_URL)
                                .withMedia(shareImage)
                                .withTitle("有人@你-为您提供优质的消费服务")
                                .share();
                        break;
                }
            }
        });

        //view加载完成时回调
        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                ActivityCompat.requestPermissions(BeManagerSuccessActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            try {
                bookPhones = AppUtils.getPhoneNumbers(BeManagerSuccessActivity.this);
                // 只能用这种折中的方法了
                if (StringUtils.isBlank(bookPhones)) {
                    bookPhones = "";
                }
            } catch (Exception e) {
                bookPhones = "";
            }
            user2_reflash_saleuser();
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_share, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.btn_ok:
                Intent intent = new Intent(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS);
                sendBroadcast(intent);

                finish();
                break;
            case R.id.btn_share:
                if (sharePopWindow.isShowing())
                    sharePopWindow.dismiss();
                else
                    sharePopWindow.showAtLocation(llRoot, Gravity.BOTTOM, 0, 0);
                break;

        }
    }

    /**
     * 用户更新服务管家通讯录
     */
    User2RefreshSaleUserRequest refreshSaleUserRequest;

    private void user2_reflash_saleuser() {
        showPd();
        if (refreshSaleUserRequest != null) {
            refreshSaleUserRequest.cancel();
        }
        User2RefreshSaleUserRequest.Input input = new User2RefreshSaleUserRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.bookPhones = bookPhones;
        input.convertJosn();
        refreshSaleUserRequest = new User2RefreshSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(refreshSaleUserRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
