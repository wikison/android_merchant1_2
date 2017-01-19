package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.MerchantManagerAdpater;
import com.zemult.merchant.aip.mine.MerchantSaleuserDelRequest;
import com.zemult.merchant.aip.slash.MerchantSaleuserListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.StringMatchUtils;
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
 * 营销经理管理
 *
 * @author djy
 * @time 2016/11/16 16:21
 */
public class MerchantManagerManageActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.tv_right)
    TextView tvRight;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private Context mContext;
    private Activity mActivity;

    /**
     * 必传参数 MERCHANT_ID
     */
    public static final String MERCHANT_ID = "merchantId";
    private MerchantManagerAdpater mAdapter;

    private int merchantId, saleUserId;
    private int page = 1;

    private MerchantSaleuserListRequest allRequest;
    private MerchantSaleuserDelRequest delRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_manager_manage);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        showPd();
        merchant_saleuserList_all(false);
    }

    private void initData() {
        mContext = this;
        mActivity = this;

        merchantId = getIntent().getIntExtra(MERCHANT_ID, -1);
    }

    private void initView() {
        lhTvTitle.setText("服务管家管理");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("管理");
        mAdapter = new MerchantManagerAdpater(mContext, new ArrayList<M_Userinfo>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);


        mAdapter.setOnDelClickListener(new MerchantManagerAdpater.OnDelClickListener() {
            @Override
            public void onDelClick(int position) {
                showPd();
                saleUserId = mAdapter.getItem(position).getUserId();
                merchant_saleuser_del(position);
            }
        });

    }


    /**
     * 商家下的营销经理列表(全部)
     */
    private void merchant_saleuserList_all(final boolean isLoadMore) {
        if (allRequest != null) {
            allRequest.cancel();
        }
        MerchantSaleuserListRequest.Input input = new MerchantSaleuserListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        allRequest = new MerchantSaleuserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    setUserListAll(((APIM_SearchUsersList) response).userList,
                            ((APIM_SearchUsersList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }
        });
        sendJsonRequest(allRequest);
    }

    /**
     * 删除商家下的某个营销经理
     */
    public  void merchant_saleuser_del(final int pos) {
        showPd();
        if (delRequest != null) {
            delRequest.cancel();
        }
        MerchantSaleuserDelRequest.Input input = new MerchantSaleuserDelRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.saleUserId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;

        input.convertJosn();
        delRequest = new MerchantSaleuserDelRequest(input,new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    mAdapter.delone(pos);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(delRequest);
    }

    /**
     * 全部
     */
    private void setUserListAll(List<M_Userinfo> listAll, int maxpage, boolean isLoadMore) {
        if (listAll.isEmpty()) {
            rlNoData.setVisibility(View.VISIBLE);
            smoothListView.setVisibility(View.GONE);
        } else {
            rlNoData.setVisibility(View.GONE);
            smoothListView.setVisibility(View.VISIBLE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(listAll, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        merchant_saleuserList_all(false);
    }

    @Override
    public void onLoadMore() {
        merchant_saleuserList_all(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_right, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_right:
            case R.id.tv_right:
                if("管理".equals(tvRight.getText().toString())){
                    tvRight.setText("完成");
                    mAdapter.refresh(true);
                }else{
                    tvRight.setText("管理");
                    mAdapter.refresh(false);
                }
                break;
        }
    }

}
