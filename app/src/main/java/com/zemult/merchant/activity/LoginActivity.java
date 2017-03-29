package com.zemult.merchant.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.ThirdBandPhoneActivity;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserWxBandUserRequest;
import com.zemult.merchant.app.BaseActivity;
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
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/2/24.
 */
public class LoginActivity extends BaseActivity {

    private static final int REQ_FIND_PWD = 0x110;
    private static final int REQ_THIRD_LOGIN = 0x120;
    @Bind(R.id.al_et_name)
    EditText etName;
    @Bind(R.id.al_et_pwd)
    EditText etPwd;
    @Bind(R.id.al_btn_login)
    Button btnLogin;
    @Bind(R.id.al_tv_forget)
    TextView tvForgetPwd;
    @Bind(R.id.al_tv_notnow)
    TextView tvNotNow;
    @Bind(R.id.user_icon)
    ImageView userIcon;
    @Bind(R.id.lh_btn_right)
    Button btnRight;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.al_tv_register)
    TextView alTvRegister;
    @Bind(R.id.iv_wx)
    ImageView ivWx;

    String strUserName, strPwd;
    UserLoginRequest user_login_request;


    private LoginSampleHelper loginHelper;
    private UMShareAPI umShareAPI;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_FIND_PWD) {
                etName.setText(data.getStringExtra("phone"));
                etPwd.setText(data.getStringExtra("password"));
                login();
            } else if (requestCode == REQ_THIRD_LOGIN) {
                finish();
            }
        }
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void init() {
        initViews();
        loginHelper = LoginSampleHelper.getInstance();
        umShareAPI = UMShareAPI.get(this);
    }

    private void initViews() {
        lhBtnBack.setVisibility(View.GONE);

        lhTvTitle.setText("登录");
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundResource(R.drawable.next_bg_btn_select);

        String strLoginPhone = SlashHelper.getSettingString("last_login_phone", "");
        if (!StringUtils.isBlank(strLoginPhone) && strLoginPhone.length() == 11) {
            imageManager.loadCircleHead(SlashHelper.getSettingString(strLoginPhone, ""), userIcon);
            etPwd.requestFocus();
        }
        etName.setText(strLoginPhone);
        etName.clearFocus();

        etName.addTextChangedListener(watcher);
        etName.addTextChangedListener(watcher2);
        etPwd.addTextChangedListener(watcher2);

    }


    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etName.getText().toString().length() == 11 && !SlashHelper.getSettingString(etName.getText().toString(), "").equals("")) {
                imageManager.loadCircleImage(SlashHelper.getSettingString(etName.getText().toString(), ""), userIcon);
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
                if (etName.getText().toString().length() > 0
                        && etPwd.getText().toString().length() > 0) {
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
                        LoginActivity.this.finish();
                        break;
                    case R.id.al_tv_forget:
                        Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                        startActivityForResult(intent, REQ_FIND_PWD);
                        break;
                }

            }
        }, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void login() {
        strUserName = etName.getText().toString();
        strPwd = etPwd.getText().toString();
        if (StringUtils.isBlank(strUserName)) {
            etName.setError("手机号不能为空");
        }
        if (StringUtils.isBlank(strPwd)) {
            etPwd.setError("密码不能为空");
        }
        if (!StringMatchUtils.isMobileNO(strUserName)) {
            ToastUtil.showMessage("请输入正确的手机号码");
            return;
        }
        if (!StringUtils.isBlank(strUserName) && !StringUtils.isBlank(strPwd))
            get_user_login_request();
    }

    //用户登录
    private void get_user_login_request() {
        loadingDialog.show();
        if (user_login_request != null) {
            user_login_request.cancel();
        }
        UserLoginRequest.Input input = new UserLoginRequest.Input();
        input.account = strUserName;
        input.password = DigestUtils.md5(strPwd).toUpperCase();
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();

        user_login_request = new UserLoginRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    if (null != getIntent().getStringExtra("actfrom") && "notification".equals(getIntent().getStringExtra("actfrom"))) {
                        Intent notificationIntent = new Intent(LoginActivity.this, SplashActivity.class);
                        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(notificationIntent);
                    } else {
//                        if(AppApplication.IMDISABLE){
//                            loadingDialog.dismiss();
//                            UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
//                            setResult(RESULT_OK);
//                            finish();
//                        }
//                        else{
                        AppUtils.initIm(((APIM_UserLogin) response).userInfo.getUserId() + "", Urls.APP_KEY);
                        loginHelper.login_Sample(((APIM_UserLogin) response).userInfo.getUserId() + "", DigestUtils.md5(strPwd).toUpperCase(), Urls.APP_KEY, new IWxCallback() {
                            @Override
                            public void onSuccess(Object... arg0) {
                                loadingDialog.dismiss();
                                ((APIM_UserLogin) response).userInfo.setPassword(DigestUtils.md5(strPwd).toUpperCase());
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
                                    Notification.showToastMsg(LoginActivity.this, "用户不存在");
                                } else {
                                    Notification.showToastMsg(LoginActivity.this, errorMessage);
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

    @OnClick({R.id.iv_wx, R.id.al_btn_login, R.id.al_tv_forget, R.id.al_tv_register, R.id.al_tv_notnow})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_wx:
                thirdLogin();
                break;
            case R.id.al_btn_login:
                login();
                break;
            case R.id.al_tv_forget:
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivityForResult(intent, REQ_FIND_PWD);
                break;
            case R.id.al_tv_notnow:
                LoginActivity.this.finish();
                break;
            case R.id.al_tv_register:
                IntentUtil.start_activity(LoginActivity.this, RegisterActivity.class);
                this.finish();
                break;
        }
    }

    private void thirdLogin() {
        umShareAPI.doOauthVerify(LoginActivity.this, SHARE_MEDIA.WEIXIN, doOauthVerifyListener);
    }

    private UMAuthListener doOauthVerifyListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            UMShareAPI.get(LoginActivity.this).getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, getPlatformInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };
    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            user_wx_band_user(data.get("openid"), data.get("nickname"), data.get("headimgurl"));
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
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
                        Intent intent = new Intent(LoginActivity.this, ThirdBandPhoneActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("head", head);
                        intent.putExtra("openid", openid);
                        startActivityForResult(intent, REQ_THIRD_LOGIN);
                    } else {
                        // 直接获取信息
                        AppUtils.initIm(((CommonResult) response).userId + "", Urls.APP_KEY);
                        M_Userinfo userInfo = new M_Userinfo();
                        userInfo.setUserId(((CommonResult) response).userId);
                        UserManager.instance().saveUserinfo(userInfo);
                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                        sendBroadcast(updateintent);
                        finish();
                    }
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                loadingDialog.dismiss();
            }
        });
        sendJsonRequest(userWxBandUserRequest);
    }

}
