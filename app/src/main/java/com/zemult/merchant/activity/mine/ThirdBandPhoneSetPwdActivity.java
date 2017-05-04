package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.User2SaleUserLoginRequest;
import com.zemult.merchant.aip.common.UserLoginWxRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import zema.volley.network.ResponseListener;

public class ThirdBandPhoneSetPwdActivity extends BaseActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.cb_look_pwd)
    CheckBox cbLookPwd;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_be_manager)
    Button btnBeManager;

    private String nickname, head, openid, phone;
    private LoginSampleHelper loginHelper;
    private int loginType;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_third_band_phone_set_pwd);
    }

    @Override
    public void init() {
        nickname = getIntent().getStringExtra("nickname");
        head = getIntent().getStringExtra("head");
        openid = getIntent().getStringExtra("openid");
        phone = getIntent().getStringExtra("phone");
        loginHelper = LoginSampleHelper.getInstance();
        lhTvTitle.setText("绑定手机号码");

        btnLogin.setEnabled(false);
        btnBeManager.setEnabled(false);
        etPwd.addTextChangedListener(watcher);
        cbLookPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                etPwd.setSelection(etPwd.length());
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 5) {
                btnLogin.setEnabled(true);
                btnLogin.setBackgroundResource(R.drawable.common_selector_btn);
                btnBeManager.setEnabled(true);
                btnBeManager.setBackgroundResource(R.drawable.common_selector_btn2);
                btnBeManager.setTextColor(0xffd6a864);
            }else {
                btnLogin.setEnabled(false);
                btnLogin.setBackgroundResource(R.drawable.next_bg_btn_select);
                btnBeManager.setEnabled(false);
                btnBeManager.setBackgroundResource(R.drawable.next_bg_btn_select2);
                btnBeManager.setTextColor(0xff999999);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_login, R.id.btn_be_manager})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                check(1);
                break;
            case R.id.btn_be_manager:
                check(2);
                break;
        }
    }


    private void check(int type){
        loginType = type;
        if (etPwd.getText().toString().length() < 6) {
            ToastUtil.showMessage("密码格式错误");
            return;
        }
        if (StringMatchUtils.isAllNum(etPwd.getText().toString())) {
            ToastUtil.showMessage("密码格式错误");
            return;
        }
        user_login_wx();
    }

    //微信授权并绑定手机号登陆(注册)
    private UserLoginWxRequest userLoginWxRequest;
    private void user_login_wx() {
        showUncanclePd();
        try {
            if (userLoginWxRequest != null) {
                userLoginWxRequest.cancel();
            }
            final UserLoginWxRequest.Input input = new UserLoginWxRequest.Input();
            input.phone = phone;
            input.password = DigestUtils.md5(etPwd.getText().toString()).toUpperCase();
            input.name = nickname;
            input.pic = head;
            input.openid = openid;
            input.convertJosn();

            userLoginWxRequest = new UserLoginWxRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    final int userId = ((CommonResult) response).userId;
                    String password = DigestUtils.md5(etPwd.getText().toString()).toUpperCase();

                    if (status == 1) {
                        M_Userinfo userInfo = new M_Userinfo();
                        userInfo.setUserId(userId);
                        userInfo.setPassword(password);
                        userInfo.setPhoneNum(phone);
                        UserManager.instance().saveUserinfo(userInfo);

                        AppUtils.initIm(userId + "", Urls.APP_KEY);
                        loginHelper.login_Sample(userId+ "", password, Urls.APP_KEY, new IWxCallback() {
                            @Override
                            public void onSuccess(Object... arg0) {
                                loadingDialog.dismiss();
                                SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());
                                sendBroadcast(new Intent(Constants.BROCAST_UPDATEMYINFO));
                                sendBroadcast(new Intent(Constants.BROCAST_LOGIN));

                                if(loginType == 2)
                                    user2SaleUserLogin(userId);
                                else
                                    finish();
                            }

                            @Override
                            public void onProgress(int arg0) {

                            }

                            @Override
                            public void onError(int errorCode, String errorMessage) {
                                loadingDialog.dismiss();
                                if (errorCode == YWLoginCode.LOGON_FAIL_INVALIDUSER) { //若用户不存在，则提示使用游客方式登录
                                    Notification.showToastMsg(ThirdBandPhoneSetPwdActivity.this, "用户不存在");
                                } else {
                                    Notification.showToastMsg(ThirdBandPhoneSetPwdActivity.this, errorMessage);
                                }
                            }
                        });
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userLoginWxRequest);
        } catch (Exception e) {
            Log.e("USER_REGISTER", e.toString());
        }
    }


    private User2SaleUserLoginRequest user2SaleUserLoginRequest;
    //一键注册成为服务管家
    private void user2SaleUserLogin(int userId) {
        if (user2SaleUserLoginRequest != null) {
            user2SaleUserLoginRequest.cancel();
        }
        User2SaleUserLoginRequest.Input input = new User2SaleUserLoginRequest.Input();
        input.userId = userId;
        input.convertJosn();
        user2SaleUserLoginRequest = new User2SaleUserLoginRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                }else{
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                finish();
            }
        });
        sendJsonRequest(user2SaleUserLoginRequest);
    }
}
