package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.base.MBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


//0592...重置密码
public class RsettingPasswordActivity extends MBaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.et_newpassword)
    EditText etNewpassword;
    @Bind(R.id.et_renewpassword)
    EditText etRenewpassword;
    @Bind(R.id.submit)
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_password);
        ButterKnife.bind(this);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("重置密码");
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
            case R.id.submit:
                break;
        }
    }
}
