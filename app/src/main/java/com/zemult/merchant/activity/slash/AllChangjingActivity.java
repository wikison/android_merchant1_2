package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.flyco.roundview.RoundLinearLayout;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.search.SearchActivity;
import com.zemult.merchant.activity.search.SearchMerchantFragment;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewAdapter;
import com.zemult.merchant.aip.slash.MerchantFirstpageSearchListRequest;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.mvp.presenter.HomePresenter;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.ModelUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 0162全部场景
 */
public class AllChangjingActivity extends BaseActivity{

    public static final String INTENT_INDUSTYR_ID = "id";
    public static final String INTENT_INDUSTYR_NAME = "name";
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.iv_right)
    ImageView ivRight;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.rll_search_bg)
    RoundLinearLayout rllSearchBg;
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
        lhTvTitle.setText(industryName);
        // 开启Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl, merchantFragment);
        transaction.commit();
    }

    private void initListener() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    merchantFragment.search(etSearch.getText().toString());
                }
                return true;
            }
        });
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
