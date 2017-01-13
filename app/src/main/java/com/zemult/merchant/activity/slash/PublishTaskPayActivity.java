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
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.EditFilter;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/8/9.
 */
public class PublishTaskPayActivity extends BaseActivity {
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
    @Bind(R.id.tv_trade_shop)
    TextView tvTradeShop;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.tv_trade_money)
    TextView tvTradeMoney;
    @Bind(R.id.et_task_pay_money)
    EditText etTaskPayMoney;

    Context mContext;
    String requestType;
    String merchantName;
    int taskType;
    String taskName;
    double payMoney;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_publish_task_pay);
    }

    @Override
    public void init() {
        initData();
        initListener();

    }

    private void initData() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("交易设置");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("完成");

        EditFilter.CashFilter(etTaskPayMoney, 10000);

        requestType = getIntent().getStringExtra("requesttype");
        taskType = getIntent().getIntExtra("publish_type", 3);
        taskName = getIntent().getStringExtra("publish_type_name");
        merchantName = getIntent().getStringExtra("merchant_name");
        payMoney = getIntent().getDoubleExtra("pay_money", 0);
        tvMerchantName.setText(merchantName);
        if (payMoney > 0)
            etTaskPayMoney.setText(payMoney + "");

    }

    private void initListener() {
        etTaskPayMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etTaskPayMoney.setText(s);
                        etTaskPayMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etTaskPayMoney.setText(s);
                    etTaskPayMoney.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etTaskPayMoney.setText(s.subSequence(0, 1));
                        etTaskPayMoney.setSelection(1);
                        return;
                    }
                }

                if (s.toString().length() >= 1 && !s.toString().endsWith(".") && Double.valueOf(s.toString()) > 0) {
                    if (Double.valueOf(s.toString()) < 1 && AppApplication.ISDEBUG == false) {
                        etTaskPayMoney.setError("消费金额最小设置为1");
                        return;
                    }
                    payMoney = Double.valueOf(etTaskPayMoney.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;

            case R.id.tv_right:
                if (isValid()) {
                    if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_TYPE)) {
                        Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_TYPE);
                        intent.putExtra("publish_type", taskType);
                        intent.putExtra("publish_type_name", taskName);
                        intent.putExtra("pay_money", payMoney);
                        setResult(RESULT_OK, intent);
                        sendBroadcast(intent);
                        finish();
                    }
                } else {
                    //ToastUtil.showMessage("请设置消费金额");
                }
                break;
        }
    }

    private boolean isValid() {
        try {
            payMoney = Double.valueOf(etTaskPayMoney.getText().toString());
            if (payMoney < 1) {
                etTaskPayMoney.setError("消费金额最小设置为1");
                return false;
            }
        } catch (Exception e) {

        }
        return true;
    }

}
