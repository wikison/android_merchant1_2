package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.pwdsetting.OldPhoneAuthActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2017/1/18.
 */

public class AccountSafeActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.rl_bangbankcard)
    RelativeLayout rlBangbankcard;
    @Bind(R.id.rl_changephone)
    RelativeLayout rlChangephone;
    @Bind(R.id.rl_authentication)
    RelativeLayout rlAuthentication;
    @Bind(R.id.rl_loginpassword)
    RelativeLayout rlLoginpassword;
    @Bind(R.id.rl_paypassword)
    RelativeLayout rlPaypassword;
    boolean isfirstload=true;
    int isSetPaypwd,isConfirm;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_accountsafe);
    }

    @Override
    public void init() {
        lhTvTitle.setText("账户与安全");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(null== SlashHelper.userManager().getUserinfo()){
            finish();
        }
        isSetPaypwd=SlashHelper.userManager().getUserinfo().isSetPaypwd;
        isConfirm= SlashHelper.userManager().getUserinfo().isConfirm;//是否实名认证过(0:否1:是)

    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_bangbankcard, R.id.rl_changephone, R.id.rl_authentication, R.id.rl_loginpassword, R.id.rl_paypassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                finish();
                break;
            case R.id.rl_bangbankcard:
                Intent intentcar=new Intent(AccountSafeActivity.this,BangDingAccountActivity.class);
                startActivity(intentcar);
                break;
            case R.id.rl_changephone:
                Intent intentphone=new Intent(AccountSafeActivity.this,OldPhoneAuthActivity.class);
                startActivity(intentphone);
                break;
            case R.id.rl_authentication:
                if(isConfirm==0){
                    Intent intenttrue=new Intent(AccountSafeActivity.this,TrueNameActivity.class);
                    startActivity(intenttrue);
                }
                else{
                    Intent intenttrue=new Intent(AccountSafeActivity.this,TrueNameResultActivity.class);
                    startActivity(intenttrue);
                }
                break;
            case R.id.rl_loginpassword:
                Intent intentpwd=new Intent(AccountSafeActivity.this,ChangePasswordActivity.class);
                intentpwd.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentpwd);
                break;
            case R.id.rl_paypassword:
                Intent intentpaypassword=new Intent(AccountSafeActivity.this,PayPasswordManagerActivity.class);
                startActivity(intentpaypassword);
                break;
        }
    }
}
