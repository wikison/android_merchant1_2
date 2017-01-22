package com.zemult.merchant.activity.slash;

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
import com.zemult.merchant.aip.mine.UserPresentPayAddRequest;
import com.zemult.merchant.aip.slash.CommonSysPresentListRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Present;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

public class SendPresentActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_pay_money)
    TextView tvPayMoney;
    @Bind(R.id.tv_pay)
    TextView tvPay;
    @Bind(R.id.rv)
    RecyclerView rv;
    private Context mContext;

    private int toUserId = 0;
    private int presentId = 0;
    private double money = 0;
    String ORDER_SN;
    int userPayId;

    UserPresentPayAddRequest userPresentPayAddRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_send_present);
    }

    @Override
    public void init() {
        mContext = this;
        toUserId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        lhTvTitle.setText("赞赏礼物");
        tvPay.setEnabled(false);
        common_syspresentList();
    }

    private CommonSysPresentListRequest presentRequest;

    private void common_syspresentList() {
        if (presentRequest != null) {
            presentRequest.cancel();
        }

        presentRequest = new CommonSysPresentListRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PresentList) response).status == 1) {
                    setPresent(((APIM_PresentList) response).sysPresentList);
                } else {
                    ToastUtil.showMessage(((APIM_PresentList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(presentRequest);
    }

    private void setPresent(final List<M_Present> userPresentList) {
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
        final PresentAdapter adapter = new PresentAdapter(mContext, userPresentList);
        adapter.fromSend();
        rv.setAdapter(adapter);
        adapter.setOnItemClickLitener(new PresentAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(double price, int presentId) {
                tvPayMoney.setText(Convert.getMoneyString(price));
                if (price == 0) {
                    money = 0;
                    tvPay.setEnabled(false);
                    tvPay.setBackgroundColor(0xff999999);
                } else {
                    SendPresentActivity.this.presentId = presentId;
                    money = price;
                    tvPay.setEnabled(true);
                    tvPay.setBackgroundColor(0xffe6bc7d);
                }

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if (money > 0) {
                    sendPresentPayRequest();
                }
                break;
        }
    }


    private void sendPresentPayRequest() {
        try {
            showPd();

            if (userPresentPayAddRequest != null) {
                userPresentPayAddRequest.cancel();
            }
            UserPresentPayAddRequest.Input input = new UserPresentPayAddRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.toUserId =
                    input.presentId = presentId;
            input.money = money;
            input.convertJosn();

            userPresentPayAddRequest = new UserPresentPayAddRequest(input, new ResponseListener() {
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
                        Intent intent = new Intent(SendPresentActivity.this, ChoosePayTypeActivity.class);
                        intent.putExtra("consumeMoney", money);
                        intent.putExtra("order_sn", ORDER_SN);
                        intent.putExtra("userPayId", userPayId);
                        intent.putExtra("merchantName", "购买礼物");
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
            sendJsonRequest(userPresentPayAddRequest);
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
}
