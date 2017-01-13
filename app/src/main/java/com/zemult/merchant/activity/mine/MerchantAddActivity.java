package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.MerchantDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeChildNewAdapter;
import com.zemult.merchant.aip.mine.UserUnsaleMerchantListRequest;
import com.zemult.merchant.aip.slash.UserAddSaleUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.MMAlert;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 添加商家
 *
 * @author djy
 * @time 2016/11/17 15:20
 */
public class MerchantAddActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.searchView)
    SearchView searchView;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private Context mContext;
    private HomeChildNewAdapter adapter;

    private int page = 1;
    private UserUnsaleMerchantListRequest listrequest;
    private String name;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_merchant_add2);
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
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("添加商家");
        lhBtnBack.setVisibility(View.VISIBLE);


        adapter = new HomeChildNewAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(adapter);

        searchView.setTvCancelVisible(View.GONE);
        searchView.setBgColor(getResources().getColor(R.color.divider_c1));
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
//        adapter.setOnPlanDetailClickListener(new HomeChildNewAdapter.OnPlanDetailClickListener() {
//            @Override
//            public void onPlanDetailClick(int position) {
//                Intent intent = new Intent(mContext, MerchantDetailActivity.class);
//                intent.putExtra(MerchantDetailActivity.MERCHANT_ID, adapter.getItem(position).merchantId);
//                startActivity(intent);
//            }
//        });
//        adapter.setOnApplyClickListener(new HomeChildNewAdapter.OnApplyClickListener() {
//            @Override
//            public void onApplyClick(final int position) {
//                MMAlert.showApplyDialog(mContext, adapter.getItem(position), new MMAlert.ApplyCallback() {
//                    @Override
//                    public void onApply() {
//                        showPd();
//                        user_add_saleuser(adapter.getItem(position).merchantId, position);
//                    }
//                });
//
//            }
//        });
        searchView.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
                showPd();
                name = text;
                merchant_searchmerchantList(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        merchant_searchmerchantList(false);
    }

    @Override
    public void onLoadMore() {
        merchant_searchmerchantList(true);
    }

    // 搜索场景列表 条件:场景名称,用户中心坐标,行业,是否上线
    private void merchant_searchmerchantList(final boolean isLoadMore) {
        if (listrequest != null) {
            listrequest.cancel();
        }
        UserUnsaleMerchantListRequest.Input input = new UserUnsaleMerchantListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.name = name; //场景名称(模糊)
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        listrequest = new UserUnsaleMerchantListRequest(input, new ResponseListener() {
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
        sendJsonRequest(listrequest);
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
            adapter.setData(list, isLoadMore);
        }
    }

    private UserAddSaleUserRequest userAddSaleUserRequest;
    /**
     * 用户申请成为商家的营销经理
     */
    private void user_add_saleuser(int merchantId, final int pos) {
        if (userAddSaleUserRequest != null) {
            userAddSaleUserRequest.cancel();
        }
        UserAddSaleUserRequest.Input input = new UserAddSaleUserRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.merchantId = merchantId;
        input.convertJosn();
        userAddSaleUserRequest = new UserAddSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    MMAlert.showOneOperateDialog(mContext, "恭喜您申请成功", "确定", new MMAlert.OneOperateCallback() {
                        @Override
                        public void onOneOperate() {
                            merchant_searchmerchantList(false);
                        }
                    });
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(userAddSaleUserRequest);
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
