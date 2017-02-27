package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.UserCheckpaypwdRequest;
import com.zemult.merchant.aip.common.UserEditpaypwdRequest;
import com.zemult.merchant.aip.common.UserSetpaypwdRequest;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.password.GridPasswordView;

import cn.trinea.android.common.util.DigestUtils;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class ValidatePayPwdActivity extends MAppCompatActivity implements View.OnClickListener {

    public static final String OPERATION = "operation";
    public static final String PASSWORD = "password";
    public static final int REQUEST_OLD_PASSWORD = 0;
    public static final int REQUEST_NEW_PASSWORD = 1;
    public static final int REQUEST_CONFIRM_PASSWORD = 2;
    public static final String TITLE_TV_TEXT = "title";
    public static final String TIP_TV_TEXT = "tip";
    public static final String CONTENT_TV_TEXT = "content";
    public static final String CONFIRM_BTN_TEXT = "confirm";
    public static final String SMS_CODE = "smscode";
    public static final String OLD_PASSWORD="oldPassWord";
    private GridPasswordView passwordView;
    private TextView tipTv;
    private TextView titleTv;
    private Button confirmBtn,lh_btn_back;
    private int operation = 0;
    TextView lh_tv_title;
    LinearLayout  ll_back;
    UserSetpaypwdRequest userSetpaypwdRequest;
    UserEditpaypwdRequest userEditpaypwdRequest;
    UserCheckpaypwdRequest userCheckpaypwdRequest;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_old_pwd);

        operation = getIntent().getIntExtra(OPERATION, REQUEST_OLD_PASSWORD);
        initView();
        initEvent();
    }

    private void initView() {
        passwordView = (GridPasswordView) findViewById(R.id.password_view);
        titleTv = (TextView) super.findViewById(R.id.title_tv);
        tipTv = (TextView) super.findViewById(R.id.tip_tv);
        confirmBtn = (Button) super.findViewById(R.id.next_step_btn);

        String content = getIntent().getStringExtra(CONTENT_TV_TEXT);
        String tip = getIntent().getStringExtra(TIP_TV_TEXT);
        String confirm = getIntent().getStringExtra(CONFIRM_BTN_TEXT);
        String titletext=getIntent().getStringExtra(TITLE_TV_TEXT);

        lh_tv_title = (TextView) findViewById(R.id.lh_tv_title);
        lh_btn_back = (Button) findViewById(R.id.lh_btn_back);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);


        tipTv.setText(tip);
        titleTv.setText(content);
        confirmBtn.setText(confirm);
        lh_tv_title.setText(titletext);
    }

    private void initEvent() {
        confirmBtn.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        lh_btn_back.setOnClickListener(this);
        passwordView
                .setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
                    public void onMaxLength(String psw) {
                        AppUtils.hideSoftKeyboard(ValidatePayPwdActivity.this);

                    }

                    public void onChanged(String psw) {

                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_step_btn:
                if (operation == REQUEST_OLD_PASSWORD) {
                    user_checkpaypwd(passwordView.getPassWord());
                } else if (operation == REQUEST_CONFIRM_PASSWORD) {

                    String password1 = passwordView.getPassWord();
                    String password2 = getIntent().getStringExtra(PASSWORD);
                    String smsCode = getIntent().getStringExtra(SMS_CODE);
                    String oldPassword =getIntent().getStringExtra(OLD_PASSWORD);

                    if (password1.equals(password2)) {
                        updatePayPassword(password1,oldPassword,smsCode);
                    } else {
                        ToastUtil.showMessage("两次输入密码不一致");
                    }
                } else {
                    Intent data = new Intent();
                    data.putExtra(PASSWORD, passwordView.getPassWord());
                    setResult(RESULT_OK, data);
                    this.finish();
                }
                break;
            case R.id.ll_back:
            case R.id.lh_btn_back:
                finish();

            default:
                break;
        }
    }


    void  updatePayPassword(String password1,String oldPassword,String smsCode){
        if(TextUtils.isEmpty(smsCode)){
            if(TextUtils.isEmpty(oldPassword)) {//第一次设置新安全密码
//                ToastUtil.showMessage("第一次设置新安全密码");
                user_setpaypwd(password1);
            }
            else{
//                ToastUtil.showMessage("修改密码");
                user_editpaypwd(password1);
            }
        }
        else{//忘记密码
//            ToastUtil.showMessage("忘记密码");
            user_setpaypwd(password1);
        }
    }


    //验证用户原安全密码
    private void user_checkpaypwd(String password) {
        showPd();
        if (userCheckpaypwdRequest != null) {
            userCheckpaypwdRequest.cancel();
        }

        UserCheckpaypwdRequest.Input input = new UserCheckpaypwdRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.password = DigestUtils.md5(password).toUpperCase();
            input.convertJosn();
        }

        userCheckpaypwdRequest = new UserCheckpaypwdRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.getMessage());
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    Intent intent = new Intent();
                intent.putExtra("oldPassWord", passwordView.getPassWord());
                setResult(RESULT_OK,intent);
                finish();
                } else {

                    CommonDialog.showDialogListener(ValidatePayPwdActivity.this, null, "忘记密码", "确定", "安全密码错误,请重试", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(
                                        ValidatePayPwdActivity.this,
                                        RetrievePasswordActivity.class);
                                intent.putExtra("validatePayPassword", true);
                                startActivityForResult(
                                        intent,
                                        RetrievePasswordActivity.REQUEST_VALIDATE_SMS);
                            CommonDialog.DismissProgressDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            passwordView.clearPassword();
                            CommonDialog.DismissProgressDialog();

                        }
                    });
                }
                dismissPd();
            }
        });
        sendJsonRequest(userCheckpaypwdRequest);
    }


    //修改安全密码
    private void user_editpaypwd(String password) {

        if (userEditpaypwdRequest != null) {
            userEditpaypwdRequest.cancel();
        }

        UserEditpaypwdRequest.Input input = new UserEditpaypwdRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.password = DigestUtils.md5(password).toUpperCase();
            input.convertJosn();
        }

        userEditpaypwdRequest = new UserEditpaypwdRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.getMessage());
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userEditpaypwdRequest);
    }


    //设置安全密码
    private void user_setpaypwd(String password) {

        if (userSetpaypwdRequest != null) {
            userSetpaypwdRequest.cancel();
        }

        UserSetpaypwdRequest.Input input = new UserSetpaypwdRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
            input.password = DigestUtils.md5(password).toUpperCase();
            input.convertJosn();
        }

        userSetpaypwdRequest = new UserSetpaypwdRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), error.getMessage());
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    Intent intent=new Intent(Constants.BROCAST_UPDATEMYINFO);
                    sendBroadcast(intent);
                    setResult(RESULT_OK);
                     finish();
                } else {
               ToastUtil.showMessage(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userSetpaypwdRequest);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 验证旧密码成功
        if (requestCode == RetrievePasswordActivity.REQUEST_VALIDATE_SMS
                && resultCode == RESULT_OK) {
            setResult(RESULT_OK,data);
            finish();
        }}

}
