package com.zemult.merchant.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.bugtags.library.Bugtags;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.ButterKnife;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.VolleyUtil;

/**
 * Created by wikison on 2016/6/2.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected ArrayList<WeakReference<Request>> listJsonRequest = new ArrayList<WeakReference<Request>>();
    public LoadingDialog loadingDialog;
    private InternalReceiver internalReceiver;

    /**
     * 初始化layout
     */
    public abstract void setContentView();

    public ImageManager imageManager;

    /**
     * 初始化数据和空间
     */
    public abstract void init();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView();
        ButterKnife.bind(this);
        checkStoragePermission();
        imageManager = new ImageManager(getApplicationContext());
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setMessageText("数据加载...");
        init();
//        AppUtils.isNetworkAvailable(this);
    }

    protected void checkStoragePermission() {
        boolean bWritePermission = AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (bWritePermission) {

        } else {
            getWritePermission();
        }
    }


    public void showPd() {
        try {
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showUncanclePd() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }

    public void dismissPd() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {
        AppUtils.isNetworkAvailable(this);
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request);
    }

    /*
     * 返回
     */
    public void doBack(View view) {
        onBackPressed();
    }

    protected final void registerReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }
        IntentFilter intentfilter = new IntentFilter();
        for (String action : actionArray) {
            intentfilter.addAction(action);
        }
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }

    // Internal calss.
    private class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            handleReceiver(context, intent);
        }
    }

    /**
     * 如果子界面需要拦截处理注册的广播 需要实现该方法
     *
     * @param context
     * @param intent
     */
    protected void handleReceiver(Context context, Intent intent) {
        // 广播处理
        if (intent == null) {
            return;
        }
    }

    @Override
    protected void onDestroy() {
        if (listJsonRequest != null && !listJsonRequest.isEmpty()) { //遍历取消所有请求
            for (WeakReference<Request> ref : listJsonRequest) {
                Request req = ref.get();
                if (req != null) {
                    req.cancel();
                }
            }
        }
        try {
            if (internalReceiver != null) {
                unregisterReceiver(internalReceiver);
            }
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    public boolean noLogin(final Context context) {
        // 没有登录跳转到登录界面
        if (SlashHelper.userManager().getUserinfo() == null) {
            CommonDialog.showDialogListener(context, null, "取消", "去登录", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();
                    startActivity(new Intent(context, LoginActivity.class));
                }
            });
            return true;
        }
        return false;
    }

    public boolean noHead(final Context context) {
        // 没有设置头像跳转到设置头像界面
        if (StringUtils.isBlank(SlashHelper.userManager().getUserinfo().getHead())) {
            CommonDialog.showDialogListener(context, null, "取消", "去设置", "您目前还没有设置过头像，要成为优秀的服务管家请先设置头像哦", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();
                    startActivity(new Intent(context, HeadManageActivity.class));
                }
            });
            return true;
        }
        return false;
    }

    private void getWritePermission() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .send();
    }

    @PermissionYes(100)
    private void getWriteYes() {

    }

    @PermissionNo(100)
    private void getWriteNo() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

}
