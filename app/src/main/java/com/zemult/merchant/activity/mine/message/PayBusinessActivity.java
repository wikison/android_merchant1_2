package com.zemult.merchant.activity.mine.message;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundRelativeLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.task.TaskRecordInfoRequest;
import com.zemult.merchant.alipay.taskpay.ChoosePayTypeActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.EditFilter;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.common.MMAlert;

import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.StringUtils;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class PayBusinessActivity extends BaseActivity {

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
    @Bind(R.id.tv_paymoney)
    TextView tvPaymoney;
    @Bind(R.id.tv_discount)
    TextView tvDiscount;
    @Bind(R.id.tv_coupon)
    TextView tvCoupon;

    @Bind(R.id.rel_coupon)
    RoundRelativeLayout relCoupon;
    @Bind(R.id.rel_zk)
    RoundRelativeLayout relZk;
    @Bind(R.id.busidetai_call_iv)
    ImageView busidetaiCallIv;



    double discount,commissionDiscount;
    TaskRecordInfoRequest taskRecordInfoRequest;


    String merchantName, merchantPic, merchantAddress, merchantTel;
    int merchantId, taskIndustryRecordId,isVoucher,voucherUserId;
    double zkvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskIndustryRecordId=getIntent().getIntExtra("taskIndustryRecordId",-1);
        if(taskIndustryRecordId==-1){
            relZk.setVisibility(View.GONE);

            merchantName = getIntent().getStringExtra("merchantName");
            merchantPic =  getIntent().getStringExtra("merchantPic");
            merchantAddress = getIntent().getStringExtra("merchantAddress");
            merchantId = getIntent().getIntExtra("merchantId",0);
            merchantTel =  getIntent().getStringExtra("merchantTel");
            commissionDiscount=0;
            tvBusinessaddress.setText(merchantAddress);
            tvBusinessname.setText(merchantName);
            imageManager.loadUrlImage(merchantPic, ivBusiness);

            discount=10;
        }
        else{
            taskRecordInfoRequest( taskIndustryRecordId);
        }

    }


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_task_pay_info);
    }

    @Override
    public void init() {
        lhTvTitle.setText("交易支付");
        EditFilter.CashFilter(etPaymoney, 10000);
        etPaymoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    zkvalue=Double.parseDouble(s.toString())*(discount/(double)10);
                    tvPaymoney.setText(String.format("%.2f", zkvalue)+"元");
                }
                else{
                    tvPaymoney.setText("0元");
                }
                isVoucher=0;
                voucherUserId=0;
                tvCoupon.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private void taskRecordInfoRequest(int taskIndustryRecordId) {
        if (taskRecordInfoRequest != null) {
            taskRecordInfoRequest.cancel();
        }
        TaskRecordInfoRequest.Input input = new TaskRecordInfoRequest.Input();

        input.taskIndustryRecordId = taskIndustryRecordId;
        input.convertJosn();
        showUncanclePd();
        taskRecordInfoRequest = new TaskRecordInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                ToastUtils.show(PayBusinessActivity.this, "服务器错误,请重试");
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryInfo) response).status == 1) {
                    M_Task mtask=((APIM_TaskIndustryInfo) response).taskIndustryRecordInfo;
                    merchantName = mtask.merchantName;
                    merchantPic = mtask.merchantPic;
                    merchantAddress = mtask.merchantAddress;
                    merchantId =mtask.merchantId;
                    commissionDiscount=mtask.commissionDiscount;
                    merchantTel = mtask.merchantTel;
                    tvBusinessaddress.setText(mtask.merchantAddress);
                    tvBusinessname.setText(mtask.merchantName);

                    if(TextUtils.isEmpty(mtask.merchantPic)){
                        int number = new Random().nextInt(10) + 1;
                        String address = "http://xiegang.oss-cn-shanghai.aliyuncs.com/merchant/covers/changjing"+number+".jpg";
                        imageManager.loadUrlImage(address, ivBusiness);//图片加载

                    }else{
                        imageManager.loadUrlImage(mtask.merchantPic, ivBusiness);
                    }

                    imageManager.loadUrlImage(mtask.merchantHead, ivBusiness);
                    tvDiscount.setText(mtask.discount+"折");
                    discount=mtask.discount;

                } else {
                    ToastUtils.show(PayBusinessActivity.this, ((APIM_TaskIndustryInfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(taskRecordInfoRequest);

    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_taskpay,R.id.rel_coupon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.busidetai_call_iv:
                if(merchantTel.indexOf(";")!=-1){
                    call();
                }
                else{
                    ToastUtil.showMessage("暂无电话");
                }

                break;
            case R.id.btn_taskpay:
                if (StringUtils.isEmpty(etPaymoney.getText().toString())) {
                    etPaymoney.setError("请输入金额");
                    return;
                }
                if(taskIndustryRecordId==-1){
                    Intent intent = new Intent(PayBusinessActivity.this, ChoosePayTypeActivity.class);
                    intent.putExtra("consumeMoney", Double.parseDouble(etPaymoney.getText().toString()));
                    intent.putExtra("merchantId", merchantId);
                    intent.putExtra("merchantTel", merchantTel);
                    intent.putExtra("merchantName", merchantName);
                    intent.putExtra("buyType", 0);//买单类型(0:直接通过商户买单,1:通过营销经理的探索记录买单)
                    intent.putExtra("merchantAddress", merchantAddress);
                    intent.putExtra("money", Double.parseDouble(tvPaymoney.getText().toString().replace("元","")));
                    intent.putExtra("isVoucher", isVoucher);
                    intent.putExtra("voucherUserId", voucherUserId);//否
                    startActivityForResult(intent, 10000);
                }
                else{
                    Intent intent = new Intent(PayBusinessActivity.this, ChoosePayTypeActivity.class);
                    intent.putExtra("consumeMoney", Double.parseDouble(etPaymoney.getText().toString()));
                    intent.putExtra("merchantId", merchantId);
                    intent.putExtra("merchantTel", merchantTel);
                    intent.putExtra("merchantName", merchantName);
                    intent.putExtra("buyType", 1);//买单类型(0:直接通过商户买单,1:通过营销经理的探索记录买单)
                    intent.putExtra("merchantAddress", merchantAddress);
                    intent.putExtra("taskIndustryRecordId", taskIndustryRecordId);//否
                    intent.putExtra("money", Double.parseDouble(tvPaymoney.getText().toString().replace("元","")));
                    intent.putExtra("isVoucher", isVoucher);
                    intent.putExtra("voucherUserId", voucherUserId);//否
                    intent.putExtra("commissionDiscount", commissionDiscount);//否
                    startActivityForResult(intent, 10000);
                }

                break;
            case R.id.rel_coupon:
                if(tvPaymoney.getText().toString().length()>0){
                    Intent intent2 = new Intent(PayBusinessActivity.this, PayCouponListActivity.class);
                    intent2.putExtra("merchantId",merchantId);
                    intent2.putExtra("paymoney",Double.parseDouble(tvPaymoney.getText().toString().replace("元","")));
                    startActivityForResult(intent2,10001);
                }
                else{
                    ToastUtil.showMessage("请输入支付金额");
                }

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10000 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
        if (requestCode == 10001 && resultCode == RESULT_OK) {
            isVoucher=data.getIntExtra("isVoucher",0);
            voucherUserId=data.getIntExtra("voucherUserId",0);
            double couponmoney=data.getDoubleExtra("couponmoney",0);
            if(couponmoney==0){
                tvCoupon.setText("");
            }
            else{
                tvCoupon.setText(couponmoney+"元");
            }

            if(zkvalue-couponmoney<=0){
                tvPaymoney.setText(0+"元");
            }
           else{
                tvPaymoney.setText(String.format("%.2f", (zkvalue-couponmoney))+"元");
            }
        }
    }

    /**
     * 拨打电话
     */
    private void call() {
        final String[] phoneNoArray=merchantTel.split(";");
        MMAlert.showAlert(this, null, phoneNoArray, null,
                new MMAlert.OnAlertSelectId() {

                    @Override
                    public void onClick(int whichButton) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + phoneNoArray[whichButton]);
                        intent.setData(data);
                        startActivity(intent);
                    }
                });
    }
}
