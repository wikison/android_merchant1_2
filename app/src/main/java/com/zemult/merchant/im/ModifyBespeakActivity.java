package com.zemult.merchant.im;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class ModifyBespeakActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.et_bespeak)
    EditText etBespeak;
    @Bind(R.id.bespek_time)
    TextView bespekTime;
    @Bind(R.id.bespek_shopname)
    TextView bespekShopname;
    @Bind(R.id.ed_bespeak_people)
    EditText edBespeakPeople;
    @Bind(R.id.btn_bespeak_commit)
    Button btnBespeakCommit;

    String shopname, bespeaktime, bespeakpeople, bespeakremark, ordertitle;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_modifybespeak);


    }

    @Override
    public void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopname=getIntent().getStringExtra("shopname");
        bespeaktime=getIntent().getStringExtra("bespeaktime");
        bespeakpeople=getIntent().getStringExtra("bespeakpeople");
        bespeakremark=getIntent().getStringExtra("bespeakremark");
        ordertitle=getIntent().getStringExtra("ordertitle");

        bespekTime.setText(bespeaktime);
        etBespeak.setText(bespeakremark);
        bespekShopname.setText(shopname);
        edBespeakPeople.setText(bespeakpeople);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
