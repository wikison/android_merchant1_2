package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.MainActivity;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindOneResultActivity extends BaseActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.al_btn_login)
    Button alBtnLogin;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_one_result);
    }

    @Override
    public void init() {
        lhTvTitle.setText("找人推荐");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back,R.id.al_btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.al_btn_login:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
        }
    }


}
