package com.zemult.merchant.activity.mine;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.Convert;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/9/27.
 */
//提现详情
public class WithdrawDetailActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.others_tv)
    TextView othersTv;
    @Bind(R.id.commit_btn)
    Button commitBtn;

    @Override
    public void setContentView() {
        setContentView(R.layout.withdrawdetail);

    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("提现详情");

        tvMoney.setText("￥" + Convert.getMoneyString(getIntent().getDoubleExtra("money",0)));//金额
        othersTv.setText("￥" + Convert.getMoneyString(getIntent().getDoubleExtra("other",0)));//手续费
        tvAccount.setText(getIntent().getStringExtra("aliAccount"));//账号
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.commit_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.commit_btn:
            case R.id.ll_back:
                onBackPressed();

                break;
        }
    }
}
