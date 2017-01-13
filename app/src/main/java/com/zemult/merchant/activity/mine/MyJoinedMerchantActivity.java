package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/26.
 */

public class MyJoinedMerchantActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    @Override
    public void setContentView() {
        setContentView(R.layout.myjoinedmerchant_activity);
    }

    @Override
    public void init() {
        initView();
        initListener();
    }



    private void initView() {
        lhTvTitle.setText("我加入的商户");

    }
    private void initListener() {

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                break;
            case R.id.ll_back:
                break;
        }
    }
}
