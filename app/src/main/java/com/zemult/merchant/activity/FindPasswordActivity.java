package com.zemult.merchant.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.login.YWLoginState;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.ChangePassSucActivity;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserFindpwdRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.SlashHelper;
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
public class FindPasswordActivity extends BaseActivity {
    public static final String REQUEST_TAG_GET_CODE = "1";
    public static final String REQUEST_TAG_FINDPWD = "2";
    private static final int WAIT = 0x001;
    private static String LOG_TAG = "FindPasswordActivity";
    //    RelativeLayout relativeLayoutHead;
//    Button btnBack;
//    TextView tvTitle;
    String strPhone, strCode, strPwd, strPwd2;
    CommonGetCodeRequest request_common_getcode;
    UserFindpwdRequest userFindpwdRequest;
    UserLoginRequest userLoginRequest;
    @Bind(R.id.afp_et_phone)
    EditText etPhone;
    @Bind(R.id.ar_et_code)
    EditText etCode;
    @Bind(R.id.afp_et_pwd)
    EditText etPwd;
    @Bind(R.id.afp_et_pwd_again)
    EditText etPwd2;
    @Bind(R.id.afp_btn_submit)
    Button btnSubmit;
    @Bind(R.id.tv_sendcode)
    TextView tvSendcode;
    private boolean isWait = false;
    private Thread mThread = null;

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_pwd);

    }

    @Override
    public void init() {
        initViews();
        Intent intent = getIntent();
        strPhone = intent.getStringExtra("RegisterPhone");
        etPhone.setText(strPhone);
        SlashHelper.setSettingBoolean("isChangingPassWord", true);
    }


    public void initViews() {
        lhTvTitle.setText(getResources().getString(R.string.title_find_password));
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }


    @OnClick(R.id.tv_sendcode)
    public void onBtnCodeClick() {
        String rUrl = "";
        strPhone = etPhone.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            etPhone.setError("请输入手机号码");
        } else {
            getCode();
        }
    }

    @OnClick(R.id.afp_btn_submit)
    public void onBtnSubmitClick(View v) {
        boolean isPwdEqual = false;
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();
        strPwd = etPwd.getText().toString();
        strPwd2 = etPwd2.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            etPhone.setError("手机号不能为空");
        }
        if (StringUtils.isBlank(strCode)) {
            etCode.setError("验证码不能为空");
        }
        if (StringUtils.isBlank(strPwd)) {
            etPwd.setError("密码不能为空");
        }
        if (StringUtils.isBlank(strPwd2)) {
            etPwd2.setError("密码不能为空");
        }
        if (!StringUtils.isEquals(strPwd, strPwd2)) {
            isPwdEqual = false;
            etPwd2.setError("密码不同, 请重新设置");
        } else {
            isPwdEqual = true;
        }
        if (!StringUtils.isBlank(strPhone) && !StringUtils.isBlank(strCode) && !StringUtils.isBlank(strPwd) && !StringUtils.isBlank(strPwd2) && isPwdEqual == true) {
            find_password();
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
                        tvSendcode.setText("重新发送(" + 60 + "s)");
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

    // 倒计时60s
    private void waitForClick() {
        isWait = true;
        final Handler handler = new Handler() {
            int i = 60;

            public void handleMessage(Message msg) {
                i--;
                tvSendcode.setText("重新发送(" + i + "s)");
                if (i == 0) {
                    isWait = false;
                    tvSendcode.setText("重新发送");
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

    //找回密码
    private void find_password() {
        loadingDialog.show();
        if (userFindpwdRequest != null) {
            userFindpwdRequest.cancel();
        }
        UserFindpwdRequest.Input input = new UserFindpwdRequest.Input();

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
                    Intent intent = new Intent(FindPasswordActivity.this, ChangePassSucActivity.class);
                    intent.putExtra("password", "forget");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);

                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(userFindpwdRequest);
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
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((APIM_UserLogin) response).status == 1) {
                    ((APIM_UserLogin) response).userInfo.setPassword(DigestUtils.md5(strPwd).toUpperCase());
                    UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
                    setResult(RESULT_OK);
                    finish();
                } else {
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
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
