package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.SearchBusinessAdapter;
import com.zemult.merchant.aip.slash.MerchantSearchmerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 商户入驻--场景全称
 *
 * @author djy
 * @time 2016/8/3 16:40
 */
public class MerchantFullNameActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.search_view)
    SearchView searchView;
    @Bind(R.id.rl_input)
    RelativeLayout rlInput;

    private MerchantSearchmerchantListRequest merchantSearchmerchantListRequest;
    private int page = 1;
    private Context mContext;
    private SearchBusinessAdapter mAdapter;
    private String name;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_search_business);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        merchant_searchmerchantList(false);
    }

    private void initData() {
        mContext = this;
    }

    private void initView() {
        lhTvTitle.setText("商家全称");
        mAdapter = new SearchBusinessAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);

        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it3 = new Intent();
                it3.putExtra("merchantId", mAdapter.getItem(position - 1).merchantId);
                setResult(RESULT_OK, it3);
                finish();
            }
        });
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                showPd();
                name = text;
                merchant_searchmerchantList(false);
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 搜索场景列表 条件:场景名称,用户中心坐标,行业,是否上线
    private void merchant_searchmerchantList(final boolean isLoadMore) {
        if (merchantSearchmerchantListRequest != null) {
            merchantSearchmerchantListRequest.cancel();
        }
        MerchantSearchmerchantListRequest.Input input = new MerchantSearchmerchantListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.name = name; //场景名称(模糊)
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantSearchmerchantListRequest = new MerchantSearchmerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).merchantList,
                            ((APIM_MerchantList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
                dismissPd();
            }
        });
        sendJsonRequest(merchantSearchmerchantListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Merchant> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }


    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.rl_input})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.rl_input:
                setResult(RESULT_OK, new Intent());
                onBackPressed();
//                CommonDialog.showInputDialogListener(mContext, null, "取消", "继续", null, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CommonDialog.DismissProgressDialog();
//                    }
//                }, new CommonDialog.CommitClickListener() {
//                    @Override
//                    public void onCommit(String name) {
//                        CommonDialog.DismissProgressDialog();
//                        Intent it2 = new Intent();
//                        it2.putExtra("merchantName", name);
//                        setResult(RESULT_OK, it2);
//                        finish();
//                    }
//                });

                break;
        }
    }

    @Override
    public void onRefresh() {
        merchant_searchmerchantList(false);
    }

    @Override
    public void onLoadMore() {
        merchant_searchmerchantList(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
