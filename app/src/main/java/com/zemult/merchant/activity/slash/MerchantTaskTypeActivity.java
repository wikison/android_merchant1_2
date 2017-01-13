package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Wikison on 2016/10/19.
 * 商家任务类型选择
 */

public class MerchantTaskTypeActivity extends BaseActivity {
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
    @Bind(R.id.tv_merchant_type_buy)
    TextView tvMerchantTypeBuy;
    @Bind(R.id.tv_merchant_type_normal)
    TextView tvMerchantTypeNormal;

    int merchantId;
    String sMerchantName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_type);
    }

    @Override
    public void init() {
        lhTvTitle.setText("选择探索类型");
        merchantId = getIntent().getIntExtra("merchantId", 0);
        sMerchantName = getIntent().getStringExtra("merchantName");
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.tv_merchant_type_buy, R.id.tv_merchant_type_normal})
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.tv_merchant_type_buy:
                intent = new Intent(this, PublishTaskActivity.class);
                intent.putExtra("merchant_id", merchantId);
                intent.putExtra("merchant_name", sMerchantName);
                intent.putExtra("merchant_task_type", true);
                startActivity(intent);
                break;
            case R.id.tv_merchant_type_normal:
                intent = new Intent(this, PublishTaskActivity.class);
                intent.putExtra("merchant_id", merchantId);
                intent.putExtra("merchant_name", sMerchantName);
                intent.putExtra("merchant_task_type", false);
                startActivity(intent);
                break;
        }
    }


}
