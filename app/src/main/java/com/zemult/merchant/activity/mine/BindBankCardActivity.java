package com.zemult.merchant.activity.mine;

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
import com.zemult.merchant.fragment.BindCardFragmentCallBack;
import com.zemult.merchant.fragment.BindCardOneFragment;
import com.zemult.merchant.fragment.BindCardSuccessFragment;
import com.zemult.merchant.fragment.BindCardThreeFragment;
import com.zemult.merchant.fragment.BindCardTwoFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 绑定银行卡主页面
 */
public class BindBankCardActivity extends BaseActivity implements BindCardFragmentCallBack {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.ll_head2)
    LinearLayout llHead2;
    @Bind(R.id.ll_head3)
    LinearLayout llHead3;
    @Bind(R.id.ll_head1)
    LinearLayout llHead1;
    @Bind(R.id.tv_tixing)
    TextView tvTixing;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.content)
    FrameLayout content;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private BindCardOneFragment oneFragment;
    private BindCardTwoFragment twoFragment;
    private BindCardThreeFragment threeFragment;
    private BindCardSuccessFragment successFragment;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_bind_bank_card);
    }

    @Override
    public void init() {
        llBack.setVisibility(View.VISIBLE);
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(R.string.title_bind_card);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        oneFragment = new BindCardOneFragment();
        twoFragment = new BindCardTwoFragment();
        threeFragment = new BindCardThreeFragment();
        successFragment = new BindCardSuccessFragment();

        transaction.replace(R.id.content, oneFragment);
        transaction.commit();

        llHead1.setVisibility(View.VISIBLE);
        llHead2.setVisibility(View.GONE);
        llHead3.setVisibility(View.GONE);
    }


    @Override
    public void showTwo(Bundle bundle) {
        // 设置银行信息
        twoFragment.setArguments(bundle);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, twoFragment);
        transaction.commit();
        llHead1.setVisibility(View.GONE);
        llHead2.setVisibility(View.VISIBLE);
        llHead3.setVisibility(View.GONE);
    }

    @Override
    public void showThree() {
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, threeFragment);
        transaction.commit();
        llHead1.setVisibility(View.GONE);
        llHead2.setVisibility(View.GONE);
        llHead3.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSuccess() {
        llRoot.setVisibility(View.GONE);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, successFragment);
        transaction.commit();
    }

    @Override
    public void finishAll() {
        onBackPressed();
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
