package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.pwdsetting.OldPhoneAuthActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.RiseNumberTextView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2016/7/15.
 */
public class MyWalletActivity extends BaseActivity {
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
    @Bind(R.id.tv_xgpostion)
    TextView tvXgpostion;
    @Bind(R.id.tv_money)
    RiseNumberTextView tvMoney;
    @Bind(R.id.btn_withdrawals)
    RoundTextView btnWithdrawals;
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
    double mymoney;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_mywallet);
    }

    @Override
    public void init() {
        lhBtnRightiamge.setVisibility(View.VISIBLE);
        lhBtnRightiamge.setBackgroundResource(R.mipmap.zhangdan_icon);
        lhTvTitle.setText("我的账户");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(null==SlashHelper.userManager().getUserinfo()){
           finish();
        }
        isSetPaypwd=SlashHelper.userManager().getUserinfo().isSetPaypwd;
        mymoney=SlashHelper.userManager().getUserinfo().money;
        isConfirm= SlashHelper.userManager().getUserinfo().isConfirm;//是否实名认证过(0:否1:是)

        if(isfirstload){
            tvMoney.withNumber((float) mymoney);
            tvMoney.setDuration(1000);
            tvMoney.start();
            isfirstload=false;
        }
        else {
            tvMoney.setText( mymoney+"");
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_tv_title, R.id.lh_btn_rightiamge, R.id.tv_money, R.id.btn_withdrawals, R.id.rl_bangbankcard, R.id.rl_changephone, R.id.rl_authentication, R.id.rl_loginpassword, R.id.rl_paypassword})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_btn_rightiamge:
                Intent intentbill=new Intent(MyWalletActivity.this,MyBillActivity.class);
                startActivity(intentbill);
                break;
            case R.id.btn_withdrawals:
                Intent intentwithdrawals=new Intent(MyWalletActivity.this,WithdrawalsActivity.class);
                startActivity(intentwithdrawals);
                break;
            case R.id.rl_bangbankcard:
                Intent intentcar=new Intent(MyWalletActivity.this,BangDingAccountActivity.class);
                startActivity(intentcar);
                break;
            case R.id.rl_changephone:
                Intent intentphone=new Intent(MyWalletActivity.this,OldPhoneAuthActivity.class);
                startActivity(intentphone);
                break;
            case R.id.rl_authentication:
                if(isConfirm==0){
                    Intent intenttrue=new Intent(MyWalletActivity.this,TrueNameActivity.class);
                    startActivity(intenttrue);
                }
                else{
                    Intent intenttrue=new Intent(MyWalletActivity.this,TrueNameResultActivity.class);
                    startActivity(intenttrue);
                }

                break;
            case R.id.rl_loginpassword:
                Intent intentpwd=new Intent(MyWalletActivity.this,ChangePasswordActivity.class);
                intentpwd.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentpwd);
                break;
            case R.id.rl_paypassword:
                    Intent intentpaypassword=new Intent(MyWalletActivity.this,PayPasswordManagerActivity.class);
                    startActivity(intentpaypassword);
                break;
        }
    }
}
