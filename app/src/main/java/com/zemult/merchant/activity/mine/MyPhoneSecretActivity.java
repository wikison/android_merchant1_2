package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by wikison on 2016/6/21.
 */
public class MyPhoneSecretActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.rtv_phone)
    RoundTextView rtvPhone;
    @Bind(R.id.sc_public)
    SwitchCompat scPubic;

    String phone;
    int isPublic = -1;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_my_phone_secret);
    }

    @Override
    public void init() {
        initData();
        initView();
    }


    private void initData() {
        phone = SlashHelper.userManager().getUserinfo().getPhoneNum();
        rtvPhone.setText(phone);
        isPublic = SlashHelper.userManager().getUserinfo().getIsOpen();
        if (isPublic == 0) {
            scPubic.setChecked(false);
        } else if (isPublic == 1) {
            scPubic.setChecked(true);
        }
    }

    private void initView() {
        lhTvTitle.setText("电话");
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

    @Override
    public void onBackPressed() {
        isPublic = scPubic.isChecked() ? 1 : 0;
        SlashHelper.userManager().getUserinfo().setIsOpen(isPublic);
        Intent intent = new Intent();
        intent.putExtra("is_open_result", isPublic);
        MyPhoneSecretActivity.this.setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

}
