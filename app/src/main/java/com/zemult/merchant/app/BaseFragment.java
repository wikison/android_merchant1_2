package com.zemult.merchant.app;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginNewActivity;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.VolleyUtil;

/**
 * Created by wikison on 2016/6/2.
 */
public abstract class BaseFragment extends Fragment {
    protected boolean isVisible;
    public LoadingDialog loadingDialog;
    protected ArrayList<WeakReference<Request>> listJsonRequest = new ArrayList<WeakReference<Request>>();
    private InternalReceiver internalReceiver;
    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("lazyLoad", "获取界面数据-----setUserVisibleHint");
        if (getUserVisibleHint()) {
            Log.i("lazyLoad", "获取界面数据-----setUserVisibleHinttrue");
            isVisible = true;
            onVisible();
        } else {
            Log.i("lazyLoad", "获取界面数据-----setUserVisibleHintfalse");
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
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
                getActivity().unregisterReceiver(internalReceiver);
            }
        } catch (Exception e) {
        }

        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setMessageText("数据加载...");
    }

    protected void showPd() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }
    protected void showUncanclePd() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
        }
    }


    protected void dismissPd() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
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
        getActivity().registerReceiver(internalReceiver, intentfilter);
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

    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {
        AppUtils.isNetworkAvailable(getActivity());
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
    }

    public boolean noLogin(final Context context) {
        // 没有登录跳转到登录界面
        if (SlashHelper.userManager().getUserinfo() == null) {
            CommonDialog.showDialogListener(context, null, "否", "是", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();
                    startActivity(new Intent(context, LoginNewActivity.class));
                }
            });
            return true;
        }
        return false;
    }
}
