package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.aip.mine.UserPresentExchangeRequest;
import com.zemult.merchant.aip.mine.UserPresentRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsInfo;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.RiseNumberTextView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

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
    @Bind(R.id.tv_car_exchanege)
    TextView tvCarExchanege;
    @Bind(R.id.tv_zuan_exchanege)
    TextView tvZuanExchanege;
    @Bind(R.id.iv_wallet)
    ImageView ivWallet;
    @Bind(R.id.tv_wallet_exchanege)
    TextView tvWalletExchanege;
    @Bind(R.id.tv_flower_exchanege)
    TextView tvFlowerExchanege;
    @Bind(R.id.tv_six_exchanege)
    TextView tvSixExchanege;

    boolean isfirstload = true;
    int isSetPaypwd, isConfirm ;
    double mymoney, exchangeMoney;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_mywallet);
    }

    @Override
    public void init() {
        lhBtnRightiamge.setVisibility(View.VISIBLE);
        lhBtnRightiamge.setBackgroundResource(R.mipmap.zhangdan_icon);
        lhTvTitle.setText("我的账户");
        mContext = this;
        showPd();
        getPresentList();
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
                CommonDialog.showDialogListener(mContext, null, "确认", "取消", "总计兑换" + exchangeMoney + "元，是否确认兑换？" , new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                        showPd();
                        presentExchange();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialog.DismissProgressDialog();
                    }
                });
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private UserPresentRequest presentRequest;

    private void getPresentList() {
        if (presentRequest != null) {
            presentRequest.cancel();
        }
        UserPresentRequest.Input input = new UserPresentRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.convertJosn();

        presentRequest = new UserPresentRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PresentList) response).status == 1) {
                    setPresent(((APIM_PresentList) response).userPresentList);
                } else {
                    ToastUtil.showMessage(((APIM_PresentList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(presentRequest);
    }

    private void setPresent(List<M_Present> userPresentList) {
        for (M_Present p : userPresentList) {
            exchangeMoney += p.num*p.exchangePrice;
            if (p.name.contains("兰博基尼")) {
                tvCarNum.setText("x" + p.num);
                tvCarExchanege.setText("(可兑换￥" + Convert.getMoneyString(p.exchangePrice) + ")");

            } else if (p.name.contains("钻戒")) {
                tvZuanNum.setText("x" + p.num);
                tvZuanExchanege.setText("(可兑换￥" + Convert.getMoneyString(p.exchangePrice) + ")");
            } else if (p.name.contains("钱包")) {
                tvWalletNum.setText("x" + p.num);
                tvWalletExchanege.setText("(可兑换￥" + Convert.getMoneyString(p.exchangePrice) + ")");
            } else if (p.name.contains("花")) {
                tvFlowerNum.setText("x" + p.num);
                tvFlowerExchanege.setText("(可兑换￥" + Convert.getMoneyString(p.exchangePrice) + ")");
            } else {
                tvSixNum.setText("x" + p.num);
                tvSixExchanege.setText("(可兑换￥" + Convert.getMoneyString(p.exchangePrice) + ")");
            }
        }
    }

    private UserPresentExchangeRequest presentExchangeRequest;

    private void presentExchange() {
        if (presentExchangeRequest != null) {
            presentExchangeRequest.cancel();
        }
        UserPresentExchangeRequest.Input input = new UserPresentExchangeRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.convertJosn();

        presentExchangeRequest = new UserPresentExchangeRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("兑换成功");

                    Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                    sendBroadcast(updateintent);

                    tvMoney.withNumber((float) (mymoney + exchangeMoney));
                    tvMoney.setDuration(1000);
                    tvMoney.start();
                    getPresentList();
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(presentExchangeRequest);
    }
}
