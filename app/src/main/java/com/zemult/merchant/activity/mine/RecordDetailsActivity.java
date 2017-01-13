package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 2016/12/26.
 */
//记录详情
public class RecordDetailsActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.head_iv)
    ImageView headIv;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.shop_tv)
    TextView shopTv;
    @Bind(R.id.number_tv)
    TextView numberTv;
    @Bind(R.id.time_tv)
    TextView timeTv;
    @Bind(R.id.hongbao_tv)
    TextView hongbaoTv;
    @Bind(R.id.hongbao_rl)
    RelativeLayout hongbaoRl;

    @Override
    public void setContentView() {
        setContentView(R.layout.recorddetail_activity);
    }

    @Override
    public void init() {
        lhTvTitle.setText("记录详情");
    }



    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.hongbao_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.hongbao_rl:
                break;
        }
    }
}
