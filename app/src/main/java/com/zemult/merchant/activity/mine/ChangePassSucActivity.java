package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.app.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class ChangePassSucActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ok_btn)
    Button okBtn;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.tv_remark)
    TextView tvRemark;

    String pwdType;

    @Override
    public void setContentView() {
        setContentView(R.layout.change_pass_suc);
    }

    @Override
    public void init() {
        lhTvTitle.setVisibility(View.VISIBLE);
        pwdType = getIntent().getStringExtra("password");
        if (pwdType.equals("change")) {
            lhTvTitle.setText("修改密码");
            tvRemark.setText("密码修改成功");
        }
        else if (pwdType.equals("forget")) {
            lhTvTitle.setText("找回密码");
            tvRemark.setText("密码找回成功");
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ok_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
            case R.id.ok_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChangePassSucActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }

}
