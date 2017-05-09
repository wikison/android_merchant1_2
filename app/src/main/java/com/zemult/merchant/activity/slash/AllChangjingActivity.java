package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.zemult.merchant.activity.search.SearchActivity;
import com.zemult.merchant.activity.search.SearchHotActivity;
import com.zemult.merchant.activity.search.SearchMerchantFragment;
import com.zemult.merchant.adapter.slashfrgment.AllIndustryAdapter;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.view.SearchView;

import java.util.List;

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
    @Bind(R.id.rv)
    RecyclerView rv;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    private Context mContext;
    private SearchMerchantFragment merchantFragment;
    private List<M_Industry> industryList;
    private AllIndustryAdapter allIndustryAdapter;

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
        mContext = this;
        industryList = (List<M_Industry>) getIntent().getSerializableExtra("industryList");
        merchantFragment = new SearchMerchantFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchActivity.INTENT_KEY, "");
        bundle.putInt(INTENT_INDUSTYR_ID, -1);

        merchantFragment.setArguments(bundle);
    }

    private void initView() {
        lhTvTitle.setText("进店找管家");
        llRight.setVisibility(View.VISIBLE);
        ivRight.setImageResource(R.mipmap.fangdajin_icon);
        // 开启Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, merchantFragment);
        transaction.commit();

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        //设置适配器
        allIndustryAdapter = new AllIndustryAdapter(mContext, industryList);
        rv.setAdapter(allIndustryAdapter);
        allIndustryAdapter.setSelectedId(industryList.get(0).id);
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

        allIndustryAdapter.setOnItemClickLitener(new AllIndustryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(int industryId) {
                merchantFragment.setIndustryIdAndSearch(industryId);
                merchantFragment.onRefresh();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.iv_right, R.id.ll_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.iv_right:
            case R.id.ll_right:
                startActivity(new Intent(mContext, SearchHotActivity.class));
                break;
        }
    }
}
