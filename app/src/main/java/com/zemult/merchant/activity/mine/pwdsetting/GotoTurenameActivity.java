package com.zemult.merchant.activity.mine.pwdsetting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TrueNameActivity;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class GotoTurenameActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.btn_gotoauth)
    Button btnGotoauth;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_goto_turename);
    }

    @Override
    public void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lhTvTitle.setText("更换绑定手机号码");

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_gotoauth})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_gotoauth:
                Intent intent =new Intent(GotoTurenameActivity.this,TrueNameActivity.class);
                startActivity(intent);
                break;
        }
    }
}
