package com.zemult.merchant.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.view.common.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.VolleyUtil;

/**
 * Created by zhangkai on 2016/6/14.
 */
/**
 * 初始化广播接收器
 */



public class MAppCompatActivity extends AppCompatActivity {

    private InternalReceiver internalReceiver;
    protected ArrayList<WeakReference<Request>> listJsonRequest;
    public LoadingDialog loadingDialog;
    public ImageManager imageManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadingDialog = new LoadingDialog(this);
        imageManager=new ImageManager(this);
        loadingDialog.setMessageText("数据加载...");
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
        if (listJsonRequest == null) {
            listJsonRequest = new ArrayList<WeakReference<Request>>();
        }
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
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
        if (listJsonRequest != null) { //遍历取消所有请求
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

}
