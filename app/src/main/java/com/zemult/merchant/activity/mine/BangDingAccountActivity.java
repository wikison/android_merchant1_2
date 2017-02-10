package com.zemult.merchant.activity.mine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.common.CommonSignNumberRequest;
import com.zemult.merchant.aip.mine.UserBandcardInfoRequest;
import com.zemult.merchant.aip.mine.UserBandcardPayRequest;
import com.zemult.merchant.alipay.PayResult;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class BangDingAccountActivity extends BaseActivity {

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
    @Bind(R.id.iv_left)
    ImageView ivLeft;
    @Bind(R.id.tv_myaccount)
    TextView tvMyaccount;
    @Bind(R.id.rel_zhifu)
    RelativeLayout relZhifu;
    UserBandcardInfoRequest userBandcardInfoRequest;
    int isBanded;
    @Bind(R.id.account_tv)
    TextView accountTv;
    UserBandcardPayRequest userBandcardPayRequest;
    CommonSignNumberRequest commonSignNumberRequest;
    private static final int SDK_PAY_FLAG = 1;
    // 商户订单号
    public String ORDER_SN = "",actfrom;


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(BangDingAccountActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if(!StringUtils.isEmpty(actfrom)&&actfrom.equals("MyWalletActivity")){
                            Intent intentwithdrawals = new Intent(BangDingAccountActivity.this, WithdrawalsActivity.class);
                            startActivity(intentwithdrawals);
                            finish();
                        }
                        else{
                            user_bandcard_info();
                        }
                    } else {
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(BangDingAccountActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else if (TextUtils.equals(resultStatus, "4000")) {
                            Toast.makeText(BangDingAccountActivity.this, "支付宝调用失败", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(BangDingAccountActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bang_ding_account);
    }

    @Override
    public void init() {
        lhTvTitle.setText("支付宝绑定");
        actfrom=getIntent().getStringExtra("actfrom");
    }

    @Override
    protected void onResume() {
        super.onResume();
        user_bandcard_info();
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
                    isBanded = ((CommonResult) response).isBand;
                    if (isBanded == 0) {//是否已经绑定(0:否,1:是)
                        accountTv.setText("未绑定");
                    } else {
                        accountTv.setText(((CommonResult) response).alipayNumber);
                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                        sendBroadcast(updateintent);
                    }

                } else {
                    ToastUtils.show(BangDingAccountActivity.this, ((CommonResult) response).info);
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

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.lh_tv_title, R.id.tv_myaccount, R.id.rel_zhifu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.lh_tv_title:
                break;
            case R.id.rel_zhifu:
            case R.id.tv_myaccount:
                if (isBanded == 0) {
                    CommonDialog.showDialogListener(BangDingAccountActivity.this,
                            null, "取消", "继续", "使用您要绑定的支付宝账号充值0.01元完成绑定，绑定仅用于提现，" +
                                    "账号一经绑定，不可更改", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CommonDialog.DismissProgressDialog();
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent intent_pay = new Intent(BangDingAccountActivity.this, BangDingAccountActivity.class);
//                                    startActivity(intent_pay);
//                                    CommonDialog.DismissProgressDialog();
                                    CommonDialog.DismissProgressDialog();
                                    user_bandcard_pay();
                                }
                            });
                }
                break;
        }
    }


    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void alipay(final String orderStr) {
        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(BangDingAccountActivity.this);
                Map<String, String> result = alipay.payV2(orderStr, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private void commonSignNumber() {
        try {
            showPd();

            if (commonSignNumberRequest != null) {
                commonSignNumberRequest.cancel();
            }
            CommonSignNumberRequest.Input input = new CommonSignNumberRequest.Input();
            input.number = ORDER_SN;

            input.convertJosn();

            commonSignNumberRequest = new CommonSignNumberRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        alipay(((CommonResult) response).orderStr);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(commonSignNumberRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }


    private void user_bandcard_pay() {
        try {
            showPd();
            if (userBandcardPayRequest != null) {
                userBandcardPayRequest.cancel();
            }
            UserBandcardPayRequest.Input input = new UserBandcardPayRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.money = 0.01;
            input.convertJosn();

            userBandcardPayRequest = new UserBandcardPayRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        dismissPd();
                        ORDER_SN = ((CommonResult) response).number;
                        commonSignNumber();
                    }
                }
            });
            sendJsonRequest(userBandcardPayRequest);
        } catch (Exception e) {
        }

    }

}
