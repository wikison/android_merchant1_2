package com.zemult.merchant.activity.mine;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.MerchantBillInfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;

import butterknife.Bind;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/8.
 */
public class MerchantBillInfoActivity extends BaseActivity {

    public static final String INTENT_TYPE = "type";
    public static final String INTENT_BILLID = "billid";
    int billId, type;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.info_type)
    TextView infoType;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_dealnote)
    TextView tvDealnote;
    @Bind(R.id.rel_deal)
    RelativeLayout relDeal;
    @Bind(R.id.tv_ordernote)
    TextView tvOrdernote;
    @Bind(R.id.rel_ordernumber)
    RelativeLayout relOrdernumber;
    @Bind(R.id.tv_createtimenote)
    TextView tvCreatetimenote;
    @Bind(R.id.rel_createtime)
    RelativeLayout relCreatetime;
    @Bind(R.id.tv_lable3)
    TextView tvLable3;
    MerchantBillInfoRequest merchantBillInfoRequest;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchantbillinfo);
    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("账单详情");
        billId = getIntent().getIntExtra(INTENT_BILLID, 0);
        Log.i("sunjian", "billid" + billId);
        type = getIntent().getIntExtra(INTENT_TYPE, 0);
        Log.i("sunjian", "type为" + type);

        if (type == 1) {
            relDeal.setVisibility(View.GONE);
            relOrdernumber.setVisibility(View.GONE);
            tvLable3.setText("时间");
        }
        showPd();
        merchantBillInfo();
    }


    private void merchantBillInfo() {

        if (merchantBillInfoRequest != null) {
            merchantBillInfoRequest.cancel();
        }
        MerchantBillInfoRequest.Input input = new MerchantBillInfoRequest.Input();
        input.billId = billId;//商户的账单id
        input.convertJosn();
        merchantBillInfoRequest = new MerchantBillInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserBillInfo) response).status == 1) {


                    if (((APIM_UserBillInfo) response).billInfo.type == 0) {
                        infoType.setText("交易");
                    } else {
                        infoType.setText("提现");
                    }

                    if (((APIM_UserBillInfo) response).billInfo.inCome == 0) {//(0:收入,1:支出)
                        tvMoney.setText("+" + ((APIM_UserBillInfo) response).billInfo.money);
                    } else {
                        tvMoney.setText("-" + ((APIM_UserBillInfo) response).billInfo.money);
                    }

                    tvDealnote.setText(((APIM_UserBillInfo) response).billInfo.note);
                    tvOrdernote.setText(((APIM_UserBillInfo) response).billInfo.number);

                    tvCreatetimenote.setText(((APIM_UserBillInfo) response).billInfo.createtime);

                }

            }
        });

        sendJsonRequest(merchantBillInfoRequest);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }


}
