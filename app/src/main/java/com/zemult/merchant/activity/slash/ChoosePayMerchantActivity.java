package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.TaMerchantChooseAdapter;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
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

public class ChoosePayMerchantActivity extends BaseActivity {

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
    @Bind(R.id.flv)
    FixedListView flv;

    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    TaMerchantChooseAdapter adapter;
    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)

    List<M_Merchant> merchantList = new ArrayList<M_Merchant>();

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_choose_pay_merchant);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        showPd();
        getOtherMerchantList();
    }

    private void initListener() {
        adapter.setOnAllClickListener(new TaMerchantChooseAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                if(adapter.getItem(position).reviewstatus==2){

                }else {
                    ToastUtil.showMessage("该商户暂不支持买单");
                }
            }
        });

    }

    private void initView() {
        lhTvTitle.setText("选择买单商户");
    }

    private void initData() {
        userId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        mContext = this;
        mActivity = this;

        adapter = new TaMerchantChooseAdapter(mContext, merchantList);
        flv.setAdapter(adapter);
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
            adapter.setData(list, isLoadMore);
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