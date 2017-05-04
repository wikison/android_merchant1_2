package com.zemult.merchant.activity.mine;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
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
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.PasswordActivity;
import com.zemult.merchant.activity.RegisterActivity;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserBandWxInfoPhoneRequest;
import com.zemult.merchant.aip.common.UserGetPwdRequest;
import com.zemult.merchant.aip.common.UserIsRegisterRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserLoginWxRequest;
import com.zemult.merchant.aip.common.UserRegisterRequest;
import com.zemult.merchant.aip.common.UserWxBandPhoneRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;


/**
 * 0008绑定手机号码
 *
 * @author djy
 * @time 2017/3/28 10:18
 */
public class ThirdBandPhoneActivity extends BaseActivity {
    private static final int WAIT = 0x001;

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_bangding)
    Button btnBangding;

    private boolean isWait = false;
    private Thread mThread = null;
    private String nickname, head, openid;
    private LoginSampleHelper loginHelper;

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

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0) {
                    btnBangding.setEnabled(true);
                    btnBangding.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnBangding.setEnabled(false);
                btnBangding.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_third_band_phone);
    }

    @Override
    public void init() {
        nickname = getIntent().getStringExtra("nickname");
        head = getIntent().getStringExtra("head");
        openid = getIntent().getStringExtra("openid");
        loginHelper = LoginSampleHelper.getInstance();

        lhTvTitle.setText("绑定手机号码");
        btnBangding.setEnabled(false);
        btnBangding.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿

        registerReceiver(new String[]{Constants.BROCAST_LOGIN});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_sendcode, R.id.btn_bangding})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_sendcode:
                String strPhone = etPhone.getText().toString();
                if (StringUtils.isBlank(strPhone)) {
                    ToastUtil.showMessage("请输入手机号码");
                } else {
                    if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                        ToastUtil.showMessage("请输入正确的手机号码");
                        return;
                    }
                    user_band_wx_info_phone();
                }
                break;
            case R.id.btn_bangding:
                if(!StringMatchUtils.isMobileNO(etPhone.getText().toString()))
                    ToastUtil.showMessage("请输入正确的手机号码");

                checkCode();
                break;
        }
    }

    private UserBandWxInfoPhoneRequest userBandWxInfoPhoneRequest;
        private void user_band_wx_info_phone() {
            showUncanclePd();
        try {
            if (userBandWxInfoPhoneRequest != null) {
                userBandWxInfoPhoneRequest.cancel();
            }
            UserBandWxInfoPhoneRequest.Input input = new UserBandWxInfoPhoneRequest.Input();
            input.phone = etPhone.getText().toString();
            input.convertJosn();

            userBandWxInfoPhoneRequest = new UserBandWxInfoPhoneRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        if(((CommonResult) response).isBand == 0)// 是否已经绑定了微信账号(0:否,1:是)
                            getCode();
                        else
                            ToastUtil.showMessage(((CommonResult) response).info);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userBandWxInfoPhoneRequest);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }

    }

    private CommonGetCodeRequest request_common_getcode;
    private void getCode() {
        try {
            if (request_common_getcode != null) {
                request_common_getcode.cancel();
            }
            CommonGetCodeRequest.Input input = new CommonGetCodeRequest.Input();
            input.phone = etPhone.getText().toString();
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
                        waitForClick();
                    }else
                        ToastUtil.showMessage(((CommonResult) response).info);
                }
            });
            sendJsonRequest(request_common_getcode);
        } catch (Exception e) {
            Log.e("COMMON_GETCODE", e.toString());
        }

    }

    private CommonCheckcodeRequest request_common_checkcode;
    private void checkCode() {//发送验证码校验
        try {
            if (request_common_checkcode != null) {
                request_common_checkcode.cancel();
            }
            final CommonCheckcodeRequest.Input input = new CommonCheckcodeRequest.Input();
            input.phone = etPhone.getText().toString();
            input.code = etCode.getText().toString();
            input.convertJosn();

            request_common_checkcode = new CommonCheckcodeRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        isRegister();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(request_common_checkcode);
        } catch (Exception e) {
            Log.e("COMMON_CHECKCODE", e.toString());
        }
    }

    private UserIsRegisterRequest request_user_is_register;
    private void isRegister() {
        try {
            if (request_user_is_register != null) {
                request_user_is_register.cancel();
            }
            final UserIsRegisterRequest.Input input = new UserIsRegisterRequest.Input();
            input.phone = etPhone.getText().toString();
            input.convertJosn();

            request_user_is_register = new UserIsRegisterRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    // 返回结果状态值,值为0或1.(0表示已经有该手机号；1表示新手机号码)
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        Intent intent = new Intent(ThirdBandPhoneActivity.this, ThirdBandPhoneSetPwdActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("head", head);
                        intent.putExtra("openid", openid);
                        intent.putExtra("phone", etPhone.getText().toString());
                        startActivity(intent);
                    } else {
                        user_wx_band_phone();
                    }
                }
            });
            sendJsonRequest(request_user_is_register);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }
    }
    //微信绑定手机号登陆(注册)
    private UserWxBandPhoneRequest wxBandPhoneRequest;
    private void user_wx_band_phone(){
        showUncanclePd();
        try {
            if (wxBandPhoneRequest != null) {
                wxBandPhoneRequest.cancel();
            }
            final UserWxBandPhoneRequest.Input input = new UserWxBandPhoneRequest.Input();
            input.openid = openid;
            input.phone = etPhone.getText().toString();
            input.convertJosn();

            wxBandPhoneRequest = new UserWxBandPhoneRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                    dismissPd();
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        user_get_pwd(((CommonResult) response).userId);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(wxBandPhoneRequest);
        } catch (Exception e) {
            Log.e("USER_REGISTER", e.toString());
        }
    }

    //根据用户id获取密码
    private UserGetPwdRequest userGetPwdRequest;
    private void user_get_pwd(final int userId) {
        showUncanclePd();
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
                    userInfo.setPhoneNum(etPhone.getText().toString());
                    UserManager.instance().saveUserinfo(userInfo);

                    AppUtils.initIm(((CommonResult) response).userId + "", Urls.APP_KEY);
                    loginHelper.login_Sample(userId+ "", ((CommonResult) response).password, Urls.APP_KEY, new IWxCallback() {
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
                                Notification.showToastMsg(ThirdBandPhoneActivity.this, "用户不存在");
                            } else {
                                Notification.showToastMsg(ThirdBandPhoneActivity.this, errorMessage);
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
                    tvSendcode.setTextColor(0xffe6bb7c);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isWait = false;
    }
}
