package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
    
    @Override
    public void setContentView() {
        setContentView(R.layout.activity_saleman_info_setting);
    }

    @Override
    public void init() {
        lhTvTitle.setText("管家信息设置");
        initData();
        registerReceiver(new String[]{Constants.BROCAST_EDITUSERINFO});
    }

    private void initData() {
        tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        imageManager.loadCircleHead(SlashHelper.userManager().getUserinfo().getHead(), ivHead);
        nameString = SlashHelper.userManager().getUserinfo().getName();
        headString = SlashHelper.userManager().getUserinfo().getHead();
    }


    //接收广播回调
    @Override
    protected void handleReceiver(Context context, Intent intent) {

        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_EDITUSERINFO.equals(intent.getAction())) {
            initData();
        }
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUESTCODE_CHANGENICKNAME && resultCode == RESULT_OK) {
            nameString = SlashHelper.userManager().getUserinfo().getName();
            //修改用户资料信息
            tvName.setText(SlashHelper.userManager().getUserinfo().getName());
        } else if (requestCode == 110 && resultCode == RESULT_OK) {
            headString = SlashHelper.userManager().getUserinfo().getHead();
            imageManager.loadCircleHead(headString, ivHead);
        }
        initData();
    }
}
