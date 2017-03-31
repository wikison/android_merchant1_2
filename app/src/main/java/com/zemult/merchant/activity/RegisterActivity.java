package com.zemult.merchant.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserIsRegisterRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;


/**
 * Created by Wikison on 2016/2/25.
 */
public class RegisterActivity extends BaseActivity {
    private static final int WAIT = 0x001;
    private static String LOG_TAG = "RegisterActivity";
    String strPhone, strCode, strRequestCode, strPwd;
    Request request_common_getcode, request_common_checkcode, request_user_is_register;

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
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
    @Bind(R.id.btn_next)
    Button btnNext;
    @Bind(R.id.cb_agree)
    CheckBox cbAgree;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;
    @Bind(R.id.tv_not_now)
    TextView tvNotNow;

    private boolean isWait = false;
    private Thread mThread = null;

    private LoginSampleHelper loginHelper;

    UserRegisterRequest userRegisterRequest;
    UserLoginRequest userLoginRequest;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void init() {
        initData();
        initViews();
        initListener();
    }

    private void initData() {
        loginHelper = LoginSampleHelper.getInstance();
    }


    public void initViews() {
        lhTvTitle.setText(getResources().getString(R.string.btn_register));
        cbAgree.setChecked(true);

        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        etPwd.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etCode.getText().toString().length() > 0
                    && etPhone.getText().toString().length() == 11
                    && etPwd.getText().toString().length() >= 6) {
                btnNext.setEnabled(true);
                btnNext.setBackgroundResource(R.drawable.common_selector_btn);

            } else {
                btnNext.setEnabled(false);
                btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void initListener() {
        cbLookPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());

                etPwd.setSelection(etPwd.length());
            }
        });
    }

    private void onBtnCodeClick() {
        strPhone = etPhone.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            ToastUtil.showMessage("请输入手机号码");
        } else {
            if (!StringMatchUtils.isMobileNO(etPhone.getText().toString())) {
                ToastUtil.showMessage("请输入正确的手机号码");
                return;
            }

            isRegister();
        }
    }

    private void onBtnRegisterClick() {
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();
        strPwd = etPwd.getText().toString();

        if (cbAgree.isChecked()) {
            if (StringMatchUtils.isAllNum(strPwd)) {
                ToastUtil.showMessage("密码格式错误");
                return;
            }
            //验证码校验
            checkCode();
        } else {
            ToastUtil.showMessage("请勾选同意本平台协议");
        }

    }

    private void isRegister() {
        try {
            if (request_user_is_register != null) {
                request_user_is_register.cancel();
            }
            UserIsRegisterRequest.Input input = new UserIsRegisterRequest.Input();
            input.phone = strPhone;
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

    private void checkCode() {//发送验证码校验
        try {
            if (request_common_checkcode != null) {
                request_common_checkcode.cancel();
            }
            final CommonCheckcodeRequest.Input input = new CommonCheckcodeRequest.Input();
            input.phone = strPhone;
            input.code = strCode;
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
    private void userRegister() {
        try {
            if (userRegisterRequest != null) {
                userRegisterRequest.cancel();
            }
            UserRegisterRequest.Input input = new UserRegisterRequest.Input();
            input.phone = strPhone;
            input.password = DigestUtils.md5(strPwd).toUpperCase();
            input.name = "nc" + strPhone;
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
    private void getUserLoginRequest() {
        loadingDialog.show();
        if (userLoginRequest != null) {
            userLoginRequest.cancel();
        }
        UserLoginRequest.Input input = new UserLoginRequest.Input();
        input.account = strPhone;
        input.password = DigestUtils.md5(strPwd).toUpperCase();
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
                    loginHelper.login_Sample(((APIM_UserLogin) response).userInfo.getUserId() + "", DigestUtils.md5(strPwd).toUpperCase(), Urls.APP_KEY, new IWxCallback() {
                        @Override
                        public void onSuccess(Object... arg0) {
                            loadingDialog.dismiss();
                            ((APIM_UserLogin) response).userInfo.setPassword(DigestUtils.md5(strPwd).toUpperCase());
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
                                ToastUtil.showMessage("用户不存在");
                            } else {
                                ToastUtil.showMessage(errorMessage);
                            }
                        }
                    });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x110)
                finish();
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_sendcode, R.id.btn_next, R.id.tv_protocol, R.id.tv_not_now})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_sendcode:
                onBtnCodeClick();
                break;
            case R.id.btn_next:
                onBtnRegisterClick();
                break;
            case R.id.tv_protocol:
                IntentUtil.start_activity(RegisterActivity.this, BaseWebViewActivity.class, new Pair<String, String>("titlename", getString(R.string.app_name) + "服务协议"), new Pair<String, String>("url", Constants.PROTOCOL_REGISTER));
                break;
            case R.id.tv_not_now:
                onBackPressed();
                break;
        }
    }

}

