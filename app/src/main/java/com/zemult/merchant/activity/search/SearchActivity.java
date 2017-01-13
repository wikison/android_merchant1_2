package com.zemult.merchant.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.zemult.merchant.R;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

/**
 * Created by wikison on 2016/6/15.
 */
public class SearchActivity extends BaseActivity implements SearchView.SearchViewListener{

    public static final String INTENT_KEY = "key";
    @Bind(R.id.a_seach_searchview)
    SearchView mSearchView;
    @Bind(R.id.fl)
    FrameLayout fl;
    private Context mContext;
    private Activity mActivity;
    private SearchMerchantFragment merchantFragment;
    private String key;

    String requestType;
    List<String> listRecentTags = new ArrayList<>();
    List<String> listRecentTagsTemp = new ArrayList<>();
    String strRecentTags = "";

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
        requestType=getIntent().getStringExtra("requesttype");
        mSearchView.setStrSearch(key);

        strRecentTags = SlashHelper.getSettingString("home_search_history", "");
        listRecentTagsTemp = Arrays.asList(strRecentTags.split(",,"));
        listRecentTags.addAll(listRecentTagsTemp);
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
        mSearchView.setOnBackClickListner(new SearchView.OnBackClickListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }
        });
        mSearchView.setSearchViewListener(this);
    }

    @Override
    public void onSearch(String strSearchWord) {
        if (!listRecentTags.contains(strSearchWord)) {
            if (listRecentTags.size() == Constants.RECENT_SEARCH_ROWS) {
                //元素循环向前移动一位 删除最后一位
                Collections.rotate(listRecentTags, 1);
                listRecentTags.remove(Constants.RECENT_SEARCH_ROWS - 1);
            }
            //向最后一位添加元素
            listRecentTags.add(strSearchWord);
            SlashHelper.setSettingString("home_search_history", listToString(listRecentTags));

        }

        merchantFragment.search(strSearchWord);
    }

    private String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",,");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    @Override
    public void onBackPressed() {
        if (requestType.equals(Constants.BROCAST_SEARCH_RECENT_WORD)) {
            Intent intent = new Intent(Constants.BROCAST_SEARCH_RECENT_WORD);
            sendBroadcast(intent);
        }

        super.onBackPressed();

    }


}
