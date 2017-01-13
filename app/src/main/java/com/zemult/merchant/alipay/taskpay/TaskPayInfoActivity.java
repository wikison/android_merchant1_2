package com.zemult.merchant.alipay.taskpay;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.EditFilter;
import com.taobao.av.util.StringUtil;

import butterknife.Bind;
import butterknife.OnClick;

public class TaskPayInfoActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_business)
    ImageView ivBusiness;
    @Bind(R.id.tv_businessname)
    TextView tvBusinessname;
    @Bind(R.id.tv_businessaddress)
    TextView tvBusinessaddress;
    @Bind(R.id.et_paymoney)
    EditText etPaymoney;
    @Bind(R.id.btn_taskpay)
    Button btnTaskpay;
    String merchantName, merchantHead, merchantAddress, merchantTel;
    int merchantId, taskIndustryRecordId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pay_business);
    }

    @Override
    public void init() {
        lhTvTitle.setText("交易支付");
        merchantName = getIntent().getStringExtra("merchantName");
        merchantHead = getIntent().getStringExtra("merchantHead");
        merchantAddress = getIntent().getStringExtra("merchantAddress");
        merchantId = getIntent().getIntExtra("merchantId", 0);
        merchantTel = getIntent().getStringExtra("merchantTel");
        taskIndustryRecordId = getIntent().getIntExtra("taskIndustryRecordId", 0);

        tvBusinessaddress.setText(merchantAddress);
        tvBusinessname.setText(merchantName);
        imageManager.loadCircleImage(merchantHead, ivBusiness);

        EditFilter.CashFilter(etPaymoney, 10000);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_taskpay})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_taskpay:
                if (StringUtil.isEmpty(etPaymoney.getText().toString())) {
                    etPaymoney.setError("请输入金额");
                    return;
                }
                Intent intent = new Intent(TaskPayInfoActivity.this, ChoosePayTypeActivity.class);
                intent.putExtra("paymoney", Double.parseDouble(etPaymoney.getText().toString()));
                intent.putExtra("merchantId", merchantId);
                intent.putExtra("merchantTel", merchantTel);
                intent.putExtra("merchantName", merchantName);
                intent.putExtra("merchantAddress", merchantAddress);
                intent.putExtra("taskIndustryRecordId", taskIndustryRecordId);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }


}
