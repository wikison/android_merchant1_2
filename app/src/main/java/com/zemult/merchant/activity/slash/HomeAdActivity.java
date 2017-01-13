package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeAdActivity extends BaseActivity {


    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv)
    ImageView iv;

    String url,titlename;
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_home_add);
    }

    @Override
    public void init() {
        url=getIntent().getStringExtra("url");
        titlename=getIntent().getStringExtra("titlename")==null?"":getIntent().getStringExtra("titlename");
        lhTvTitle.setText(titlename);
        imageManager.loadUrlImage(url, iv);
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
