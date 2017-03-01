package com.zemult.merchant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.mobileim.login.YWLoginState;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.UserFindpwdRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class FindPassword2Activity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.et_pwd_again)
    EditText etPwdAgain;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    private String strPhone, strCode, strPwd, strPwd2;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etPwd.getText().toString().length() > 0
                        && etPwdAgain.getText().toString().length() > 0) {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                btnSubmit.setEnabled(false);
                btnSubmit.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_password2);
    }

    @Override
    public void init() {
        lhTvTitle.setText("忘记密码");
        strPhone = getIntent().getStringExtra("strPhone");
        strCode = getIntent().getStringExtra("strCode");

        btnSubmit.setEnabled(false);
        btnSubmit.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPwd.addTextChangedListener(watcher);
        etPwdAgain.addTextChangedListener(watcher);
    }

    @OnClick(R.id.btn_submit)
    public void onBtnSunbmitClick(View v) {
        strPwd = etPwd.getText().toString();
        strPwd2 = etPwdAgain.getText().toString();

        if (!StringUtils.isEquals(strPwd, strPwd2)) {
            etPwdAgain.setError("密码不同, 请重新设置");
        } else {
            if (strPwd.length() < 6) {
                etPwdAgain.setError("密码格式错误");
                return;
            }
            if (StringMatchUtils.isAllNum(strPwd)) {
                etPwdAgain.setError("密码格式错误");
                return;
            }
            find_password();
        }
    }


    private UserFindpwdRequest userFindpwdRequest;

    //找回密码
    private void find_password() {
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
//                    Intent intent = new Intent(FindPasswordActivity.this, ChangePassSucActivity.class);
//                    intent.putExtra("password", "forget");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }

            }
        });
        sendJsonRequest(userFindpwdRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
