package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.pwdsetting.OldPhoneAuthActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.RiseNumberTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.tv_money)
    RiseNumberTextView tvMoney;
    @Bind(R.id.btn_tixian)
    Button btnTixian;
    @Bind(R.id.tv_car_num)
    TextView tvCarNum;
    @Bind(R.id.tv_zuan_num)
    TextView tvZuanNum;
    @Bind(R.id.tv_wallet_num)
    TextView tvWalletNum;
    @Bind(R.id.tv_six_num)
    TextView tvSixNum;
    @Bind(R.id.tv_flower_num)
    TextView tvFlowerNum;
    @Bind(R.id.btn_duihuan)
    Button btnDuihuan;

    boolean isfirstload = true;
    int isSetPaypwd, isConfirm;
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
        if (null == SlashHelper.userManager().getUserinfo()) {
            finish();
        }
        isSetPaypwd = SlashHelper.userManager().getUserinfo().isSetPaypwd;
        mymoney = SlashHelper.userManager().getUserinfo().money;
        isConfirm = SlashHelper.userManager().getUserinfo().isConfirm;//是否实名认证过(0:否1:是)

        if (isfirstload) {
            tvMoney.withNumber((float) mymoney);
            tvMoney.setDuration(1000);
            tvMoney.start();
            isfirstload = false;
        } else {
            tvMoney.setText(mymoney + "");
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_tv_title, R.id.lh_btn_rightiamge, 
            R.id.tv_money, R.id.btn_tixian, R.id.btn_duihuan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_btn_rightiamge:
                Intent intentbill = new Intent(MyWalletActivity.this, MyBillActivity.class);
                startActivity(intentbill);
                break;
            case R.id.btn_tixian:
                Intent intentwithdrawals = new Intent(MyWalletActivity.this, WithdrawalsActivity.class);
                startActivity(intentwithdrawals);
                break;
            case R.id.btn_duihuan:
                // TODO: 2017/1/19
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
