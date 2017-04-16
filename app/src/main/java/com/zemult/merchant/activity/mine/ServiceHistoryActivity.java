package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.UserSalePayAdapter;
import com.zemult.merchant.aip.mine.UserSalePayListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Bill;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/12/26.
 */

public class ServiceHistoryActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {

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
    Button lhBtnRightIamge;
    @Bind(R.id.smoothListView)
    SmoothListView mSmoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private Context mContext;
    private Activity mActivity;
    private UserSalePayListRequest userSalePayListRequest;
    private List<M_Bill> payList = new ArrayList<M_Bill>();
    private UserSalePayAdapter userSalePayAdapter;
    int page = 1;
    int selectPosition;
    int selectPayId;
    int saleUserId,merchantId;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_commonlist);
    }

    @Override
    public void init() {
        mContext = this;
        mActivity = this;
        saleUserId = getIntent().getIntExtra("saleUserId",-1);
        merchantId = getIntent().getIntExtra("merchantId",-1);

        initView();
        initListener();

        getUserSalePayList(false);

    }

    private void initListener() {

        mSmoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position - 1;
                M_Bill m_bill = userSalePayAdapter.getItem(position - 1);
                selectPayId = m_bill.userPayId;
                Intent intent = new Intent(mContext, ServiceHistoryDetailActivity.class);
                intent.putExtra("userPayId", m_bill.userPayId);
                startActivity(intent);
            }
        });

    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("交易单记录");
        mSmoothListView.setRefreshEnable(true);
        mSmoothListView.setLoadMoreEnable(false);
        mSmoothListView.setDividerHeight(DensityUtil.dp2px(mContext, 12));
        mSmoothListView.setHeaderDividersEnabled(false);
        mSmoothListView.setSmoothListViewListener(this);
        userSalePayAdapter = new UserSalePayAdapter(this, payList);
        mSmoothListView.setAdapter(userSalePayAdapter);
    }

    /**
     * 获取我的服务记录列表
     */
    private void getUserSalePayList(final boolean isLoadMore) {
        if (userSalePayListRequest != null) {
            userSalePayListRequest.cancel();
        }
        UserSalePayListRequest.Input input = new UserSalePayListRequest.Input();
        input.saleUserId = saleUserId;
        input.merchantId= merchantId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();

        userSalePayListRequest = new UserSalePayListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserBillList) response).status == 1) {
                    fillAdapter(((APIM_UserBillList) response).userPayList,
                            ((APIM_UserBillList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserBillList) response).info);
                }
                
                mSmoothListView.stopRefresh();
                mSmoothListView.stopLoadMore();

            }
        });
        sendJsonRequest(userSalePayListRequest);
    }


    // 填充数据
    private void fillAdapter(List<M_Bill> list, int maxPage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
            rlNoData.setVisibility(View.VISIBLE);
            mSmoothListView.setVisibility(View.GONE);
            mSmoothListView.setLoadMoreEnable(false);
        } else {
            rlNoData.setVisibility(View.GONE);
            mSmoothListView.setVisibility(View.VISIBLE);
            mSmoothListView.setLoadMoreEnable(page < maxPage);
            userSalePayAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        getUserSalePayList(false);
    }

    @Override
    public void onLoadMore() {
        getUserSalePayList(true);
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
