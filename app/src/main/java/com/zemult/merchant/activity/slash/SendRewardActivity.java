package com.zemult.merchant.activity.slash;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.SendRewardAdapter;
import com.zemult.merchant.aip.common.CommonRewardRequest;
import com.zemult.merchant.aip.mine.UserRewardPayAddRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.FixedGridView;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class SendRewardActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.gv)
    FixedGridView gv;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;

    private Context mContext;
    private SendRewardAdapter adapter;
    private int toUserId = 0;
    private double money = 0;
    private String ORDER_SN;
    private int userPayId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_send_reward);
    }

    @Override
    public void init() {
        mContext = this;
        lhTvTitle.setText("赞赏红包");
        showPd();
        common_reward();
    }

    private CommonRewardRequest commonRewardRequest;

    //获取所有的省市区列表
    private void common_reward() {
        if (commonRewardRequest != null) {
            commonRewardRequest.cancel();
        }
        commonRewardRequest = new CommonRewardRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {

                if (((CommonResult) response).status == 1) {
                    String moneys = ((CommonResult) response).rewardMoney;
                    if (!TextUtils.isEmpty(moneys)) {
                        List<String> moneyList = Arrays.asList(moneys.split(","));
                        adapter = new SendRewardAdapter(mContext, moneyList);
                        gv.setAdapter(adapter);
                        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                tvPayMoney.setText(adapter.getItem(position));
                                adapter.setSelected(position);
                                if (!tvPay.isEnabled()) {
                                    money = Double.valueOf(adapter.getItem(position));
                                    tvPay.setEnabled(true);
                                    tvPay.setBackgroundColor(0xffe6bc7d);
                                }

                            }
                        });
                    }
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });

        sendJsonRequest(commonRewardRequest);
    }

    private UserRewardPayAddRequest rewardPayAddRequest;

    private void user_reward_pay_add() {
        try {
            showPd();

            if (rewardPayAddRequest != null) {
                rewardPayAddRequest.cancel();
            }
            UserRewardPayAddRequest.Input input = new UserRewardPayAddRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.toUserId = toUserId;
            input.money = money;
            input.convertJosn();

            rewardPayAddRequest = new UserRewardPayAddRequest(input, new ResponseListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dismissPd();
                    System.out.print(error);
                }

                @Override
                public void onResponse(Object response) {
                    int status = ((CommonResult) response).status;
                    if (status == 1) {
                        ORDER_SN = ((CommonResult) response).number;
                        userPayId = ((CommonResult) response).userPayId;
                        Intent intent = new Intent(mContext, ChoosePayTypeActivity.class);
                        intent.putExtra("consumeMoney", money);
                        intent.putExtra("order_sn", ORDER_SN);
                        intent.putExtra("userPayId", userPayId);
                        intent.putExtra("toUserId", toUserId);
                        intent.putExtra("merchantName", "赞赏红包");
                        intent.putExtra("merchantHead", "");
                        intent.putExtra("managerhead", "");
                        intent.putExtra("managername", "");
                        startActivityForResult(intent, 10001);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(rewardPayAddRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.tv_pay:
                user_reward_pay_add();
                break;
        }
    }
}
