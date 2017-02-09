package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserBandcardInfoRequest;
import com.zemult.merchant.aip.mine.UserCashWithdrawRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BalancePayAlertView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class WithdrawalsActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_zhifubao)
    TextView tvZhifubao;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.tv_account)
    TextView tvAccount;
    @Bind(R.id.tv_maxmoney)
    TextView tvMaxmoney;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.et_money)
    EditText etMoney;
    @Bind(R.id.btn_withdrawal)
    Button btnWithdrawal;
    UserCashWithdrawRequest userCashWithdrawRequest;
    UserBandcardInfoRequest userBandcardInfoRequest;
    int isBanged;
    double myMoney = 0;
    String aliAccount = "";

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_withdrawals);
    }

    @Override
    public void init() {
        lhTvTitle.setText("提现");
        user_bandcard_info();
        myMoney = SlashHelper.userManager().getUserinfo().money;
        EditFilter.CashFilter(etMoney, Constants.MAX_WITHDRAW);

        if (myMoney >= Constants.MIN_WITHDRAW) {
            etMoney.setEnabled(true);
            etMoney.setHint("请输入提现金额");
            if (myMoney <= Constants.MAX_WITHDRAW) {
                tvMaxmoney.setText("当前可提现金额 " + myMoney + " 元");
            } else {
                tvMaxmoney.setText("当前可提现金额" + Constants.MAX_WITHDRAW + "元");
            }
        } else {
            etMoney.setHint("提现余额不足");
            etMoney.setEnabled(false);
            tvMaxmoney.setText("当前账户余额 " + myMoney + " 元, 可提现金额 0 元");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }


    //提现
    private void user_cash_withdraw(final String money) {
        loadingDialog.show();
        if (userCashWithdrawRequest != null) {
            userCashWithdrawRequest.cancel();
        }
        UserCashWithdrawRequest.Input input = new UserCashWithdrawRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.money = money;
        input.convertJosn();

        userCashWithdrawRequest = new UserCashWithdrawRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("提现申请已经提交，我们会尽快帮您处理");
                    Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                    sendBroadcast(updateintent);

                    Intent intent = new Intent(WithdrawalsActivity.this, WithdrawDetailActivity.class);
                    intent.putExtra("money", money);
                    intent.putExtra("aliAccount", aliAccount);
                    intent.putExtra("other", ((CommonResult) response).serviceMoney);
                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }


            }
        });
        sendJsonRequest(userCashWithdrawRequest);
    }

    private void user_bandcard_info() {
        if (userBandcardInfoRequest != null) {
            userBandcardInfoRequest.cancel();
        }


        UserBandcardInfoRequest.Input input = new UserBandcardInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.convertJosn();
        userBandcardInfoRequest = new UserBandcardInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    isBanged = ((CommonResult) response).isBand;
                    if (isBanged == 0) {//是否已经绑定(0:否,1:是)
                        tvAccount.setText("未绑定");
                    } else {
                        tvAccount.setText(((CommonResult) response).alipayNumber);
                        aliAccount = ((CommonResult) response).alipayNumber;
                    }

                } else {
                    ToastUtils.show(WithdrawalsActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userBandcardInfoRequest);
    }


    //显示输入安全密码对话框
    private void showInputPwdDialog(final String paymoney) {

        BalancePayAlertView payAlertView = new BalancePayAlertView(WithdrawalsActivity.this);
        payAlertView.setAmount(paymoney + "");

        payAlertView
                .setValidatePasswordListener(new BalancePayAlertView.OnValidatePasswordListener() {
                    public void onValidateSuccessed(String pwd) {
                        user_cash_withdraw(paymoney);
                    }

                    public void onValidateFailed() {
                        ToastUtil.showMessage("安全密码验证失败");
                    }
                });
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_withdrawal})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_withdrawal:
                String money = etMoney.getText().toString();
                if (isBanged == 0) {
                    ToastUtil.showMessage("您还没有绑定支付宝账号");
                    return;
                }
                if (!StringUtils.isEmpty(money)) {
                    if (AppApplication.ISDEBUG) {
                        showInputPwdDialog(money);
                    } else {
                        if (Double.parseDouble(money) >= Constants.MIN_WITHDRAW && Double.parseDouble(money) <= Constants.MAX_WITHDRAW) {
                            showInputPwdDialog(money);
                        } else {
                            ToastUtil.showMessage("请输入正确的提现金额");
                        }

                    }


                } else {
                    ToastUtil.showMessage("请输入提现金额");
                }
                break;
        }
    }
}
