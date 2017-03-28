package com.zemult.merchant.activity.mine;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.zemult.merchant.activity.PasswordActivity;
import com.zemult.merchant.activity.RegisterActivity;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserIsRegisterRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
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
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.cb_look_pwd)
    CheckBox cbLookPwd;
    @Bind(R.id.btn_bangding)
    Button btnBangding;

    private boolean isWait = false;
    private Thread mThread = null;
    private String nickname, head;
    private LoginSampleHelper loginHelper;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0
                        && etPwd.getText().toString().length() > 0) {
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
        loginHelper = LoginSampleHelper.getInstance();

        lhTvTitle.setText("绑定手机号码");
        btnBangding.setEnabled(false);
        btnBangding.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        etPwd.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿

        cbLookPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    etPwd.setInputType(InputType.TYPE_CLASS_TEXT);
                else
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
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
                    etPhone.setError("请输入手机号码");
                } else {
                    if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                        etPhone.setError("请输入正确的手机号码");
                        return;
                    }

                    isRegister();
                }
                break;
            case R.id.btn_bangding:
                if(StringMatchUtils.isMobileNO(etPhone.getText().toString()))
                    ToastUtil.showMessage("请输入正确的手机号码");
                if (etPwd.getText().toString().length() < 6) {
                    ToastUtil.showMessage("密码格式错误");
                    return;
                }
                if (StringMatchUtils.isAllNum(etPwd.getText().toString())) {
                    ToastUtil.showMessage("密码格式错误");
                    return;
                }

                checkCode();
                break;
        }
    }

    private UserIsRegisterRequest request_user_is_register;
    private void isRegister() {
        try {
            if (request_user_is_register != null) {
                request_user_is_register.cancel();
            }
            UserIsRegisterRequest.Input input = new UserIsRegisterRequest.Input();
            input.phone = etPhone.getText().toString();
            input.convertJosn();

            request_user_is_register = new UserIsRegisterRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        getCode();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(request_user_is_register);
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
                        userRegister();
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


    //注册
    private UserRegisterRequest userRegisterRequest;
    private void userRegister() {
        try {
            if (userRegisterRequest != null) {
                userRegisterRequest.cancel();
            }
            UserRegisterRequest.Input input = new UserRegisterRequest.Input();
            input.phone = etPhone.getText().toString();
            input.password = DigestUtils.md5(etPwd.getText().toString()).toUpperCase();
            input.name = nickname;
            // TODO: 2017/3/28  头像 
            input.convertJosn();

            userRegisterRequest = new UserRegisterRequest(input, new ResponseListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print(error);
                }


                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        getUserLoginRequest();
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                }
            });
            sendJsonRequest(userRegisterRequest);
        } catch (Exception e) {
            Log.e("USER_REGISTER", e.toString());
        }


    }


    //用户登录
    private UserLoginRequest userLoginRequest;
    private void getUserLoginRequest() {
        loadingDialog.show();
        if (userLoginRequest != null) {
            userLoginRequest.cancel();
        }
        UserLoginRequest.Input input = new UserLoginRequest.Input();
        input.account = etPhone.getText().toString();
        input.password = DigestUtils.md5(etPwd.getText().toString()).toUpperCase();
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();

        userLoginRequest = new UserLoginRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    AppUtils.initIm(((APIM_UserLogin) response).userInfo.getUserId() + "", Urls.APP_KEY);
                    loginHelper.login_Sample(((APIM_UserLogin) response).userInfo.getUserId() + "", DigestUtils.md5(etPhone.getText().toString()).toUpperCase(), Urls.APP_KEY, new IWxCallback() {
                        @Override
                        public void onSuccess(Object... arg0) {
                            loadingDialog.dismiss();
                            ((APIM_UserLogin) response).userInfo.setPassword(DigestUtils.md5(etPhone.getText().toString()).toUpperCase());
                            UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
                            SlashHelper.setSettingString("last_login_phone", SlashHelper.userManager().getUserinfo().getPhoneNum());
                            setResult(RESULT_OK);
                            ToastUtil.showMessage("注册成功");
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
                                Notification.showToastMsg(ThirdBandPhoneActivity.this, "用户不存在");
                            } else {
                                Notification.showToastMsg(ThirdBandPhoneActivity.this, errorMessage);
                            }
                        }
                    });
//                        }

                } else {
                    loadingDialog.dismiss();
                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
                }

            }
        });
        sendJsonRequest(userLoginRequest);
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
