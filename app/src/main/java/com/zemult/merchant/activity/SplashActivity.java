package com.zemult.merchant.activity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.umeng.socialize.utils.Log;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonAppVersionRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.NewVersionDialog;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.service.NewVersionUpdateService;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.util.Timer;
import java.util.TimerTask;

import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/22.
 */
public class SplashActivity extends BaseActivity {
    int iTime = 0;
    String releaseVersion = "";
    CommonAppVersionRequest commonAppVersionRequest;
    UserLoginRequest user_login_request;
    private Timer mTimer = null;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimer = new Timer();
        this.mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                mHandler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    public void init() {
        initDebugVersion();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iTime++;
            if (iTime > 5) {
                if(null!=mTimer){
                    mTimer.cancel();
                }
                mTimer = null;
                if (null != SlashHelper.userManager().getUserinfo()) {
                    get_user_login_request();
                }
            }

            super.handleMessage(msg);
        }
    };

    public void commonAppVersion() {
        if (commonAppVersionRequest != null) {
            commonAppVersionRequest.cancel();
        }
        CommonAppVersionRequest.Input input = new CommonAppVersionRequest.Input();
        input.type = "0";
        input.version = releaseVersion;
        input.convertJosn();

        commonAppVersionRequest = new CommonAppVersionRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                loadingDialog.dismiss();
                int status = ((APIM_CommonAppVersion) response).status;

                SlashHelper.setSettingString(SlashHelper.APP.Key.URL_SHARE_APP, ((APIM_CommonAppVersion) response).url_share_app);
                SlashHelper.setSettingString(SlashHelper.APP.Key.URL_SHARE_NEWS, ((APIM_CommonAppVersion) response).url_share_news);
                SlashHelper.setSettingString(SlashHelper.APP.Key.URL_SHARE_TASK, ((APIM_CommonAppVersion) response).url_share_taskIndustryRecord);
                SlashHelper.setSettingString(SlashHelper.APP.Key.URL_SHARE_COMMISSION, ((APIM_CommonAppVersion) response).url_share_commission);
                if (status == 1) {
                    if (!releaseVersion.equals(((APIM_CommonAppVersion) response).appInfo.version)) {
                        NewVersionDialog newVersionDialog = new NewVersionDialog();
                        newVersionDialog.setCancelable(false);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("appinfo", ((APIM_CommonAppVersion) response).appInfo);
                        newVersionDialog.setArguments(bundle);
                        newVersionDialog.show(getSupportFragmentManager(), "newVersion");
                        newVersionDialog.setUpdateCallback(new NewVersionDialog.UpdateCallback() {
                            @Override
                            public void onUpload() {
                                Intent intent = new Intent(SplashActivity.this, NewVersionUpdateService.class);
                                intent.putExtra("appName", getApplicationContext().getResources().getString(R.string.app_name) + System.currentTimeMillis());
                                intent.putExtra("updateUrl", ((APIM_CommonAppVersion) response).appInfo.path);
                                startService(intent);
                                ToastUtil.showMessage("下载中...");
                                if (((APIM_CommonAppVersion) response).appInfo.isForce == 0) {//强制更新 是否强制更新(0:否,1:是)
                                    Intent i = new Intent();
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.setClass(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    SplashActivity.this.finish();
                                } else {
                                    ToastUtil.showMessage("此版本有重要更新，新版本下载中，请稍后...");
                                    showPd();
                                }
                            }

                            @Override
                            public void onCancel() {
                                if (((APIM_CommonAppVersion) response).appInfo.isForce == 0) {//强制更新 是否强制更新(0:否,1:是)
                                    Intent i = new Intent();
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.setClass(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    SplashActivity.this.finish();
                                } else {
                                    ToastUtil.showMessage("此版本有重要更新，请更新以后再使用");
                                }
                            }
                        });
                    } else {
                        Intent i = new Intent();
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.setClass(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        SplashActivity.this.finish();
                    }

                } else {
                    ToastUtil.showMessage(((APIM_CommonAppVersion) response).info);
                }
            }
        });
        sendJsonRequest(commonAppVersionRequest);
    }


    /**
     * 测试环境下，显示编译号
     */
    private void initDebugVersion() {

        try {
            ApplicationInfo appInfo = AppApplication.instance().getPackageManager()
                    .getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

            String version = appInfo.metaData.getString("DEBUG_VERSION");
            releaseVersion = appInfo.metaData.getString("RELEASE_VERSION").replace("v", "");
            commonAppVersion();
//            if (!AppApplication.ISDEBUG) {
//                return;
//            }

        } catch (Exception e) {
            Log.e("获取Debug版本号失败");
        }

    }

    private void get_user_login_request() {
        if (user_login_request != null) {
            user_login_request.cancel();
        }
        UserLoginRequest.Input input = new UserLoginRequest.Input();
        input.account = SlashHelper.userManager().getUserinfo().getPhoneNum();
        input.password = SlashHelper.userManager().getUserinfo().getPassword();
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();

        user_login_request = new UserLoginRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(final Object response) {
//                if (((APIM_UserLogin) response).status == 1) {
//                } else {
//                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
//                }
            }
        });
        sendJsonRequest(user_login_request);
    }

}
