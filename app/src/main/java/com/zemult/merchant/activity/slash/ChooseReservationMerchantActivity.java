package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.search.SearchUserAdpater;
import com.zemult.merchant.adapter.slash.TaMerchantChooseAdapter;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.CreateBespeakActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.BounceScrollView;
import com.zemult.merchant.view.FixedListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2017/1/20.
 */

public class ChooseReservationMerchantActivity extends BaseActivity {

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
    @Bind(R.id.flv_can)
    FixedListView flvCan;
    @Bind(R.id.bsv_container)
    BounceScrollView bsvContainer;


    List<M_Merchant> merchantList = new ArrayList<M_Merchant>();

    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    TaMerchantChooseAdapter adapterCan;
    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_reservation_merchant);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getOtherMerchantList();
    }

    private void initListener() {
        adapterCan.setOnAllClickListener(new TaMerchantChooseAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                M_Merchant m_merchant= adapterCan.getItem(position);
                Intent  intent = new Intent(mContext, CreateBespeakActivity.class);
                intent.putExtra("serviceId",userId);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("m_merchant",m_merchant);
                intent.putExtras(mBundle);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        lhTvTitle.setText("选择预约商户");
    }

    private void initData() {
        userId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        mContext = this;
        mActivity = this;
        adapterCan = new TaMerchantChooseAdapter(mContext, merchantList);
        flvCan.setAdapter(adapterCan);
    }

    /**
     * 查看TA挂靠的商家
     */
    private void getOtherMerchantList() {
        if (merchantOtherMerchantListRequest != null) {
            merchantOtherMerchantListRequest.cancel();
        }
        MerchantOtherMerchantListRequest.Input input = new MerchantOtherMerchantListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.center = (String) SPUtils.get(mContext, Constants.SP_CENTER, "119.971736,31.829737");
        input.userId = userId;
        input.page = 1;
        input.rows = Constants.ROWS;
        input.convertJosn();
        merchantOtherMerchantListRequest = new MerchantOtherMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    merchantList = ((APIM_MerchantList) response).merchantList;
                    fillAdapter(merchantList, false);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantOtherMerchantListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Merchant> list, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
        } else {
            adapterCan.setData(list, isLoadMore);
        }

    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }

}
