package com.zemult.merchant.activity.mine;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import butterknife.ButterKnife;
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
    @Bind(R.id.iv1)
    ImageView iv1;
    @Bind(R.id.iv2)
    ImageView iv2;
    @Bind(R.id.iv3)
    ImageView iv3;
    @Bind(R.id.ll_head)
    LinearLayout llHead;
    @Bind(R.id.ll_root)
    LinearLayout llRoot;
    @Bind(R.id.content)
    FrameLayout content;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private BindCardOneFragment oneFragment;
    private BindCardTwoFragment twoFragment;
//    private BindCardThreeFragment threeFragment;
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
//        threeFragment = new BindCardThreeFragment();
        successFragment = new BindCardSuccessFragment();

        transaction.replace(R.id.content, oneFragment);
        transaction.commit();
    }


    @Override
    public void showTwo(Bundle bundle) {
        // 设置银行信息
        twoFragment.setArguments(bundle);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content, twoFragment);
        transaction.commit();

        iv2.setImageResource(R.mipmap.two_icon_yes);
    }

//    @Override
//    public void showThree() {
//        transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.content, threeFragment);
//        transaction.commit();
//    }

    @Override
    public void showSuccess(Bundle bundle) {
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
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
