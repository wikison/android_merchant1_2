package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class PayPasswordManagerActivity extends BaseActivity {


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
    @Bind(R.id.rl_changepaypassword)
    RelativeLayout rlChangepaypassword;
    @Bind(R.id.rl_findpaypasswrd)
    RelativeLayout rlFindpaypasswrd;

    int isSetPaypwd;
    private String smsCode, oldPassWord; //短信验证码
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pay_password_manager);
    }

    @Override
    public void init() {
        isSetPaypwd = SlashHelper.userManager().getUserinfo().isSetPaypwd;//是否设置过安全密码  0否  1是
        if (isSetPaypwd==0) {
            toEditNewPwd();
            return;
        }
        if(getIntent().getBooleanExtra("gotofind",false)){
            if(SlashHelper.userManager().getUserinfo().isConfirm==0) {
                ToastUtil.showMessage("您还没有完成实名认证");
                Intent intenttrue = new Intent(PayPasswordManagerActivity.this, TrueNameActivity.class);
                startActivity(intenttrue);
            }else{
                onForgetPasswordClick();
            }
        }
        lhTvTitle.setText("支付密码");


    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_changepaypassword, R.id.rl_findpaypasswrd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_changepaypassword:
                onModifyPasswordClick();
                break;
            case R.id.rl_findpaypasswrd:

                if(SlashHelper.userManager().getUserinfo().isConfirm==0) {
                    ToastUtil.showMessage("您还没有完成实名认证");
                    Intent intenttrue = new Intent(PayPasswordManagerActivity.this, TrueNameActivity.class);
                    startActivity(intenttrue);
                }else{
                    onForgetPasswordClick();
                }

                break;
        }
    }

    /**
     * 修改密码
     *
     */
    public void onModifyPasswordClick() {
        toOldPassword();
    }

    private void toOldPassword() {
        Intent intent = new Intent(this, ValidatePayPwdActivity.class);
        intent.putExtra(ValidatePayPwdActivity.OPERATION,
                ValidatePayPwdActivity.REQUEST_OLD_PASSWORD);
        intent.putExtra(ValidatePayPwdActivity.TITLE_TV_TEXT, "修改支付密码");
        intent.putExtra(ValidatePayPwdActivity.TIP_TV_TEXT, "");
        intent.putExtra(ValidatePayPwdActivity.CONTENT_TV_TEXT, "请输入旧的密码");
        intent.putExtra(ValidatePayPwdActivity.CONFIRM_BTN_TEXT, "下一步");
        startActivityForResult(intent,
                ValidatePayPwdActivity.REQUEST_OLD_PASSWORD);
    }

    /**
     * 忘记密码
     *
     */
    public void onForgetPasswordClick() {
                        Intent intent = new Intent(this, RetrievePasswordActivity.class);
                        intent.putExtra("validatePayPassword", true);
                        startActivityForResult(intent,
                                RetrievePasswordActivity.REQUEST_VALIDATE_SMS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 验证旧密码成功
        if (requestCode == ValidatePayPwdActivity.REQUEST_OLD_PASSWORD
                && resultCode == RESULT_OK) {
            smsCode = data.getStringExtra("smsCode");
            if (TextUtils.isEmpty(smsCode)) {
                oldPassWord = data.getStringExtra("oldPassWord");
            } else {
                oldPassWord = null;
            }
            toEditNewPwd();
        }
        // 设置新密码成功
        else if (requestCode == ValidatePayPwdActivity.REQUEST_NEW_PASSWORD) {
            if (resultCode == RESULT_OK) {
                toConfirmNewPwd(data);
            } else if (isSetPaypwd==0) {
                this.finish();
            }
        }
        // 确认新密码成功
        else if (requestCode == ValidatePayPwdActivity.REQUEST_CONFIRM_PASSWORD) {
            if (resultCode == RESULT_OK) {
                ToastUtil.showMessage("设置成功");
                finish();
            }
            if (isSetPaypwd==0) {
                this.finish();
            }
        } else if (requestCode == RetrievePasswordActivity.REQUEST_VALIDATE_SMS
                && resultCode == RESULT_OK) {
            smsCode = data.getStringExtra("smsCode");
            oldPassWord = null;
            toEditNewPwd();
        }
        else if (requestCode == RetrievePasswordActivity.REQUEST_VALIDATE_SMS//忘记安全密码，跳转以后按返回
                && resultCode != RESULT_OK&&getIntent().getBooleanExtra("gotofind",false)) {
            finish();
        }
        else if (requestCode == RetrievePasswordActivity.REQUEST_VALIDATE_ID
                && resultCode == RESULT_OK) {
            toEditNewPwd();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toEditNewPwd() {
        Intent intent = new Intent(this, ValidatePayPwdActivity.class);
        intent.putExtra(ValidatePayPwdActivity.OPERATION,
                ValidatePayPwdActivity.REQUEST_NEW_PASSWORD);
        intent.putExtra(ValidatePayPwdActivity.TITLE_TV_TEXT, "修改支付密码");
        intent.putExtra(ValidatePayPwdActivity.TIP_TV_TEXT, "");
        intent.putExtra(ValidatePayPwdActivity.CONTENT_TV_TEXT, "请输入新的密码");
        intent.putExtra(ValidatePayPwdActivity.CONFIRM_BTN_TEXT, "下一步");
        startActivityForResult(intent,
                ValidatePayPwdActivity.REQUEST_NEW_PASSWORD);
    }

    private void toConfirmNewPwd(Intent data) {
        Intent intent = new Intent(this, ValidatePayPwdActivity.class);
        intent.putExtra(ValidatePayPwdActivity.OPERATION,
                ValidatePayPwdActivity.REQUEST_CONFIRM_PASSWORD);
        intent.putExtra(ValidatePayPwdActivity.TITLE_TV_TEXT, "修改支付密码");
        intent.putExtra(ValidatePayPwdActivity.TIP_TV_TEXT, "");
        intent.putExtra(ValidatePayPwdActivity.CONTENT_TV_TEXT, "请再次输入新的密码");
        intent.putExtra(ValidatePayPwdActivity.CONFIRM_BTN_TEXT, "下一步");
        intent.putExtra(ValidatePayPwdActivity.PASSWORD,
                data.getStringExtra(ValidatePayPwdActivity.PASSWORD));
        intent.putExtra(ValidatePayPwdActivity.SMS_CODE, smsCode);
        intent.putExtra(ValidatePayPwdActivity.OLD_PASSWORD, oldPassWord);
        startActivityForResult(intent,
                ValidatePayPwdActivity.REQUEST_CONFIRM_PASSWORD);
        // System.out.println("设置的新密码："
//				+ data.getStringExtra(ValidatePayPwdActivity.PASSWORD));
    }

}