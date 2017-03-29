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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.login.YWLoginState;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserFindpwdRequest;
import com.zemult.merchant.aip.common.UserIsRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/2/24.
 */
public class FindPasswordActivity extends BaseActivity {
    private static final int WAIT = 0x001;
    private static final int REQ_RESET_PWD = 0x120;
    private static String LOG_TAG = "FindPasswordActivity";

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

    CommonGetCodeRequest request_common_getcode;
    CommonCheckcodeRequest request_common_checkcode;
    UserFindpwdRequest userFindpwdRequest;

    private String strPhone, strCode, strPwd;
    private boolean isWait = false;
    private Thread mThread = null;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_pwd);

    }

    @Override
    public void init() {
        initViews();
        initData();
        initListener();

    }

    private void initData() {
        strPhone = getIntent().getStringExtra("RegisterPhone");
        etPhone.setText(strPhone);
        SlashHelper.setSettingBoolean("isChangingPassWord", true);
    }

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


    public void initViews() {
        lhTvTitle.setText(getResources().getString(R.string.title_find_password));

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

    public void onBtnCodeClick() {
        strPhone = etPhone.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            ToastUtil.showMessage("请输入手机号码");
            return;
        }
        if (!StringMatchUtils.isMobileNO(strPhone)) {
            ToastUtil.showMessage("请输入正确的手机号码");
            return;
        }
        isRegister();
    }

    public void onBtnSubmitClick() {
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();
        strPwd = etPwd.getText().toString();

        if (StringMatchUtils.isAllNum(strPwd)) {
            ToastUtil.showMessage("密码格式错误");
            return;
        }
        //验证码校验
        checkCode();
    }

    private UserIsRegisterRequest request_user_is_register;

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
                    // 返回结果状态值,值为0或1.(0表示不能注册--已经有该手机号；1表示可以注册)
                    if (status == 1)
                        ToastUtil.showMessage("该手机号码还未注册，赶快去注册吧！");
                    else
                        getCode();
                }
            });
            sendJsonRequest(request_user_is_register);
        } catch (Exception e) {
            Log.e("USER_IS_REGISTER", e.toString());
        }

    }

    //获取验证码
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

            ;
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


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_sendcode, R.id.btn_next})
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
                onBtnSubmitClick();
                break;
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
                        findPassword();
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

    //找回密码
    private void findPassword() {
        loadingDialog.show();
        if (userFindpwdRequest != null) {
            userFindpwdRequest.cancel();
        }
        final UserFindpwdRequest.Input input = new UserFindpwdRequest.Input();

        input.phone = strPhone;
        input.code = strCode;
        input.password = DigestUtils.md5(strPwd).toUpperCase();
        input.convertJosn();

        userFindpwdRequest = new UserFindpwdRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("密码找回成功");
                    SlashHelper.userManager().saveUserinfo(null);
                    LoginSampleHelper.getInstance().setAutoLoginState(YWLoginState.idle);

                    Intent intent = new Intent();
                    intent.putExtra("phone", strPhone);
                    intent.putExtra("password", strPwd);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(userFindpwdRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQ_RESET_PWD) {
                setResult(RESULT_OK, data);
                onBackPressed();
            }
        }
    }

}
