package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.slash.MerchantInfoRequest2;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import zema.volley.network.ResponseListener;

/**
 * 场景入驻（已填-已通过）
 *
 * @author djy
 * @time 2016/12/1 10:51
 */
public class MerchantPassActivity extends BaseActivity {
    public static final String INTENT_MERCHANTID = "merchantId";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.tv_fullname)
    TextView tvFullname;
    @Bind(R.id.tv_classify)
    TextView tvClassify;
    @Bind(R.id.tv_area)
    TextView tvArea;
    @Bind(R.id.tv_address)
    TextView tvAddress;
    @Bind(R.id.textView9)
    TextView textView9;
    @Bind(R.id.rl_certification)
    LinearLayout rlCertification;
    @Bind(R.id.tv_uploadimage)
    TextView tvUploadimage;
    @Bind(R.id.tv_yongjin)
    TextView tvYongjin;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.tv_tixian)
    TextView tvTixian;
    @Bind(R.id.ll_tixian)
    LinearLayout llTixian;
    private int merchantId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_pass);
    }

    @Override
    public void init() {
        lhTvTitle.setText("商家入驻");
        merchantId = getIntent().getIntExtra(INTENT_MERCHANTID, -1);
        showPd();
        my_merchant_getinfo();
    }


    /**
     * 获取自己的 商家(场景)详情(请求)
     */
    private MerchantInfoRequest2 myMerchantGetinfoRequest;

    private void my_merchant_getinfo() {
        showPd();
        if (myMerchantGetinfoRequest != null) {
            myMerchantGetinfoRequest.cancel();
        }

        MerchantInfoRequest2.Input input = new MerchantInfoRequest2.Input();
        input.merchantId = merchantId;
        input.userId = SlashHelper.userManager().getUserId();

        input.convertJosn();
        myMerchantGetinfoRequest = new MerchantInfoRequest2(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetinfo) response).status == 1) {
                    M_Merchant merchant = ((APIM_MerchantGetinfo) response).merchant;

                    tvFullname.setText(merchant.name);
                    tvClassify.setText(merchant.industryName);
                    tvArea.setText(merchant.provinceName + " " + merchant.cityName + " " + merchant.areaName);
                    tvAddress.setText(merchant.address);
                    tvPhone.setText(merchant.tel);
                    tvYongjin.setText(merchant.commissionDiscount + "");
                    tvTixian.setText(merchant.moneyType == 0 ? "银行卡" : "支付宝");
                } else {
                    ToastUtil.showMessage(((APIM_MerchantGetinfo) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(myMerchantGetinfoRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
