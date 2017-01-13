package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2016/8/5.
 */
public class TestResultActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.name_tv)
    TextView nameTv;
    @Bind(R.id.money_tv)
    TextView moneyTv;
    @Bind(R.id.code_tv)
    TextView codeTv;
    public static final String Call_Cards_REFRESH = "call_cards_refresh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testresult);
        ButterKnife.bind(this);
        lhTvTitle.setText("验单结果");
        String name = getIntent().getStringExtra("name");
        double money = getIntent().getDoubleExtra("money",0);
        String code =getIntent().getStringExtra("code");
        nameTv.setText(name);
        moneyTv.setText("￥"+money);
        codeTv.setText(code);

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                EventBus.getDefault().post(Call_Cards_REFRESH);
                onBackPressed();

                break;
        }
    }
}
