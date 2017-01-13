package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/9/6.
 */

//热门探索
public class HotDiscoverActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.hot_lv)
    SmoothListView hotLv;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_hotdiscover);

    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("热门搜索");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
