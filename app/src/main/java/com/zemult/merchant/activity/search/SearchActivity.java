package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SearchView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wikison on 2016/6/15.
 */
public class SearchActivity extends BaseActivity implements SearchView.SearchViewListener {

    public static final String INTENT_KEY = "key";
    @Bind(R.id.fl)
    FrameLayout fl;
    @Bind(R.id.search_et_input)
    TextView searchEtInput;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.ll_search)
    LinearLayout llSearch;
    private Context mContext;
    private Activity mActivity;
    private SearchMerchantFragment merchantFragment;
    private String key;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
        key = getIntent().getStringExtra(INTENT_KEY);
        searchEtInput.setText(key);

        merchantFragment = new SearchMerchantFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchActivity.INTENT_KEY, key);
        merchantFragment.setArguments(bundle);
    }

    private void initView() {
        // 开启Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, merchantFragment);
        transaction.commit();
    }

    private void initListener() {
    }

    @Override
    public void onSearch(String strSearchWord) {
        merchantFragment.search(strSearchWord);
    }

    @Override
    public void onClear() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_cancel, R.id.ll_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
            case R.id.ll_search:
                onBackPressed();
                break;
        }
    }
}
