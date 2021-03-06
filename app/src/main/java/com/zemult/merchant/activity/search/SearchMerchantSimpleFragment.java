package com.zemult.merchant.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.mine.TabManageActivity;
import com.zemult.merchant.activity.slash.AllChangjingActivity;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewSimpleAdapter;
import com.zemult.merchant.aip.mine.UserCheckSaleUser1_2_2Request;
import com.zemult.merchant.aip.slash.Merchant2SearchListBandRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by wikison on 2016/6/15.
 * 搜索场景
 */
public class SearchMerchantSimpleFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    public static final String TAG = SearchMerchantSimpleFragment.class.getSimpleName();

    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.ll_no_bind)
    LinearLayout llNoBind;

    private Merchant2SearchListBandRequest request;
    private int page = 1, industryId;

    private HomeChildNewSimpleAdapter mAdapter; // 主页数据

    private Context mContext;

    private String key;

    @Override
    protected void lazyLoad() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_child_simple, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();
        showPd();
        merchant_firstpage_search_List(false);
    }

    private void initData() {
        key = getArguments().getString(SearchActivity.INTENT_KEY);
        industryId = getArguments().getInt(AllChangjingActivity.INTENT_INDUSTYR_ID, -1);
        mContext = getActivity();
    }

    private void initView() {
        mAdapter = new HomeChildNewSimpleAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Merchant merchant = mAdapter.getItem(position - 1);
                Intent intent = new Intent(mContext, Search4KeyWordsActivity.class);
                intent.putExtra("key", merchant.name);
                startActivity(intent);

//                goTo(merchant);
            }
        });
//        mAdapter.setItemClickListener(new HomeChildNewSimpleAdapter.ItemClickListener() {
//            @Override
//            public void onBannerClick(M_Merchant merchant) {
//                goTo(merchant);
//            }
//
//            @Override
//            public void onRvHeadClick(int pos) {
//
//            }
//        });
    }

//    private void goTo(M_Merchant merchant) {
//        if (iToAdd == 1) {
//            if (merchant != null && merchant.isCommission == 1) { // 是服务管家
//                Intent intent = new Intent(mContext, TabManageActivity.class);
//                intent.putExtra(TabManageActivity.TAG, merchant.merchantId);
//                intent.putExtra(TabManageActivity.NAME, merchant.name);
//                intent.putExtra(TabManageActivity.TAGS, merchant.tags);
//                intent.putExtra(TabManageActivity.COMEFROM, 2);
//                startActivity(intent);
//            } else { // 不是服务管家
//                user_check_saleuser_1_2_2(merchant);
//            }
//        } else {
//            Intent intent = new Intent(mContext, MerchantDetailActivity.class);
//            intent.putExtra(MerchantDetailActivity.MERCHANT_ID, merchant.merchantId);
//            startActivity(intent);
//        }
//    }

    /**
     * 判断用户是否可以申请商家的服务管家
     */
    private UserCheckSaleUser1_2_2Request checkSaleUser1_2_2Request;

    private void user_check_saleuser_1_2_2(final M_Merchant merchant) {
        showPd();
        if (checkSaleUser1_2_2Request != null) {
            checkSaleUser1_2_2Request.cancel();
        }
        UserCheckSaleUser1_2_2Request.Input input = new UserCheckSaleUser1_2_2Request.Input();

        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchant.merchantId;
        input.convertJosn();
        checkSaleUser1_2_2Request = new UserCheckSaleUser1_2_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
//                    if (StringUtils.isBlank(SlashHelper.userManager().getUserinfo().getHead())) {
//                        Intent it = new Intent(mContext, BeManagerFirstActivity.class);
//                        it.putExtra(TabManageActivity.TAG, merchant.merchantId);
//                        it.putExtra(TabManageActivity.NAME, merchant.name);
//                        startActivity(it);
//                    } else {
//                        Intent it = new Intent(mContext, TabManageActivity.class);
//                        it.putExtra(TabManageActivity.TAG, merchant.merchantId);
//                        it.putExtra(TabManageActivity.NAME, merchant.name);
//                        it.putExtra(TabManageActivity.COMEFROM, 1);
//                        startActivity(it);
//                    }
                    Intent it = new Intent(mContext, TabManageActivity.class);
                    it.putExtra(TabManageActivity.TAG, merchant.merchantId);
                    it.putExtra(TabManageActivity.NAME, merchant.name);
                    it.putExtra(TabManageActivity.COMEFROM, 1);
                    startActivity(it);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(checkSaleUser1_2_2Request);
    }

    public void search(String key) {
        if (key.equals(this.key))
            return;

        Intent intent =new Intent(getActivity(),Search4KeyWordsActivity.class);
        intent.putExtra("key",key);
        startActivity(intent);
//        showPd();
//        this.key = key;
//        merchant_firstpage_search_List(false);
    }

    //搜索方案列表
    public void merchant_firstpage_search_List(final boolean isLoadMore) {
        if (request != null) {
            request.cancel();
        }

        Merchant2SearchListBandRequest.Input input = new Merchant2SearchListBandRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.industryId = industryId;
        input.name = key;
        input.city = Constants.CITYID;
        input.center = Constants.CENTER;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        request = new Merchant2SearchListBandRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    fillAdapter(((APIM_MerchantList) response).merchantList,
                            ((APIM_MerchantList) response).maxpage,
                            isLoadMore);

                } else {
                    ToastUtils.show(getActivity(), ((APIM_MerchantList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }
        });
        sendJsonRequest(request);
    }

    // 填充数据
    private void fillAdapter(List<M_Merchant> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            smoothListView.setVisibility(View.GONE);
            llNoBind.setVisibility(View.VISIBLE);

        } else {
            smoothListView.setVisibility(View.VISIBLE);
            llNoBind.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);

            if (!isLoadMore)
                smoothListView.setSelection(0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        merchant_firstpage_search_List(false);
    }

    @Override
    public void onLoadMore() {
        merchant_firstpage_search_List(true);
    }

    public void setIndustryIdAndSearch(int industryId) {
        this.industryId = industryId;
        showPd();
        merchant_firstpage_search_List(false);
    }
}
