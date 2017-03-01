package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.CommonWithcashCountRequest;
import com.zemult.merchant.aip.mine.UserBandcardInfo_1_2_1Request;
import com.zemult.merchant.aip.mine.UserCashInfoRequest;
import com.zemult.merchant.aip.mine.UserCashWithdrawRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BalancePayAlertView;

import java.text.DecimalFormat;

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
    UserCashInfoRequest userCashInfoRequest;
    UserBandcardInfo_1_2_1Request userBandcardInfoRequest;
    CommonWithcashCountRequest commonWithcashCountRequest;
    int isBanged;
    double myMoney = 0;
    String aliAccount = "";
    String money;
    double serviceMoney;
    double cashMoney = 0;//日提现剩余额度

    private long firstTime = 0; //记录第一次点击的时间

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_withdrawals);
    }

    @Override
    public void init() {
        lhTvTitle.setText("提现");
        user_bandcard_info_1_2_1();
        myMoney = SlashHelper.userManager().getUserinfo().money;

        user_cash_info();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

    }

    private void user_cash_info() {
        if (userCashInfoRequest != null) {
            userCashInfoRequest.cancel();
        }
        UserCashInfoRequest.Input input = new UserCashInfoRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.convertJosn();

        userCashInfoRequest = new UserCashInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    //返回的cashMoney为日已提现金额
                    String sHint = "";
                    cashMoney = ((CommonResult) response).cashMoney;
                    cashMoney = Constants.MAX_WITHDRAW - cashMoney;
                    if (cashMoney < myMoney) {
                        EditFilter.CashFilter(etMoney, cashMoney);
                    } else {
                        EditFilter.CashFilter(etMoney, myMoney);
                    }
                    if (cashMoney < Constants.MIN_WITHDRAW) {
                        sHint = String.format("当前可提现金额<font color='#e6bb7c'>%s</font>元, 账户余额<font color='#e6bb7c'>%s</font>元", "0.00", Convert.getMoneyString(myMoney));
                        etMoney.setEnabled(false);
                        btnWithdrawal.setEnabled(false);
                        btnWithdrawal.setBackgroundResource(R.drawable.next_bg_btn_select);
                    } else {
                        if (myMoney <= cashMoney) {
                            sHint = String.format("当前可提现金额<font color='#e6bb7c'>%s</font>元, 账户余额<font color='#e6bb7c'>%s</font>元", Convert.getMoneyString(myMoney), Convert.getMoneyString(myMoney));
                        } else {
                            sHint = String.format("当前可提现金额<font color='#e6bb7c'>%s</font>元, 账户余额<font color='#e6bb7c'>%s</font>元", Convert.getMoneyString(cashMoney), Convert.getMoneyString(myMoney));
                        }
                    }
                    tvMaxmoney.setText(Html.fromHtml(sHint));
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }


            }
        });
        sendJsonRequest(userCashInfoRequest);
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
                    intent.putExtra("money", Double.parseDouble(money) - ((CommonResult) response).serviceMoney);
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

    private void user_bandcard_info_1_2_1() {
        if (userBandcardInfoRequest != null) {
            userBandcardInfoRequest.cancel();
        }


        UserBandcardInfo_1_2_1Request.Input input = new UserBandcardInfo_1_2_1Request.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }

        input.convertJosn();
        userBandcardInfoRequest = new UserBandcardInfo_1_2_1Request(input, new ResponseListener() {
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
                        tvAccount.setText(((CommonResult) response).bankName + "(尾号"
                                + ((CommonResult) response).bankNumber.substring(
                                ((CommonResult) response).bankNumber.length() - 4,
                                ((CommonResult) response).bankNumber.length()
                        )
                                + ")");
                        aliAccount = tvAccount.getText().toString();
                    }

                } else {
                    ToastUtils.show(WithdrawalsActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userBandcardInfoRequest);
    }


    private void commonWithcashCountRequest() {
        if (commonWithcashCountRequest != null) {
            commonWithcashCountRequest.cancel();
        }

        CommonWithcashCountRequest.Input input = new CommonWithcashCountRequest.Input();
        input.money = money;
        input.convertJosn();
        commonWithcashCountRequest = new CommonWithcashCountRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    serviceMoney = ((CommonResult) response).serviceMoney;
                    showInputPwdDialog(money, serviceMoney);
                } else {
                    ToastUtils.show(WithdrawalsActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commonWithcashCountRequest);
    }


    //显示输入安全密码对话框
    private void showInputPwdDialog(final String paymoney, double serviceMoney) {

        BalancePayAlertView payAlertView = new BalancePayAlertView(WithdrawalsActivity.this);

        try {
            DecimalFormat df = new DecimalFormat("######0.00");
            payAlertView.setAmount(df.format(Double.parseDouble(paymoney)) + "");
            payAlertView.setTips("提现");
            payAlertView.setTips2("额外扣除" + df.format(serviceMoney) + "元手续费");//
        } catch (Exception e) {
        }


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
                long secondTime = System.currentTimeMillis();
                if (secondTime - firstTime < 1000) {
                    firstTime = secondTime;
                    return;
                }
                firstTime = secondTime;
                money = etMoney.getText().toString();
                double toCashMoney = 0;
                if (isBanged == 0) {
                    ToastUtil.showMessage("您还没有绑定银行卡");
                    return;
                }
                if (!StringUtils.isEmpty(money)) {
                    toCashMoney = Double.parseDouble(money);
                    if (toCashMoney < Constants.MIN_WITHDRAW) {
                        ToastUtil.showMessage("最小提现额为" + Constants.MIN_WITHDRAW + "元，请重新输入");
                        return;
                    }
                    if (toCashMoney > Constants.MAX_WITHDRAW) {
                        ToastUtil.showMessage("最大提现额为" + Constants.MAX_WITHDRAW + "元，请重新输入");
                        return;
                    }
                    if (toCashMoney > cashMoney) {
                        ToastUtil.showMessage("您输入的金额已超出当前可提现额度，请重新输入");
                        return;
                    }
                    if (toCashMoney > myMoney) {
                        ToastUtil.showMessage("您输入的金额已超出账户余额，请重新输入");
                        return;
                    }

                    commonWithcashCountRequest();


                } else {
                    ToastUtil.showMessage("请输入提现金额");
                }
                break;
        }
    }
}
