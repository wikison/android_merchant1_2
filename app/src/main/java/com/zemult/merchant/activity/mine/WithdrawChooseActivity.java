package com.zemult.merchant.activity.mine;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantEnterActivity;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 选择提现方式
 *
 * @author djy
 * @time 2016/12/5 14:15
 */
public class WithdrawChooseActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll1)
    LinearLayout ll1;
    @Bind(R.id.ll2)
    LinearLayout ll2;

    private static final int REQ_CHOOSE_WITHDRAW = 0x110;
    private String aliAccount,bankCard,bankName,bankUser;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_withdraw_choose);
        aliAccount = getIntent().getStringExtra("aliAccount");
        bankCard = getIntent().getStringExtra("bankCard");
        bankName = getIntent().getStringExtra("bankName");
        bankUser = getIntent().getStringExtra("bankUser");
    }

    @Override
    public void init() {
        lhTvTitle.setText("提现方式");
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll1, R.id.ll2})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll1:
                break;
            case R.id.ll2:
                intent = new Intent(this, BindZfbActivity.class);
                intent.putExtra("aliAccount", aliAccount);
                startActivityForResult(intent, REQ_CHOOSE_WITHDRAW);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
