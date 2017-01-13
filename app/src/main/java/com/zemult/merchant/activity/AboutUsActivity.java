
package com.zemult.merchant.activity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2015/5/27.
 */
public class AboutUsActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.us_version_name)
    TextView usVersionName;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_aboutus);
    }

    @Override
    public void init() {
        lhBtnBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("关于我们");
        usVersionName.setText("版本号V"+SlashHelper.deviceManager().getVersionName());
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            finish();
            break;

        }

    }
}
