package com.zemult.merchant.activity.mine;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.aip.slash.User2RefreshSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class SaleManInfoImproveActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.tongxunlu_tv)
    TextView tongxunluTv;
    @Bind(R.id.notice_tv)
    TextView noticeTv;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;

    UserEditinfoRequest userEditinfoRequest;

    String headString = "", bookPhones;
    Activity mActivity;
    private boolean hasload;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_sale_man_info_improve);
    }

    @Override
    public void init() {
        mActivity = this;
        lhTvTitle.setText("完善资料");
        registerReceiver(new String[]{Constants.BROCAST_EDITUSERINFO});
        EditFilter.WordFilter(etName, 6);
        etName.setText(SlashHelper.userManager().getUserinfo().getName());

        //view加载完成时回调
        llRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!hasload){
                    hasload = true;
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, 100);
                }
            }
        });
    }

    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_EDITUSERINFO.equals(intent.getAction())) {
            if (!TextUtils.isEmpty(SlashHelper.userManager().getUserinfo().getHead())) {
                imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
            }
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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_head, R.id.tongxunlu_tv, R.id.notice_tv, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                setResult(RESULT_OK);
                onBackPressed();
                break;
            case R.id.iv_head:
                startActivityForResult(new Intent(this, HeadManageActivity.class), 110);
                break;
            case R.id.tongxunlu_tv:
                break;
            case R.id.notice_tv:
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", mActivity.getPackageName());
                    intent.putExtra("app_uid", mActivity.getApplicationInfo().uid);
                    startActivity(intent);
                } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.btn_save:
                if (StringUtils.isBlank(etName.getText().toString())) {
                    Toast.makeText(this, "请输入您的昵称", Toast.LENGTH_SHORT).show();
                } else {
                    user_editinfo();
                }
                break;
        }
    }

    //修改用户资料信息
    private void user_editinfo() {
        showPd();
        if (userEditinfoRequest != null) {
            userEditinfoRequest.cancel();
        }
        UserEditinfoRequest.Input input = new UserEditinfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.name = etName.getText().toString();
            input.convertJosn();
        }

        userEditinfoRequest = new UserEditinfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    SlashHelper.userManager().getUserinfo().setName(etName.getText().toString());
                    SlashHelper.userManager().saveUserinfo(SlashHelper.userManager().getUserinfo());
                    setResult(RESULT_OK);
                    Intent in = new Intent(Constants.BROCAST_LOGIN);
                    sendBroadcast(in);
                    SaleManInfoImproveActivity.this.finish();

                } else {
                    ToastUtils.show(SaleManInfoImproveActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userEditinfoRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 110 && resultCode == RESULT_OK) {
            headString = SlashHelper.userManager().getUserinfo().getHead();
            imageManager.loadCircleHead(headString, ivHead);
        }
    }

}
