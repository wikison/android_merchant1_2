package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.Convert;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.QrImageUtil;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wikison on 2016/6/21.
 */
public class MyQrActivity extends BaseActivity {

    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.set_btn)
    Button setBtn;
    String price = "";
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
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.iv_merchant_head)
    ImageView ivMerchantHead;
    @Bind(R.id.tv_merchant_name)
    TextView tvMerchantName;
    @Bind(R.id.ll_merchant_head)
    LinearLayout llMerchantHead;
    @Bind(R.id.iv_qr)
    ImageView ivQr;
    @Bind(R.id.tv_hint)
    TextView tvHint;

    int userSaleId, merchantId;
    String merchantHead, merchantName;
    Bitmap bitmap;
    String strFrom = "";
    String qrInfo;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_qr);
    }

    @Override
    public void init() {
        initData();
        initView();

    }


    private void initData() {
        strFrom = getIntent().getStringExtra("from");
        userSaleId = getIntent().getIntExtra("userSaleId", 0);
        merchantId = getIntent().getIntExtra("merchantId", 0);
        merchantHead = getIntent().getStringExtra("merchantHead");
        merchantName = getIntent().getStringExtra("merchantName");
    }

    private void initView() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        imageManager.loadCircleImage(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
        tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        tvLevel.setText(Convert.getExperienceText(SlashHelper.userManager().getUserinfo().getExperience()));
        if ("MyInfoSet".equals(strFrom)) {
            lhTvTitle.setText("我的二维码");
            qrInfo = "userId=" + SlashHelper.userManager().getUserId();
            bitmap = QrImageUtil.createQRImage(Constants.QR_USER_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));
            tvHint.setVisibility(View.VISIBLE);
        } else if ("SaleManage".equals(strFrom)) {
            llMerchantHead.setVisibility(View.VISIBLE);
            setBtn.setVisibility(View.VISIBLE);
            lhTvTitle.setText("找我买单");
            moneyTv.setVisibility(View.VISIBLE);
            tvMerchantName.setText(merchantName);
            tvHint.setVisibility(View.GONE);
            qrInfo = "merchantId=" + merchantId + "&userId=" + userSaleId + "&price=" + price;
            bitmap = QrImageUtil.createQRImage(Constants.QR_PAY_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));
        }
        if (bitmap != null)
            ivQr.setImageBitmap(bitmap);
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.set_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.set_btn:
                if (setBtn.getText().equals("设置金额")) {
                    Intent intent = new Intent(this, SettingMoneyActivity.class);
                    startActivityForResult(intent, 111);
                } else {
                    moneyTv.setText("");
                    price = "0";
                    qrInfo = "merchantId=" + merchantId + "&userId=" + userSaleId + "&price=" + price;
                    bitmap = QrImageUtil.createQRImage(Constants.QR_PAY_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                            DensityUtil.dip2px(this, 240));
                    if (bitmap != null)
                        ivQr.setImageBitmap(bitmap);
                    setBtn.setText("设置金额");
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK) {
            String result = data.getExtras().getString("result");
            price = result;
            moneyTv.setText("￥  " + price);
            qrInfo = "merchantId=" + merchantId + "&userId=" + userSaleId + "&price=" + price;
            bitmap = QrImageUtil.createQRImage(Constants.QR_PAY_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));
            if (bitmap != null)
                ivQr.setImageBitmap(bitmap);
            setBtn.setText("清除金额");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
