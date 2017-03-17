package com.zemult.merchant.activity.slash;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.AppointmentDetailActivity;
import com.zemult.merchant.adapter.slash.ChooseReservationAdapter;
import com.zemult.merchant.adapter.slashfrgment.SendRewardAdapter;
import com.zemult.merchant.aip.common.CommonRewardRequest;
import com.zemult.merchant.aip.mine.UserMerchantPayAddRequest;
import com.zemult.merchant.aip.mine.UserReservationPayAddRequest;
import com.zemult.merchant.aip.reservation.UserReservationSaleListRequest;
import com.zemult.merchant.aip.slash.MerchantInfoRequest;
import com.zemult.merchant.aip.slash.UserInfoRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedGridView;
import com.zemult.merchant.view.FixedListView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Bind(R.id.cb_reward)
    CheckBox cbReward;
    @Bind(R.id.iv_reward)
    ImageView ivReward;
    private M_Merchant merchant;

    private M_Userinfo userinfo;
    int merchantId, userSaleId, reservationId;
    String reservationIds;
    public static final String MERCHANT_INFO = "merchantInfo";

    public static final String USER_INFO = "userInfo";
    public static final String MERCHANT_ID = "merchant_id";
    public static final String M_RESERVATION = "M_RESERVATION";
    // 商户订单号
    private String ORDER_SN = "", managerhead, managername, scanMoney;
    private int userPayId = 0;
    double money = 0, rewardMoney = 0;
    MerchantInfoRequest merchantInfoRequest;

    UserInfoRequest userInfoRequest;
    UserMerchantPayAddRequest userMerchantPayAddRequest;
    UserReservationSaleListRequest userReservationSaleListRequest;
    CommonRewardRequest commonRewardRequest;
    List<M_Reservation> reservationList = new ArrayList<>();

    ChooseReservationAdapter adapter;
    private SendRewardAdapter adapterReward;
    M_Reservation mReservation;

    Dialog alertDialog;
    List<M_Bill> moneyList = new ArrayList<M_Bill>();

    private Context mContext;

    private int selectPosition = 2; //选中的赞赏红包, 默认2, 金额6.66
    private String strRewardMoney = "";
    Set<Integer> selectidset=new HashSet<Integer>();

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
        mReservation = (M_Reservation) getIntent().getSerializableExtra(M_RESERVATION);
        reservationId = getIntent().getIntExtra("reservationId", 0);
        reservationIds = getIntent().getStringExtra("reservationIds");
        merchant = (M_Merchant) getIntent().getSerializableExtra(MERCHANT_INFO);
        userinfo = (M_Userinfo) getIntent().getSerializableExtra(USER_INFO);

        adapter = new ChooseReservationAdapter(mContext, reservationList, mReservation != null);
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
            if(!StringUtils.isBlank((userinfo.getHead()))){
                imageManager.loadCircleImage(userinfo.getHead(), ivHead);
            }
        }

    }

    private void initView() {
        lhTvTitle.setText("结账服务");

        EditFilter.CashFilter(etPaymoney, Constants.MAX_PAY);
        btnPay.setEnabled(false);
        btnPay.setBackgroundResource(R.drawable.next_bg_btn_select);
        etPaymoney.addTextChangedListener(watcher);
        if (!StringUtils.isEmpty(scanMoney)) {
            etPaymoney.setText(scanMoney);
        }

        alertDialog = new Dialog(this, R.style.MMTheme_DataSheet);

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

        adapter.setOnCheckClickListener(new ChooseReservationAdapter.OnCheckClickListener() {
            @Override
            public void onCheckClick(int position) {
                for (int i = 0; i < reservationList.size(); i++) {
                    if (i != position) {
                        reservationList.get(i).setChecked(false);
                    }else {
                        reservationList.get(position).setChecked(!reservationList.get(position).isChecked());
                    }
                }
                adapter.setData(reservationList, false);

            }
        });
    }

    private void getNetworkData() {
        if (mReservation != null) {
            List<M_Reservation> reservation = new ArrayList<>();
            reservation.add(mReservation);
            fillAdapter(reservation, false);
        } else {
            getReservationSaleList();
        }

        common_reward();
    }

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

                if (((APIM_PresentList) response).status == 1) {
                    if (((APIM_PresentList) response).moneyList.size()>0) {
                        moneyList =((APIM_PresentList) response).moneyList;
//                        strRewardMoney = moneyList.get(selectPosition);
//                        rewardMoney = Double.parseDouble(strRewardMoney);
                        double sumDoubleMoney = 0;
                        for(Integer selectidposition : selectidset){
                            sumDoubleMoney=sumDoubleMoney+Double.valueOf(moneyList.get(selectidposition).money);
                        }
                        BigDecimal b   =   new BigDecimal(sumDoubleMoney);
                        rewardMoney   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
                        cbReward.setText(String.format("赞赏红包%s元", rewardMoney));
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_PresentList) response).info);
                }
                dismissPd();
            }
        });

        sendJsonRequest(commonRewardRequest);
    }

    private void showDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_reward, null);
        FixedGridView gv = (FixedGridView) view.findViewById(R.id.gv);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);

        adapterReward = new SendRewardAdapter(mContext, moneyList);
        gv.setAdapter(adapterReward);

//        if (cbReward.isChecked()) {
//         adapterReward.setSelected(selectidset);
//        }
        adapterReward.setSelected(selectidset);

        alertDialog.setContentView(view);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectidset.clear();
                rewardMoney=0;
                cbReward.setText(String.format("赞赏红包%s元", rewardMoney));
                cbReward.setTextColor(getResources().getColor(R.color.font_black_999));
                cbReward.setChecked(false);
                alertDialog.dismiss();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectPosition = position;

                if(selectidset.contains(position)){
                    selectidset.remove(position);
                }else{
                    selectidset.add(position);
                }
                double sumDoubleMoney = 0;
                for(Integer selectidposition : selectidset){
                    sumDoubleMoney=sumDoubleMoney+Double.valueOf(adapterReward.getItem(selectidposition).money);
                }
                BigDecimal b   =   new BigDecimal(sumDoubleMoney);
                rewardMoney   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
                cbReward.setText(String.format("赞赏红包%s元", rewardMoney));

//                strRewardMoney = moneyList.get(selectPosition);
//                rewardMoney = Double.parseDouble(strRewardMoney);
                cbReward.setTextColor(getResources().getColor(R.color.bg_head_red));
                cbReward.setChecked(true);
                tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney() + rewardMoney));
                adapterReward.setSelected(selectidset);
            }
        });

        Window dialogWindow = alertDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialog_style);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
        dialogWindow.setAttributes(lp);
        alertDialog.show();
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
            input.consumeMoney = money + (cbReward.isChecked() ? rewardMoney : 0);
            input.money = money;
            if (StringUtils.isEmpty(selectReservations())) {
                input.reservationIds = "";
            } else {
                input.reservationIds = selectReservations();
            }
            input.rewardMoney = cbReward.isChecked() ? rewardMoney : 0;
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
                        intent.putExtra("consumeMoney", money + (cbReward.isChecked() ? rewardMoney : 0));
                        intent.putExtra("order_sn", ORDER_SN);
                        intent.putExtra("userPayId", userPayId);
                        intent.putExtra("merchantName", merchant.name);
                        intent.putExtra("merchantHead", merchant.head);
                        intent.putExtra("managerhead", managerhead);
                        intent.putExtra("managername", managername);

                        String imMessageTitle="";
                        String imMessageContent="";
                        for(int i:selectidset){
                            imMessageTitle=imMessageTitle+ moneyList.get(i).name+",";
                            imMessageContent=imMessageContent+ moneyList.get(i).name+moneyList.get(i).money+",";
                        }
                        if(imMessageTitle.indexOf(",")!=-1){
                            intent.putExtra("imMessageTitle", imMessageTitle.substring(0,imMessageTitle.length()-1));
                            intent.putExtra("imMessageContent", imMessageContent.substring(0,imMessageContent.length()-1));
                        }
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
            input.consumeMoney = money + (cbReward.isChecked() ? rewardMoney : 0);
            input.money = money;
            input.rewardMoney = rewardMoney;
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
                        intent.putExtra("consumeMoney", money + (cbReward.isChecked() ? rewardMoney : 0));
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
                    if(!StringUtils.isBlank(userinfo.getHead())){
                        imageManager.loadCircleImage(userinfo.getHead(), ivHead);
                    }
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
            list.get(0).setChecked(true);
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
                    if (getMoney() > 0) {
                        btnPay.setBackgroundResource(R.drawable.common_selector_btn);
                    }
                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney() + (cbReward.isChecked() ? rewardMoney : 0)));
                }
            } else {
                etPaymoney.setHint("输入实际的消费金额");
                tvFuhao.setVisibility(View.GONE);
                btnPay.setEnabled(false);
                btnPay.setBackgroundResource(R.drawable.next_bg_btn_select);
                tvMoneyRealpay.setText("￥" + Convert.getMoneyString(cbReward.isChecked() ? rewardMoney : 0));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_pay, R.id.iv_reward, R.id.cb_reward})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_reward:
                showDialog();
                break;
            case R.id.cb_reward:
                if (cbReward.isChecked()) {
                    cbReward.setTextColor(getResources().getColor(R.color.bg_head_red));
                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney() + rewardMoney));
                } else {
                    tvMoneyRealpay.setText("￥" + Convert.getMoneyString(getMoney()));
                    cbReward.setTextColor(getResources().getColor(R.color.font_black_999));
                }
                break;
            case R.id.btn_pay:
                money = getMoney();
                if (money > 0) {
                    userTaskPayRequest();
                }

                break;
        }
    }

    private String selectReservations() {
        String reservationIds = "";
        if (mReservation != null) {
            reservationIds = mReservation.reservationId + "";
        } else {
            for (int i = 0; i < flv.getChildCount(); i++) {
                LinearLayout ll = (LinearLayout) flv.getChildAt(i);// 获得子级
                CheckBox cb = (CheckBox) ll.findViewById(R.id.cb);// 从子级中获得控件
                if (cb.isChecked()) {
                    reservationIds = reservationIds + adapter.getItem(i).reservationId + ",";
                }
            }
            if (reservationIds.length() > 0) {
                reservationIds = reservationIds.substring(0, reservationIds.length() - 1);
            }
        }

        return reservationIds;
    }

    private double getMoney() {
        double result = 0;
        if (!StringUtils.isBlank(etPaymoney.getText().toString())) {
            result = Double.parseDouble(etPaymoney.getText().toString());
        }
        return result;
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
