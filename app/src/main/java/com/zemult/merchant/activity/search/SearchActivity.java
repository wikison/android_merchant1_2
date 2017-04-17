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
public class SearchActivity extends BaseActivity {

    public static final String INTENT_KEY = "key";
    @Bind(R.id.a_seach_searchview)
    SearchView searchview;
    @Bind(R.id.fl)
    FrameLayout fl;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_head)
    LinearLayout llHead;

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
            llHead.setVisibility(View.VISIBLE);
            lhTvTitle.setText("绑定商户");
            searchview.setStrHint("搜索商户名称");
            searchview.setTvCancelVisible(View.GONE);
            searchview.setBgColor(0xfff5f5f5);

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
}
