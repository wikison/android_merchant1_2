package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.PresentAdapter;
import com.zemult.merchant.aip.mine.UserBandcardInfo_1_2_1Request;
import com.zemult.merchant.aip.mine.UserPresentExchangeRequest;
import com.zemult.merchant.aip.mine.UserPresentRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.RiseNumberTextView;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.MMAlert;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
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
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.btn_duihuan)
    Button btnDuihuan;

    boolean isfirstload = true;
    int isSetPaypwd, isConfirm, isBanged;
    double mymoney, exchangeMoney;
    private Context mContext;
    UserBandcardInfo_1_2_1Request userBandcardInfoRequest;

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
            try {
                DecimalFormat df = new DecimalFormat("######0.00");
                tvMoney.setText(df.format(mymoney) + "");
            } catch (Exception e) {
            }
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
                user_bandcard_info();
                break;
            case R.id.btn_duihuan:
                CommonDialog.showDialogListener(mContext, null, "确认", "取消", "总计兑换" + Convert.getMoneyString(exchangeMoney) + "元，是否确认兑换？", new View.OnClickListener() {
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


    private void user_bandcard_info() {
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
                        MMAlert.showOneOperateDialog(mContext, "提现需要先绑定银行卡", "前往绑定", new MMAlert.OneOperateCallback() {
                            @Override
                            public void onOneOperate() {
                                Intent intent = new Intent(MyWalletActivity.this, BindBankCardActivity.class);
                                intent.putExtra("actfrom", "MyWalletActivity");
                                startActivity(intent);
                            }
                        });
                    } else {
                        if (mymoney < Constants.MIN_WITHDRAW) {
                            ToastUtil.showMessage("您的余额不足" + Convert.getMoneyString(Constants.MIN_WITHDRAW) + "元，暂时无法提现");
                        } else {
                            Intent intentwithdrawals = new Intent(MyWalletActivity.this, WithdrawalsActivity.class);
                            startActivity(intentwithdrawals);
                        }
                    }

                } else {
                    ToastUtils.show(MyWalletActivity.this, ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userBandcardInfoRequest);
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
            exchangeMoney += p.num * p.exchangePrice;
        }

        GridLayoutManager manager = new GridLayoutManager(mContext, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0)
                    return 2; // 2/2=1 代表权重
                else
                    return 1;
            }
        });
        // 错列网格布局
        rv.setLayoutManager(manager);
        rv.setAdapter(new PresentAdapter(mContext, userPresentList));
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
