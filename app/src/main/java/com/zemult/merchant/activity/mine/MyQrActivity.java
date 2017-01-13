package com.zemult.merchant.activity.mine;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.QrImageUtil;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by wikison on 2016/6/21.
 */
public class MyQrActivity extends BaseActivity {
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
    @Bind(R.id.ll_name)
    LinearLayout llName;
    @Bind(R.id.iv_qr)
    ImageView ivQr;

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
        if ("MyInfoSet".equals(strFrom)) {
            lhTvTitle.setText("我的二维码");
            imageManager.loadCircleImage(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
            tvName.setText(SlashHelper.userManager().getUserinfo().getName());
            qrInfo = "userId=" + SlashHelper.userManager().getUserId();
            bitmap = QrImageUtil.createQRImage(Constants.QR_USER_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));
        } else if ("SaleManage".equals(strFrom)) {
            lhTvTitle.setText("找我买单");
            imageManager.loadCircleImage(merchantHead, ivHead);
            tvName.setText(merchantName);
            qrInfo = "merchantId=" + merchantId + "&userId=" + userSaleId;

            bitmap = QrImageUtil.createQRImage(Constants.QR_PAY_PREFIX + qrInfo, DensityUtil.dip2px(this, 240),
                    DensityUtil.dip2px(this, 240));
        }

        if (bitmap != null)
            ivQr.setImageBitmap(bitmap);
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
