package com.zemult.merchant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.login.YWLoginCode;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.LoginSampleHelper;
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
 * Created by Wikison on 2016/2/24.
 */
public class LoginActivity extends BaseActivity {

    private static final int REQ_FIND_PWD = 0x110;
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

    String strUserName, strPwd;
    UserLoginRequest user_login_request;

    private LoginSampleHelper loginHelper;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQ_FIND_PWD){
                etName.setText(data.getStringExtra("phone"));
                etPwd.setText(data.getStringExtra("password"));
                login();
            }
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void init() {
        initViews();
        loginHelper = LoginSampleHelper.getInstance();

    }

    private void initViews() {
        lhBtnBack.setVisibility(View.GONE);
        tvForgetPwd.setMovementMethod(LinkMovementMethod.getInstance());
        tvForgetPwd.setText(setLinkText(getResources().getString(R.string.txt_login_forget_password)));
        tvNotNow.setMovementMethod(LinkMovementMethod.getInstance());
        tvNotNow.setText(setLinkText(getResources().getString(R.string.txt_register_notnow)));

        btnRight.setText(getResources().getString(R.string.btn_register));
        btnRight.setVisibility(View.VISIBLE);
        btnRight.setTextColor(getResources().getColor(R.color.white));
        btnRight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        lhTvTitle.setText("登录");
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundResource(R.drawable.next_bg_btn_select);

        String strLoginPhone = SlashHelper.getSettingString("last_login_phone", "");
        if (!StringUtils.isBlank(strLoginPhone)&& strLoginPhone.length()==11){
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
                        Intent intent= new Intent(LoginActivity.this, FindPasswordActivity.class);
                        startActivityForResult(intent, REQ_FIND_PWD);
                        break;
                }

            }
        }, 0, spanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    @OnClick(R.id.lh_btn_right)
    public void onRegisterClick() {
        IntentUtil.start_activity(LoginActivity.this, RegisterActivity.class);
        this.finish();
    }

    @OnClick(R.id.al_btn_login)
    public void onBtnLoginClick() {
        login();
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
//                        }
                    }
                } else {
                    loadingDialog.dismiss();
                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
                }

            }
        });
        sendJsonRequest(user_login_request);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
