package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/6/15.
 */
public class SafeSettingActivity extends MBaseActivity {


    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.change_rl)
    RelativeLayout changeRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_safesetting);
        ButterKnife.bind(this);


        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("安全设置");
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back,R.id.change_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
//            case R.id.certification_rl:
//                //startActivity(new Intent(this, BindBankCardActivity.class));
//                break;
            case R.id.change_rl:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
        }
    }


}
