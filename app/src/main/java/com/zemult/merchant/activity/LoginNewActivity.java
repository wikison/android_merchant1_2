package com.zemult.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.ThirdBandPhoneActivity;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserGetPwdRequest;
import com.zemult.merchant.aip.common.UserLogin2_3Request;
import com.zemult.merchant.aip.common.UserWxBandUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;

import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/6/12.
 */
public class LoginNewActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.iv_right2)
    ImageView ivRight2;
    @Bind(R.id.ll_right2)
    LinearLayout llRight2;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.user_icon)
    ImageView userIcon;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_sendcode)
    RoundTextView tvSendcode;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.iv_wx)
    ImageView ivWx;
    @Bind(R.id.al_tv_notnow)
    TextView alTvNotnow;


    String strPhone, strCode;
    UserLogin2_3Request user_login_request;
    CommonGetCodeRequest request_common_getcode;
    private LoginSampleHelper loginHelper;
    private UMShareAPI umShareAPI;
    private static final int WAIT = 0x001;
    private boolean isWait = false;
    private Thread mThread = null;
    int from;


    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_LOGIN.equals(intent.getAction())) {
            finish();
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login_new);
    }

    @Override
    public void init() {
        initViews();
        from = getIntent().getIntExtra("guide_login", 0);
        loginHelper = LoginSampleHelper.getInstance();
        umShareAPI = UMShareAPI.get(this);
        registerReceiver(new String[]{Constants.BROCAST_LOGIN});
    }

    private void initViews() {
        lhBtnBack.setVisibility(View.GONE);

        lhTvTitle.setText("登录");
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundResource(R.drawable.next_bg_btn_select);

        String strLoginPhone = SlashHelper.getSettingString("last_login_phone", "");
        if (!StringUtils.isBlank(strLoginPhone) && strLoginPhone.length() == 11) {
            imageManager.loadCircleHead(SlashHelper.getSettingString(strLoginPhone, ""), userIcon);
            etCode.requestFocus();
        }

        cbAgree.setChecked(true);

        etPhone.setText(strLoginPhone);
        etPhone.clearFocus();

        etPhone.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher2);
        etCode.addTextChangedListener(watcher2);

        if (!AppUtils.isWeixinAvailable(LoginNewActivity.this))
            ivWx.setVisibility(View.GONE);

    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etPhone.getText().toString().length() == 11 && !SlashHelper.getSettingString(etPhone.getText().toString(), "").equals("")) {
                imageManager.loadCircleImage(SlashHelper.getSettingString(etPhone.getText().toString(), ""), userIcon);
            } else {
                userIcon.setImageResource(R.mipmap.user_icon);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher watcher2 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etPhone.getText().toString().length() > 0
                        && etCode.getText().toString().length() > 0) {
                    btnLogin.setEnabled(true);
                    btnLogin.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnLogin.setEnabled(false);
                btnLogin.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //设置超链接文字
    public SpannableString setLinkText(String spanStr) {
        SpannableString spannableString = new SpannableString(spanStr);
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.al_tv_notnow:
                        LoginNewActivity.this.finish();
                        break;
                }

            }
        }, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void login() {
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            etPhone.setError("手机号不能为空");
        }
        if (!StringMatchUtils.isMobileNO(strPhone)) {
            ToastUtil.showMessage("请输入正确的手机号码");
            return;
        }
        if (!StringUtils.isBlank(strPhone) && !StringUtils.isBlank(strCode))
            get_user_login_request();
    }

    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeRequest.Input input = new CommonGetCodeRequest.Input();
            input.phone = strPhone;
            input.convertJosn();

            request_common_getcode = new CommonGetCodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ToastUtil.showMessage("验证码已发送, 请查收!");
                        tvSendcode.setText("重新获取(" + 60 + "s)");
                        tvSendcode.setClickable(false);
                        tvSendcode.setTextColor(0xff828282);
                        tvSendcode.getDelegate().setStrokeColor(0xff828282);
                        waitForClick();
                    } else
                        ToastUtil.showMessage(((CommonResult) response).info);
                }
            });
            sendJsonRequest(request_common_getcode);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }

    }

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler() {
            int i = 60;

            public void handleMessage(Message msg) {
                i--;
                tvSendcode.setText("重新获取(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新获取");
                    tvSendcode.setClickable(true);
                    tvSendcode.setTextColor(getResources().getColor(R.color.font_main));
                    tvSendcode.getDelegate().setStrokeColor(getResources().getColor(R.color.font_main));
                    i = 60;
                }
            }
        };

        mThread = new Thread() {
            @Override
            public void run() {
                while (isWait) {
                    try {
                        handler.sendEmptyMessage(WAIT);
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mThread.start();
    }

    //用户登录
    private void get_user_login_request() {
        loadingDialog.show();
        if (user_login_request != null) {
            user_login_request.cancel();
        }
        UserLogin2_3Request.Input input = new UserLogin2_3Request.Input();
        input.account = strPhone;
        input.code = strCode;
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();

        user_login_request = new UserLogin2_3Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    if (null != getIntent().getStringExtra("actfrom") && "notification".equals(getIntent().getStringExtra("actfrom"))) {
                        Intent notificationIntent = new Intent(LoginNewActivity.this, SplashActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(notificationIntent);
                    } else {
                        AppUtils.initIm(((APIM_UserLogin) response).userInfo.getUserId() + "", Urls.APP_KEY);
                        loginHelper.login_Sample(((APIM_UserLogin) response).userInfo.getUserId() + "", ((APIM_UserLogin) response).userInfo.getPassword(), Urls.APP_KEY, new IWxCallback() {
                            @Override
                            public void onSuccess(Object... arg0) {
                                loadingDialog.dismiss();
                                UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
                                SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());
                                setResult(RESULT_OK);
                                Intent intent = new Intent(Constants.BROCAST_LOGIN);
                                sendBroadcast(intent);
                                finish();
                            }

                            @Override
                            public void onProgress(int arg0) {

                            }

                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                loadingDialog.dismiss();
                                if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登录
                                } else {
                                    Notification.showToastMsg(LoginNewActivity.this, errorMessage);
                                }
                            }
                        });
                    }

                } else {
                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
                }
                loadingDialog.dismiss();
            }
        });
        sendJsonRequest(user_login_request);
    }

    @OnClick({R.id.iv_wx, R.id.btn_login, R.id.tv_sendcode, R.id.tv_protocol, R.id.al_tv_notnow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wx:
                thirdLogin();
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.al_tv_notnow:
                if (from == 1) {
                    startActivity(new Intent(this, MainActivity.class));
                    from = 0;
                }
                LoginNewActivity.this.finish();
                break;
            case R.id.tv_sendcode:
                strPhone = etPhone.getText().toString();
                if (StringUtils.isBlank(strPhone)) {
                    ToastUtil.showMessage("手机号不能为空");
                    return;
                }
                if (!StringMatchUtils.isMobileNO(strPhone)) {
                    ToastUtil.showMessage("请输入正确的手机号码");
                    return;
                }
                getCode();
                break;
            case R.id.tv_protocol:
                IntentUtil.start_activity(LoginNewActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.app_name) + "服务协议"), new Pair<String, String>("url", Constants.PROTOCOL_REGISTER));
                break;
        }
    }

    private void thirdLogin() {
        if (!umShareAPI.isInstall(LoginNewActivity.this, SHARE_MEDIA.WEIXIN)) {
            return;
        }
        showPd();
        umShareAPI.doOauthVerify(LoginNewActivity.this, SHARE_MEDIA.WEIXIN, doOauthVerifyListener);
    }

    private UMAuthListener doOauthVerifyListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            UMShareAPI.get(LoginNewActivity.this).getPlatformInfo(LoginNewActivity.this, SHARE_MEDIA.WEIXIN, getPlatformInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            dismissPd();
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            dismissPd();
            Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };
    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            user_wx_band_user(data.get("unionid"), data.get("nickname"), data.get("headimgurl"));
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            dismissPd();
            Toast.makeText(getApplicationContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            dismissPd();
            Toast.makeText(getApplicationContext(), "获取信息取消", Toast.LENGTH_SHORT).show();
        }
    };

    private UserWxBandUserRequest userWxBandUserRequest;

    //根据微信号获取绑定的用户信息
    private void user_wx_band_user(final String openid, final String nickname, final String head) {
        loadingDialog.show();
        if (userWxBandUserRequest != null) {
            userWxBandUserRequest.cancel();
        }
        UserWxBandUserRequest.Input input = new UserWxBandUserRequest.Input();
        input.openid = openid;
        input.convertJosn();

        userWxBandUserRequest = new UserWxBandUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((CommonResult) response).status == 1) {
                    if (((CommonResult) response).isBand == 0) { // 是否已经绑定了用户账号(0:否,1:是)
                        Intent intent = new Intent(LoginNewActivity.this, ThirdBandPhoneActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("head", head);
                        intent.putExtra("openid", openid);
                        startActivity(intent);
                    } else {
                        // 直接获取信息
                        user_get_pwd(((CommonResult) response).userId);
                    }
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                loadingDialog.dismiss();
            }
        });
        sendJsonRequest(userWxBandUserRequest);
    }

    //根据用户id获取密码
    private UserGetPwdRequest userGetPwdRequest;

    private void user_get_pwd(final int userId) {
        loadingDialog.show();
        if (userGetPwdRequest != null) {
            userGetPwdRequest.cancel();
        }
        UserGetPwdRequest.Input input = new UserGetPwdRequest.Input();
        input.userId = userId;
        input.convertJosn();

        userGetPwdRequest = new UserGetPwdRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((CommonResult) response).status == 1) {
                    M_Userinfo userInfo = new M_Userinfo();
                    userInfo.setUserId(userId);
                    userInfo.setPassword(((CommonResult) response).password);
                    UserManager.instance().saveUserinfo(userInfo);

                    AppUtils.initIm(((CommonResult) response).userId + "", Urls.APP_KEY);
                    loginHelper.login_Sample(userId + "", ((CommonResult) response).password, Urls.APP_KEY, new IWxCallback() {
                        @Override
                        public void onSuccess(Object... arg0) {
                            loadingDialog.dismiss();
                            SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());
                            sendBroadcast(new Intent(Constants.BROCAST_UPDATEMYINFO));
                            sendBroadcast(new Intent(Constants.BROCAST_LOGIN));
                            finish();
                        }

                        @Override
                        public void onProgress(int arg0) {

                        }

                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            loadingDialog.dismiss();
                            if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登录
                                Notification.showToastMsg(LoginNewActivity.this, "用户不存在");
                            } else {
                                Notification.showToastMsg(LoginNewActivity.this, errorMessage);
                            }
                        }
                    });
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                loadingDialog.dismiss();
            }
        });
        sendJsonRequest(userGetPwdRequest);
    }

}
