package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.AppointmentDetailActivity;
import com.zemult.merchant.adapter.slash.ChooseReservationAdapter;
import com.zemult.merchant.aip.mine.UserMerchantPayAddRequest;
import com.zemult.merchant.aip.mine.UserReservationPayAddRequest;
import com.zemult.merchant.aip.reservation.UserReservationSaleListRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class FindPayActivity extends BaseActivity {

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
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.tv_merchant)
    TextView tvMerchant;
    @Bind(R.id.et_paymoney)
    EditText etPaymoney;
    @Bind(R.id.tv_money_realpay)
    TextView tvMoneyRealpay;
    @Bind(R.id.btn_pay)
    Button btnPay;
    @Bind(R.id.tv_fuhao)
    TextView tvFuhao;
    @Bind(R.id.tv_title_name)
    TextView tvTitleName;
    @Bind(R.id.tv_title_merchant)
    TextView tvTitleMerchant;
    @Bind(R.id.flv)
    FixedListView flv;
    @Bind(R.id.bsv_container)
    BounceScrollView bsvContainer;

    private M_Merchant merchant;
    private M_Userinfo userinfo;
    int merchantId, userSaleId, reservationId;
    String reservationIds;

    public static final String MERCHANT_INFO = "merchantInfo";
    public static final String USER_INFO = "userInfo";
    public static final String MERCHANT_ID = "merchant_id";
    // 商户订单号
    private String ORDER_SN = "", managerhead, managername, scanMoney;
    private int userPayId = 0;
    double paymoney = 0, truepaymoney = 0;

    MerchantInfoRequest merchantInfoRequest;
    UserInfoRequest userInfoRequest;
    UserMerchantPayAddRequest userMerchantPayAddRequest;
    UserReservationSaleListRequest userReservationSaleListRequest;

    List<M_Reservation> reservationList = new ArrayList<>();
    ChooseReservationAdapter adapter;
    M_Reservation mReservation;


    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_pay);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getNetworkData();
    }

    private void initData() {
        mContext = this;

        merchantId = getIntent().getIntExtra("merchantId", 0);
        userSaleId = getIntent().getIntExtra("userSaleId", 0);
        scanMoney = getIntent().getStringExtra("scanMoney");
        mReservation = (M_Reservation) getIntent().getSerializableExtra("M_RESERVATION");
        reservationId = getIntent().getIntExtra("reservationId", 0);
        reservationIds = getIntent().getStringExtra("reservationIds");
        merchant = (M_Merchant) getIntent().getSerializableExtra(MERCHANT_INFO);
        userinfo = (M_Userinfo) getIntent().getSerializableExtra(USER_INFO);

        adapter = new ChooseReservationAdapter(mContext, reservationList);
        flv.setAdapter(adapter);

        if (merchant == null) {
            merchant_info(merchantId);
        } else {
            tvMerchant.setText(merchant.name);
        }

        if (userinfo == null) {
            getUserInfo(userSaleId);
        } else {
            managerhead = userinfo.getHead();
            managername = userinfo.getName();
            tvName.setText(userinfo.getName());
            imageManager.loadCircleImage(userinfo.getHead(), ivHead);
        }

    }

    private void initView() {
        lhTvTitle.setText("结账服务");

        EditFilter.CashFilter(etPaymoney, 10000);
        btnPay.setEnabled(false);
        btnPay.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPaymoney.addTextChangedListener(watcher);
        if (!StringUtils.isEmpty(scanMoney)) {
            etPaymoney.setText(scanMoney);
        }

    }

    private void initListener() {
        adapter.setOnAllClickListener(new ChooseReservationAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                Intent intent = new Intent(mContext, AppointmentDetailActivity.class);
                intent.putExtra(AppointmentDetailActivity.INTENT_TYPE, 0);
                intent.putExtra(AppointmentDetailActivity.INTENT_RESERVATIONID, adapter.getItem(position).reservationId + "");
                startActivity(intent);
            }
        });
    }

    private void getNetworkData() {
        if (mReservation != null) {
            reservationList.clear();
            reservationList.add(mReservation);
            fillAdapter(reservationList, false);
        } else {
            getReservationSaleList();
        }
    }


    private void userTaskPayRequest() {
        try {
            showPd();

            if (userMerchantPayAddRequest != null) {
                userMerchantPayAddRequest.cancel();
            }
            UserMerchantPayAddRequest.Input input = new UserMerchantPayAddRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.merchantId = merchantId;
            input.saleUserId = userSaleId;
            input.consumeMoney = truepaymoney;
            input.money = truepaymoney;
            if (StringUtils.isEmpty(reservationIds)) {
                input.reservationIds = "";
            } else {
                input.reservationIds = reservationIds;
            }
            input.convertJosn();

            userMerchantPayAddRequest = new UserMerchantPayAddRequest(input, new ResponseListener() {
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
                        Intent intent = new Intent(FindPayActivity.this, ChoosePayTypeActivity.class);
                        intent.putExtra("consumeMoney", truepaymoney);
                        intent.putExtra("order_sn", ORDER_SN);
                        intent.putExtra("userPayId", userPayId);
                        intent.putExtra("merchantName", merchant.name);
                        intent.putExtra("merchantHead", merchant.head);
                        intent.putExtra("managerhead", managerhead);
                        intent.putExtra("managername", managername);
                        startActivityForResult(intent, 10000);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(userMerchantPayAddRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    private UserReservationPayAddRequest reservationPayAddRequest;

    private void user_reservation_pay_add() {
        try {
            showPd();

            if (reservationPayAddRequest != null) {
                reservationPayAddRequest.cancel();
            }
            UserReservationPayAddRequest.Input input = new UserReservationPayAddRequest.Input();
            input.userId = SlashHelper.userManager().getUserId();
            input.reservationId = mReservation.reservationId;
            input.consumeMoney = truepaymoney;
            input.money = truepaymoney;
            input.convertJosn();

            reservationPayAddRequest = new UserReservationPayAddRequest(input, new ResponseListener() {
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
                        Intent intent = new Intent(FindPayActivity.this, ChoosePayTypeActivity.class);
                        intent.putExtra("consumeMoney", truepaymoney);
                        intent.putExtra("order_sn", ORDER_SN);
                        intent.putExtra("userPayId", userPayId);
                        intent.putExtra("merchantName", merchant.name);
                        intent.putExtra("merchantHead", merchant.head);
                        intent.putExtra("managerhead", managerhead);
                        intent.putExtra("managername", managername);
                        startActivityForResult(intent, 10000);
                    } else {
                        ToastUtil.showMessage(((CommonResult) response).info);
                    }
                    dismissPd();
                }
            });
            sendJsonRequest(reservationPayAddRequest);
        } catch (Exception e) {
            dismissPd();
        }
    }

    //商家详情
    private void merchant_info(int merchantId) {
        showPd();
        if (merchantInfoRequest != null) {
            merchantInfoRequest.cancel();
        }


        MerchantInfoRequest.Input input = new MerchantInfoRequest.Input();
        input.merchantId = merchantId;

        input.convertJosn();
        merchantInfoRequest = new MerchantInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    merchant = ((APIM_MerchantGetinfo) response).merchant;
                    tvMerchant.setText("" + merchant.name);
                } else {
                    ToastUtils.show(FindPayActivity.this, ((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantInfoRequest);
    }

    private void getUserInfo(int userSaleId) {
        if (userInfoRequest != null) {
            userInfoRequest.cancel();
        }
        UserInfoRequest.Input input = new UserInfoRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.userId = userSaleId;
        input.convertJosn();
        userInfoRequest = new UserInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {
                    userinfo = ((APIM_UserLogin) response).UserInfo;
                    tvName.setText(userinfo.getName());
                    imageManager.loadCircleImage(userinfo.getHead(), ivHead);
                    managerhead = userinfo.getHead();
                    managername = userinfo.getName();
                } else {
                    ToastUtils.show(FindPayActivity.this, ((APIM_UserLogin) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userInfoRequest);
    }

    private void getReservationSaleList() {
        if (userReservationSaleListRequest != null) {
            userReservationSaleListRequest.cancel();
        }
        UserReservationSaleListRequest.Input input = new UserReservationSaleListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.saleUserId = userSaleId;
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = Constants.ROWS;
        input.convertJosn();
        userReservationSaleListRequest = new UserReservationSaleListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserReservationList) response).status == 1) {
                    reservationList = ((APIM_UserReservationList) response).reservationList;

                    fillAdapter(reservationList, false);
                } else {
                    ToastUtil.showMessage(((APIM_UserReservationList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userReservationSaleListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Reservation> list, boolean isLoadMore) {
        if (list == null || list.size() == 0) {

        } else {
            adapter.setData(list, isLoadMore);
        }
        bsvContainer.post(new Runnable() {
            @Override
            public void run() {

                bsvContainer.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().length() > 0) {
                if (etPaymoney.getText().toString().length() > 0) {
                    etPaymoney.setHint("");
                    btnPay.setEnabled(true);
                    btnPay.setBackgroundResource(R.drawable.common_selector_btn);
                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(Double.parseDouble(etPaymoney.getText().toString())));
                }
            } else {
                etPaymoney.setHint("请输入支付金额");
                tvFuhao.setVisibility(View.GONE);
                btnPay.setEnabled(false);
                btnPay.setBackgroundResource(R.drawable.next_bg_btn_select);
                tvMoneyRealpay.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_pay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.btn_pay:
                truepaymoney = Double.parseDouble(etPaymoney.getText().toString());
                if (truepaymoney > 0) {
                    if (mReservation != null)
                        user_reservation_pay_add();
                    else
                        userTaskPayRequest();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            onBackPressed();
        }
    }
}
