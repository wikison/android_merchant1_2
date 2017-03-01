package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.search.SearchActivity;
import com.zemult.merchant.activity.search.SearchMerchantFragment;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SearchView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 0162全部场景
 */
public class AllChangjingActivity extends BaseActivity {

    public static final String INTENT_INDUSTYR_ID = "id";
    public static final String INTENT_INDUSTYR_NAME = "name";
    @Bind(R.id.searchview)
    SearchView searchView;
    @Bind(R.id.fl)
    FrameLayout fl;

    private Context mContext;
    private int industryId;
    private String industryName;
    private SearchMerchantFragment merchantFragment;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_changjing);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
    }

    private void initData() {
        industryId = getIntent().getIntExtra(INTENT_INDUSTYR_ID, 0);
        industryName = getIntent().getStringExtra(INTENT_INDUSTYR_NAME);
        mContext = this;

        merchantFragment = new SearchMerchantFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchActivity.INTENT_KEY, "");
        bundle.putInt(INTENT_INDUSTYR_ID, industryId);

        merchantFragment.setArguments(bundle);
    }

    private void initView() {
        // 开启Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, merchantFragment);
        transaction.commit();
    }

    private void initListener() {
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                merchantFragment.search(text);
            }

            @Override
            public void onClear() {

            }
        });
    }
}
