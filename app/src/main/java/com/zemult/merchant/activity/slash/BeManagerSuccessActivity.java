package com.zemult.merchant.activity.slash;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BeManagerSuccessActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_be_manager_success);
    }

    @Override
    public void init() {
        lhTvTitle.setText("成为服务管家");
        tvName.setText('"' + getIntent().getStringExtra(TabManageActivity.NAME) + '"' );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_share, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.btn_ok:
                Intent intent = new Intent(Constants.BROCAST_BE_SERVER_MANAGER_SUCCESS);
                sendBroadcast(intent);

                onBackPressed();
                break;
            case R.id.btn_share:
                break;

        }
    }
}
