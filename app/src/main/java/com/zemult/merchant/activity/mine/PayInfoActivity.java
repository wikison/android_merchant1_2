package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserPayDelRequest;
import com.zemult.merchant.aip.mine.UserPayInfoRequest;
import com.zemult.merchant.alipay.taskpay.AssessmentActivity;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.alipay.taskpay.TaskPayResultActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.ImageManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

public class PayInfoActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.iv_merchant_head)
    ImageView ivMerchantHead;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.iv_sale_head)
    ImageView ivSaleHead;
    @Bind(R.id.tv_sale_name)
    TextView tvSaleName;
    @Bind(R.id.tv_sale_money)
    TextView tvSaleMoney;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_pay_num)
    TextView tvPayNum;
    @Bind(R.id.tv_trade_time)
    TextView tvTradeTime;
    @Bind(R.id.tv_pay_type)
    TextView tvPayType;

    @Bind(R.id.rtv_to_pay)
    RoundTextView rtvToPay;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.ll_pay_type)
    LinearLayout llPayType;
    @Bind(R.id.tv_dingjin)
    TextView tvDingjin;
    @Bind(R.id.tv_rest_left)
    TextView tvRestLeft;
    @Bind(R.id.tv_restmoney)
    TextView tvRestmoney;
    @Bind(R.id.havedingjin_ll)
    LinearLayout havedingjinLl;
    @Bind(R.id.rtv_to_recom)
    RoundTextView rtvToRecom;

    private Context mContext;
    private Activity mActivity;
    UserPayInfoRequest userPayInfoRequest;
    UserPayDelRequest userPayDelRequest;
    M_Bill m;
    int userPayId;
    protected ImageManager mImageManager;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pay_info);
    }

    @Override
    public void init() {
        initData();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
        mImageManager = new ImageManager(mContext);
        userPayId = getIntent().getIntExtra("userPayId", 0);
        lhTvTitle.setText("消费单详情");
        if (userPayId > 0)
            user_pay_info();
    }


    //订单详情
    private void user_pay_info() {
        showPd();
        if (userPayInfoRequest != null) {
            userPayInfoRequest.cancel();
        }


        UserPayInfoRequest.Input input = new UserPayInfoRequest.Input();
        input.userPayId = userPayId;

        input.convertJosn();
        userPayInfoRequest = new UserPayInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillInfo) response).status == 1) {
                    m = ((APIM_UserBillInfo) response).userPayInfo;
                    //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))

                    tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
//                    if (m.rewardMoney == 0) {
//                        haveredLl.setVisibility(View.GONE);
//                    } else {
//                        haveredLl.setVisibility(View.VISIBLE);
//                        tvRealpay.setText("" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
//                        tvRedmoney.setText("" + (m.rewardMoney == 0 ? "0" : Convert.getMoneyString(m.rewardMoney)));
//                    }
                    //支付单类型(0:买单--(直接买单/关联预约单无定金买单-包含合并赞赏金额),3:购买礼物,4:打赏,5:预约单定金,6有定金的预约单买单(包含合并赞赏金额))
                    if (m.type == 0) {
                        havedingjinLl.setVisibility(View.GONE);
                        switch (m.state) {
                            //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))
                            case 0:
                                tvState.setText("待支付");
                                llPayType.setVisibility(View.GONE);
                                rtvToPay.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                // 是否评价(0:否,1:是)(type=0时有值)
                                if (m.isComment==1) {
                                    llPayType.setVisibility(View.VISIBLE);
                                    rtvToRecom.setVisibility(View.GONE);
                                    rtvToPay.setVisibility(View.GONE);
                                    tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                                    tvState.setText("已完成");
                                } else {
                                    tvMoney.setText("-" + (m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                                    tvState.setText("待评价");
                                    rtvToPay.setVisibility(View.GONE);
                                    llPayType.setVisibility(View.VISIBLE);
                                    rtvToRecom.setVisibility(View.VISIBLE);
                                }

                                break;
                            case 2:
                                tvState.setText("已失效");
                                llPayType.setVisibility(View.GONE);
                                rtvToPay.setVisibility(View.GONE);
                                rtvToRecom.setVisibility(View.GONE);
                                break;
                        }

                    } else if (m.type == 3) {

                    } else if (m.type == 4) {

                    } else if (m.type == 5) {
                        tvState.setText("订金");
                        havedingjinLl.setVisibility(View.GONE);
                        llPay.setVisibility(View.GONE);

                    } else if (m.type == 6) {
                        havedingjinLl.setVisibility(View.VISIBLE);
                        tvDingjin.setText("" +(m.reservationMoney == 0 ? "0" : Convert.getMoneyString(m.reservationMoney)));
                        tvRestmoney.setText("" +(m.payMoney == 0 ? "0" : Convert.getMoneyString(m.payMoney)));
                        switch (m.state) {
                            //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))
                            case 0:
                                tvState.setText("待支付");
                                llPayType.setVisibility(View.GONE);
                                rtvToPay.setVisibility(View.VISIBLE);
                                break;
                            case 1:
                                // 是否评价(0:否,1:是)(type=0时有值)
                                tvMoney.setText("-" + (Convert.getMoneyString(m.payMoney)));
                                if (m.isComment == 1) {
                                    tvState.setText("已完成");
                                    llPayType.setVisibility(View.VISIBLE);
                                    rtvToPay.setVisibility(View.GONE);
                                    rtvToRecom.setVisibility(View.GONE);
                                } else {
                                    tvState.setText("待评价");
                                    llPayType.setVisibility(View.VISIBLE);
                                    rtvToPay.setVisibility(View.GONE);
                                    rtvToRecom.setVisibility(View.VISIBLE);
                                }

                                break;
                            case 2:
                                tvState.setText("已失效");
                                llPayType.setVisibility(View.GONE);
                                rtvToPay.setVisibility(View.GONE);
                                rtvToRecom.setVisibility(View.GONE);
                                break;
                        }
                    }


                        switch (m.moneyType) {
                            case 0:
                                tvPayType.setText("账户余额");
                                break;
                            case 1:
                                tvPayType.setText("支付宝支付");
                                break;
                            case 2:
                                tvPayType.setText("微信支付");
                                break;
                        }
                        tvMerchantName.setText(m.merchantName);
                        if (!TextUtils.isEmpty(m.saleUserHead)) {
                            mImageManager.loadCircleImage(m.saleUserHead, ivSaleHead);
                        }
                        tvSaleName.setText(m.saleUserName);
//                    tvSaleMoney.setText("" + (m.allMoney == 0 ? "0.00" : Convert.getMoneyString(m.allMoney)));
//                    tvPayMoney.setText("" + (m.payMoney == 0 ? "0.00" : Convert.getMoneyString(m.payMoney)));
                        tvPayNum.setText(m.number);
                        tvTradeTime.setText(m.createtime);

                    } else {
                        ToastUtils.show(PayInfoActivity.this, ((APIM_UserBillInfo) response).info);
                    }
                    dismissPd();
                }
            });

            sendJsonRequest(userPayInfoRequest);
        }

        @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rtv_to_pay, R.id.rtv_to_recom})
        public void onClick (View view){
            switch (view.getId()) {
                case R.id.lh_btn_back:
                case R.id.ll_back:
                    EventBus.getDefault().post(TaskPayResultActivity.APPOINT_REFLASH);
                    finish();
                    break;
//            case R.id.rtv_cancel:
//                CommonDialog.showDialogListener(mContext, null, "否", "是", "是否取消订单", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CommonDialog.DismissProgressDialog();
//
//                    }
//                }, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CommonDialog.DismissProgressDialog();
//                        cancelPay();
//                    }
//                });
//                break;
                case R.id.rtv_to_pay:
                    goPay();
                    break;

                case R.id.rtv_to_recom:
                    goAssessment();
                    break;
            }
        }

    private void cancelPay() {
        showPd();
        if (userPayDelRequest != null) {
            userPayDelRequest.cancel();
        }


        UserPayDelRequest.Input input = new UserPayDelRequest.Input();
        input.userPayId = userPayId;

        input.convertJosn();
        userPayDelRequest = new UserPayDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    Intent intent = new Intent(Constants.BROCAST_REFRESH_ORDER);
                    sendBroadcast(intent);
                    user_pay_info();
                } else {
                    ToastUtils.show(PayInfoActivity.this, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userPayDelRequest);
    }

    private void goAssessment() {
        Intent intent = new Intent(mActivity, AssessmentActivity.class);
        intent.putExtra("userPayId", userPayId);
        intent.putExtra("managerhead", m.saleUserHead);
        intent.putExtra("managername", m.saleUserName);
        intent.putExtra("merchantName", m.merchantName);
        startActivityForResult(intent, 2000);
    }

    private void goPay() {
        Intent intent = new Intent(mActivity, ChoosePayTypeActivity.class);
        intent.putExtra("consumeMoney", m.allMoney);
        intent.putExtra("order_sn", m.number);
        intent.putExtra("userPayId", userPayId);
        intent.putExtra("merchantId", m.merchantId);
        intent.putExtra("merchantName", m.merchantName);
        intent.putExtra("merchantHead", m.merchantHead);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1000) {
                setResult(RESULT_OK);
                onBackPressed();

            } else if (requestCode == 2000) {
                user_pay_info();
                Intent intent = new Intent(Constants.BROCAST_REFRESH_ORDER);
                intent.putExtra("userPayId", userPayId);
                setResult(RESULT_OK, intent);
                sendBroadcast(intent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
