package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantCashWithdrawRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BalancePayAlertView;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.ResponseListener;

public class MerchantWithdrawalsActivity extends BaseActivity {

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
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.iv_zhifubao)
    ImageView ivZhifubao;
    @Bind(R.id.tv_txlab)
    TextView tvTxlab;
    @Bind(R.id.tv_complete)
    TextView tvComplete;

    MerchantCashWithdrawRequest merchantCashWithdrawRequest;
    int merchantId;
    double merchantMoney = 0;
    String aliAccount = "", bankName, bankCard;
    int moneyType; //商家绑定类型 0银行卡 1支付宝

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchnat_withdrawals);
    }

    @Override
    public void init() {
        lhTvTitle.setText("提现");
        moneyType = getIntent().getIntExtra("moneyType", -1);
        merchantId = getIntent().getIntExtra("merchantId", 0);
        aliAccount = getIntent().getStringExtra("aliAccount");
        bankName = getIntent().getStringExtra("bankName");
        bankCard = getIntent().getStringExtra("bankCard");
        merchantMoney = getIntent().getDoubleExtra("merchantMoney", 0);
        EditFilter.CashFilter(etMoney, Constants.MAX_WITHDRAW);

        if(moneyType==0){
            ivZhifubao.setVisibility(View.GONE);
            tvZhifubao.setText(bankName);
            tvAccount.setText(bankCard);
            tvComplete.setText("3~10个工作日之内到账");
        }else if(moneyType==1){
            ivZhifubao.setVisibility(View.VISIBLE);
            tvZhifubao.setText("支付宝账户");
            tvAccount.setText(aliAccount);
            tvComplete.setText("2~3个工作日之内到账");
        }

        if (merchantMoney >= Constants.MIN_WITHDRAW) {
            etMoney.setEnabled(true);
            etMoney.setHint("请输入提现金额");
            if (merchantMoney <= Constants.MAX_WITHDRAW) {
                tvMaxmoney.setText("当前可提现金额 " + merchantMoney + " 元");
            } else {
                tvMaxmoney.setText("当前可提现金额" + Constants.MAX_WITHDRAW + "元");
            }
        } else {
            etMoney.setHint("提现余额不足");
            etMoney.setEnabled(false);
            tvMaxmoney.setText("当前账户余额 " + merchantMoney + " 元, 可提现金额 0 元");
        }
    }

    //商家提现
    private void merchant_cash_withdraw(final String money) {
        loadingDialog.show();
        if (merchantCashWithdrawRequest != null) {
            merchantCashWithdrawRequest.cancel();
        }
        MerchantCashWithdrawRequest.Input input = new MerchantCashWithdrawRequest.Input();
        input.merchantId = SlashHelper.userManager().getUserId();
        input.money = money;
        input.convertJosn();

        merchantCashWithdrawRequest = new MerchantCashWithdrawRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                loadingDialog.dismiss();
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("提现申请已经提交，我们会尽快帮您处理");
                    //刷新商家账户余额
//                    Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
//                    sendBroadcast(updateintent);

                    Intent intent = new Intent(MerchantWithdrawalsActivity.this, WithdrawDetailActivity.class);
                    intent.putExtra("money", money);
                    intent.putExtra("moneyType", moneyType);
                    intent.putExtra("aliAccount", aliAccount);
                    intent.putExtra("bankName", bankName);
                    intent.putExtra("bankCard", bankCard);

                    startActivity(intent);
                    finish();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }


            }
        });
        sendJsonRequest(merchantCashWithdrawRequest);
    }


    //显示输入安全密码对话框
    private void showInputPwdDialog(final String paymoney) {

        BalancePayAlertView payAlertView = new BalancePayAlertView(MerchantWithdrawalsActivity.this);
        payAlertView.setAmount(paymoney + "");

        payAlertView
                .setValidatePasswordListener(new BalancePayAlertView.OnValidatePasswordListener() {
                    public void onValidateSuccessed(String pwd) {
                        merchant_cash_withdraw(paymoney);
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
