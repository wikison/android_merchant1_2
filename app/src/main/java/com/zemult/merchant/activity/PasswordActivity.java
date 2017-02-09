package com.zemult.merchant.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.EditFilter;
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
 * Created by Wikison on 2016/2/24.
 */
//设置登录密码
public class PasswordActivity extends BaseActivity {
    private static String LOG_TAG = "PasswordActivity";

    RelativeLayout relativeLayoutHead;
    Button btnBack;
    TextView tvTitle;
    private LoginSampleHelper loginHelper;

    String strPhone, strPwd, strPwd2, name;
    UserRegisterRequest userRegisterRequest;
    UserLoginRequest userLoginRequest;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.et_pwd_again)
    EditText etPwdAgain;
    @Bind(R.id.btn_regist)
    Button btnRegist;
    @Bind(R.id.et_nickname)
    EditText etNickname;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etPwd.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0
                        && etPwdAgain.getText().toString().length() > 0
                        && etNickname.getText().toString().length() > 0
                        ) {
                    btnRegist.setEnabled(true);
                    btnRegist.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnRegist.setEnabled(false);
                btnRegist.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_setpwd);

    }

    @Override
    public void init() {
        Intent intent = getIntent();
        strPhone = intent.getStringExtra("RegisterPhone");
        etPhone.setText(strPhone);
        loginHelper = LoginSampleHelper.getInstance();
        EditFilter.WordFilter(etNickname, 11);
        initViews();
    }

    public void initViews() {
        relativeLayoutHead = (RelativeLayout) findViewById(R.id.include_layout_head);
        relativeLayoutHead.setBackgroundColor(getResources().getColor(R.color.bg_head));

        btnBack = (Button) relativeLayoutHead.findViewById(R.id.lh_btn_back);
        btnBack.setBackgroundResource(R.drawable.btn_back);
        tvTitle = (TextView) relativeLayoutHead.findViewById(R.id.lh_tv_title);
        tvTitle.setText(getResources().getString(R.string.title_set_password));
        tvTitle.setTextColor(getResources().getColor(R.color.white));
        tvTitle.setTextSize(18);
        tvTitle.setVisibility(View.VISIBLE);

        btnRegist.setEnabled(false);
        btnRegist.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etPwd.addTextChangedListener(watcher);
        etPwdAgain.addTextChangedListener(watcher);
        etNickname.addTextChangedListener(watcher);
    }

    @OnClick(R.id.btn_regist)
    public void onBtnRegisterClick(View v) {
        strPwd = etPwd.getText().toString();
        strPwd2 = etPwdAgain.getText().toString();
        name = etNickname.getText().toString();

        if (!StringUtils.isEquals(strPwd, strPwd2)) {
            etPwdAgain.setError("密码不同, 请重新设置");
        } else {
            if (strPwd.length() < 6) {
                etPwdAgain.setError("密码长度设置不正确");
                return;
            }
            if (StringMatchUtils.isAllNum(strPwd)) {
                etPwdAgain.setError("密码不能全为数字");
                return;
            }
                userRegister();

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
            input.name = name;
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
                    AppUtils.initIm(((APIM_UserLogin) response).userInfo.getUserId() + "", LoginSampleHelper.APP_KEY);
                    loginHelper.login_Sample(((APIM_UserLogin) response).userInfo.getUserId() + "", DigestUtils.md5(strPwd).toUpperCase(), LoginSampleHelper.APP_KEY, new IWxCallback() {
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
                                Notification.showToastMsg(PasswordActivity.this, "用户不存在");
                            } else {
                                Notification.showToastMsg(PasswordActivity.this, errorMessage);
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


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
