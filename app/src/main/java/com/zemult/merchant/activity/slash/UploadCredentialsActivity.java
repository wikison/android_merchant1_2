package com.zemult.merchant.activity.slash;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.fragment.UploadCredentialsFragmentCallBack;
import com.zemult.merchant.fragment.UploadCredentialsOneFragment;
import com.zemult.merchant.fragment.UploadCredentialsSuccessFragment;
import com.zemult.merchant.fragment.UploadCredentialsTwoFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * +013121200证件上传
 */
public class UploadCredentialsActivity extends BaseActivity implements UploadCredentialsFragmentCallBack {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.content)
    FrameLayout content;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private UploadCredentialsOneFragment oneFragment;
    private UploadCredentialsTwoFragment twoFragment;
    private UploadCredentialsSuccessFragment successFragment;
    String IDphotos,ossImgname;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_upload_credentials);
    }

    @Override
    public void init() {
        llBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("证件上传");
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        oneFragment = new UploadCredentialsOneFragment();
        twoFragment = new UploadCredentialsTwoFragment();
        successFragment = new UploadCredentialsSuccessFragment();

        transaction.replace(R.id.content, oneFragment);
        transaction.commit();

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
    public void showTwo(Bundle bundle) {
        // 设置照片信息
        twoFragment.setArguments(bundle);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, twoFragment);
        transaction.commit();
    }


    @Override
    public void showSuccess() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, successFragment);
        transaction.commit();
    }


}

