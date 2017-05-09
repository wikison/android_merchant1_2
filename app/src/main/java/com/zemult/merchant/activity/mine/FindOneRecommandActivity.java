package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by admin on 2017/3/31.
 */

public class FindOneRecommandActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_find_one_recommand);
    }

    @Override
    public void init() {
        lhTvTitle.setText("智能找管家");
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.al_btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:

            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.al_btn_login:
                Intent it = new Intent(this, FindOneResultActivity.class);
                startActivity(it);
                break;
        }
    }
}
