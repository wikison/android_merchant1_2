package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.view.SearchView;

import butterknife.Bind;

/**
 * Created by wikison on 2016/6/15.
 */
public class SearchActivity extends BaseActivity {

    public static final String INTENT_KEY = "key";
    @Bind(R.id.a_seach_searchview)
    SearchView searchview;
    @Bind(R.id.fl)
    FrameLayout fl;

    private Context mContext;
    private Activity mActivity;
    private SearchMerchantFragment merchantFragment;
    private String key;

    int iToAdd = 0;


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
        iToAdd = getIntent().getIntExtra("be_service_manager", -1);
        searchview.setStrHint("");
        searchview.setStrSearch(key);

        merchantFragment = new SearchMerchantFragment();
        Bundle bundle = new Bundle();
        if (iToAdd == 1) {
            bundle.putString(SearchActivity.INTENT_KEY, "");
        } else {
            bundle.putString(SearchActivity.INTENT_KEY, key);

        }
        bundle.putInt("be_service_manager", iToAdd);
        merchantFragment.setArguments(bundle);
    }

    private void initView() {
        // 开启Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, merchantFragment);
        transaction.commit();
    }

    private void initListener() {
        searchview.setSearchViewListener(new SearchView.SearchViewListener() {
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
