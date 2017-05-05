package com.zemult.merchant.activity.mine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.aip.slash.User2RefreshSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class SalemanInfoSettingActivity extends BaseActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.tv_name)
    TextView tvName;
    String nameString = "", headString = "",bookPhones;
    @Bind(R.id.notice_layout)
    RelativeLayout noticeLayout;
    @Bind(R.id.tongxulu_layout)
    RelativeLayout tongxuluLayout;
    @Bind(R.id.textView1)
    TextView textView1;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    private boolean hasload;
    Activity mActivity;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_saleman_info_setting);
    }

    @Override
    public void init() {
        mActivity = this;
        lhTvTitle.setText("完善个人资料");
        initData();
        registerReceiver(new String[]{Constants.BROCAST_EDITUSERINFO});

        //view加载完成时回调
        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!hasload) {
                    hasload = true;
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, 100);
                }
            }
        });
    }

    private void initData() {
        tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
        nameString = SlashHelper.userManager().getUserinfo().getName();
        headString = SlashHelper.userManager().getUserinfo().getHead();
    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_EDITUSERINFO.equals(intent.getAction())) {
            initData();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0) {
            try {
                bookPhones = AppUtils.getPhoneNumbers(mActivity);
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



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.mis_headgo_layout, R.id.mis_name_layout, R.id.notice_layout, R.id.tongxulu_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                setResult(RESULT_OK);
                onBackPressed();
                break;
            case R.id.mis_headgo_layout:
                startActivityForResult(new Intent(SalemanInfoSettingActivity.this, HeadManageActivity.class), 110);
                break;
            case R.id.mis_name_layout:
                Intent intent = new Intent(SalemanInfoSettingActivity.this, NicknameActivity.class);
                startActivityForResult(intent, Constants.REQUESTCODE_CHANGENICKNAME);
                break;
            case R.id.notice_layout:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent2 = new Intent();
                    intent2.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent2.putExtra("app_package", mActivity.getPackageName());
                    intent2.putExtra("app_uid", mActivity.getApplicationInfo().uid);
                    startActivity(intent2);
                } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Intent intent3 = new Intent();
                    intent3.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent3.addCategory(Intent.CATEGORY_DEFAULT);
                    intent3.setData(Uri.parse("package:" + mActivity.getPackageName()));
                    startActivity(intent3);
                }
                break;
            case R.id.tongxulu_layout:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUESTCODE_CHANGENICKNAME && resultCode == RESULT_OK) {
            nameString = SlashHelper.userManager().getUserinfo().getName();
            //修改用户资料信息
            tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        } else if (requestCode == 110 && resultCode == RESULT_OK) {
            headString = SlashHelper.userManager().getUserinfo().getHead();
            imageManager.loadCircleImage(headString, ivHead);
        }
        initData();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
