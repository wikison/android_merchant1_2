package com.zemult.merchant.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.HeadManageActivity;
import com.zemult.merchant.adapter.slashfrgment.PhotoFix3Adapter;
import com.zemult.merchant.aip.mine.UserEditinfoRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SalemanInfoSettingActivity extends BaseActivity {

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.iv_head)
    ImageView ivHead;
    @Bind(R.id.imageView)
    ImageView imageView;
    @Bind(R.id.tv_name)
    TextView tvName;
    String nameString="", headString="";
    UserEditinfoRequest userEditinfoRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_saleman_info_setting);
    }

    @Override
    public void init() {
        lhTvTitle.setText("管家信息设置");
        initData();
    }

    private void initData() {
        tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
        nameString = SlashHelper.userManager().getUserinfo().getName();
        headString = SlashHelper.userManager().getUserinfo().getHead();
    }




    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.mis_headgo_layout, R.id.mis_name_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.mis_headgo_layout:
                startActivityForResult(new Intent(SalemanInfoSettingActivity.this, HeadManageActivity.class), 110);
                break;
            case R.id.mis_name_layout:
               Intent  intent = new Intent(SalemanInfoSettingActivity.this, NicknameActivity.class);
                startActivityForResult(intent, Constants.REQUESTCODE_CHANGENICKNAME);
                break;
        }
    }
}
