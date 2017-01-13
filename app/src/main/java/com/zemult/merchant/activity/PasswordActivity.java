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

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.aip.common.UserRegisterRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
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
public class PasswordActivity extends BaseActivity {
    private static String LOG_TAG = "PasswordActivity";

    RelativeLayout relativeLayoutHead;
    Button btnBack;
    TextView tvTitle;

    String strPhone, strPwd, strPwd2;
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

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etPwd.getText().toString().length() > 0
                        && etPhone.getText().toString().length() > 0
                        && etPwdAgain.getText().toString().length() > 0) {
                    btnRegist.setEnabled(true);
                    btnRegist.setBackgroundResource(R.drawable.commit);
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
    }

    @OnClick(R.id.btn_regist)
    public void onBtnRegisterClick(View v) {
        strPwd = etPwd.getText().toString();
        strPwd2 = etPwdAgain.getText().toString();

        if (!StringUtils.isEquals(strPwd, strPwd2)) {
            etPwdAgain.setError("密码不同, 请重新设置");
        } else {
            if (strPwd.length() < 6) {
                etPwdAgain.setError("密码长度设置不正确");
            } else if (StringMatchUtils.isAllNum(strPwd)) {
                etPwdAgain.setError("密码不能全为数字");
            } else {
                userRegister();
            }


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
//                        getUserLoginRequest();
                        setResult(RESULT_OK);
                        ToastUtil.showMessage("注册成功");
                        finish();
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
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
