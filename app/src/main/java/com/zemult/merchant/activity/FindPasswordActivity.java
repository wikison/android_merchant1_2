package com.zemult.merchant.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.zemult.merchant.aip.common.CommonCheckcodeRequest;
import com.zemult.merchant.aip.common.CommonGetCodeRequest;
import com.zemult.merchant.aip.common.UserFindpwdRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
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
public class FindPasswordActivity extends BaseActivity {
    private static final int WAIT = 0x001;
    private static final int REQ_RESET_PWD = 0x120;
    private static String LOG_TAG = "FindPasswordActivity";
    private String strPhone, strCode;
    CommonGetCodeRequest request_common_getcode;
    @Bind(R.id.afp_et_phone)
    EditText etPhone;
    @Bind(R.id.ar_et_code)
    EditText etCode;
    @Bind(R.id.afp_btn_submit)
    Button btnNext;
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

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etCode.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0) {
                    btnNext.setEnabled(true);
                    btnNext.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnNext.setEnabled(false);
                btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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

        btnNext.setEnabled(false);
        btnNext.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPhone.addTextChangedListener(watcher);
        etCode.addTextChangedListener(watcher);
        tvSendcode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG ); //下划线
        tvSendcode.getPaint().setAntiAlias(true);//抗锯齿
    }


    @OnClick(R.id.tv_sendcode)
    public void onBtnCodeClick() {
        String rUrl = "";
        strPhone = etPhone.getText().toString();
        if (StringUtils.isBlank(strPhone)) {
            etPhone.setError("请输入手机号码");
            return;
        }
        if (!StringMatchUtils.isMobileNO(strPhone)) {
            etPhone.setError("请输入正确的手机号码");
            return;
        }
        getCode();
    }

    @OnClick(R.id.afp_btn_submit)
    public void onBtnSubmitClick(View v) {
        strPhone = etPhone.getText().toString();
        strCode = etCode.getText().toString();

        //验证码校验
        checkCode();
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



//    //用户登录
//    private void getUserLoginRequest() {
//        loadingDialog.show();
//        if (userLoginRequest != null) {
//            userLoginRequest.cancel();
//        }
//        UserLoginRequest.Input input = new UserLoginRequest.Input();
//        input.account = strPhone;
//        input.password = DigestUtils.md5(strPwd).toUpperCase();
//        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
//        input.convertJosn();
//
//        userLoginRequest = new UserLoginRequest(input, new ResponseListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                loadingDialog.dismiss();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                loadingDialog.dismiss();
//                if (((APIM_UserLogin) response).status == 1) {
//                    ((APIM_UserLogin) response).userInfo.setPassword(DigestUtils.md5(strPwd).toUpperCase());
//                    UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
//                    setResult(RESULT_OK);
//                    finish();
//                } else {
//                    ToastUtil.showMessage(((APIM_UserLogin) response).info);
//                }
//
//            }
//        });
//        sendJsonRequest(userLoginRequest);
//    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    CommonCheckcodeRequest request_common_checkcode;
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
                        Intent intent = new Intent(FindPasswordActivity.this, FindPassword2Activity.class);
                        intent.putExtra("strPhone", strPhone);
                        intent.putExtra("strCode", strCode);
                        startActivityForResult(intent, REQ_RESET_PWD);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK ){
            if(requestCode == REQ_RESET_PWD){
                setResult(RESULT_OK, data);
                onBackPressed();
            }
        }
    }
}
