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

public class CreateBespeakActivity extends BaseActivity {

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

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bespeak);


    }

    @Override
    public void init() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.btn_bespeak_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_bespeak_commit:
                Intent intent =new Intent();
                intent.putExtra("shopname","全聚德通江路店");
                intent.putExtra("bespeaktime","2016-12-16 20:00");
                intent.putExtra("bespeakremark","要靠窗户的位子");
                intent.putExtra("bespeakpeople","4");
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
    }
}
