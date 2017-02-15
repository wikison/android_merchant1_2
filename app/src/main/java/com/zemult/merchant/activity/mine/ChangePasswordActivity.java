package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.FindPasswordActivity;
import com.zemult.merchant.aip.common.UserEditpwdRequest;
import com.zemult.merchant.aip.common.UserLoginRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

/**
 * 修改密码
 */
public class ChangePasswordActivity extends BaseActivity {

    private static final int CHANGE_PWD_SUCCESS_REQ = 0x110;
    private static final int FORGET_PWD_REQ = 0x120;
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
    @Bind(R.id.et_oldpassword)
    EditText etOldpassword;
    @Bind(R.id.et_newpassword)
    EditText etNewpassword;
    @Bind(R.id.et_renewpassword)
    EditText etRenewpassword;
    @Bind(R.id.submit)
    Button submit;
    @Bind(R.id.tv_forgetpassword)
    TextView tvForgetpassword;
    private UserEditpwdRequest editpwdRequest;
    private UserLoginRequest user_login_request;
    private Context mContext;

    private String strOldPwd, strNewPwd, strNewPwd2;
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() >= 6) {
                if (etOldpassword.getText().toString().length() >= 6
                        && etNewpassword.getText().toString().length() >= 6
                        && etRenewpassword.getText().toString().length() >= 6) {
                    submit.setEnabled(true);
                    submit.setBackgroundResource(R.drawable.common_selector_btn);
                }

            } else {
                submit.setEnabled(false);
                submit.setBackgroundResource(R.drawable.next_bg_btn_select);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.change_password);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("修改密码");
        tvForgetpassword.setText(Html.fromHtml("<u>" + "忘记密码" + "</u>"));
        SlashHelper.setSettingBoolean("isChangingPassWord", true);
        submit.setEnabled(false);
        submit.setBackgroundResource(R.drawable.next_bg_btn_select);
        etOldpassword.addTextChangedListener(watcher);
        etNewpassword.addTextChangedListener(watcher);
        etRenewpassword.addTextChangedListener(watcher);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.submit, R.id.tv_forgetpassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_forgetpassword:
                Intent intent = new Intent(mContext, FindPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                break;
            case R.id.submit:
                doSubmit();
                break;
        }
    }

    private void doSubmit() {
        strOldPwd = etOldpassword.getText().toString();
        strNewPwd = etNewpassword.getText().toString();
        strNewPwd2 = etRenewpassword.getText().toString();
        if (!StringUtils.isEquals(strNewPwd, strNewPwd2)) {
            etRenewpassword.setError("密码不同, 请重新设置");
            return;
        } else {
            boolean b = StringMatchUtils.isAllNum(strNewPwd);
            if (b) {
                etRenewpassword.setError("密码不能纯数字");
                return;
            } else {
                //先验证原密码
                get_user_login_request();
            }
        }
    }

    //用户登录
    private void get_user_login_request() {
        loadingDialog.show();
        if (user_login_request != null) {
            user_login_request.cancel();
        }
        UserLoginRequest.Input input = new UserLoginRequest.Input();
        input.account = SlashHelper.userManager().getUserinfo().getAccount();
        input.password = DigestUtils.md5(strOldPwd).toUpperCase();
        input.device_token = SlashHelper.deviceManager().getUmengDeviceToken();
        input.convertJosn();

        user_login_request = new UserLoginRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((APIM_UserLogin) response).status == 1) {
                    user_editpwd();
                } else {
                    etOldpassword.setError(((APIM_UserLogin) response).info);
                }

            }
        });
        sendJsonRequest(user_login_request);
    }

    private void user_editpwd() {
        showPd();

        if (editpwdRequest != null) {
            editpwdRequest.cancel();
        }
        UserEditpwdRequest.Input input = new UserEditpwdRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.password = DigestUtils.md5(etOldpassword.getText().toString()).toUpperCase();
        input.newpassword = DigestUtils.md5(etNewpassword.getText().toString()).toUpperCase();

        input.convertJosn();

        editpwdRequest = new UserEditpwdRequest(input, new ResponseListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                System.out.print(error);
            }


            @Override
            public void onResponse(Object response) {
                dismissPd();
                int status = ((CommonResult) response).status;
                if (status == 1) {
                    ToastUtil.showMessage("密码修改成功, 请重新登录");
                    SlashHelper.userManager().saveUserinfo(null);
                    Intent intent = new Intent(mContext, ChangePassSucActivity.class);
                    intent.putExtra("password", "change");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(editpwdRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FORGET_PWD_REQ:
                case CHANGE_PWD_SUCCESS_REQ:
                    SlashHelper.setSettingBoolean("isChangingPassWord", false);
                    onBackPressed();
                    break;
            }

        }
    }
}
