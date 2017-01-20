package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slash.TaMerchantAdapter;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.IntentUtil;
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
 * Created by Wikison on 2017/1/19.
 */

public class TAMerchantListActivity extends BaseActivity {

    @Bind(R.id.tv_merchant_number)
    TextView tvMerchantNumber;
    @Bind(R.id.flv_merchant)
    FixedListView flvMerchant;
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
    @Bind(R.id.bsv_container)
    BounceScrollView bsvContainer;

    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    TaMerchantAdapter taMerchantAdapter;
    List<M_Merchant> listMerchant = new ArrayList<M_Merchant>();

    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_ta_merchant_list);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        getNetworkData();
    }

    private void initData() {
        userId = getIntent().getIntExtra(UserDetailActivity.USER_ID, -1);
        mContext = this;
        mActivity = this;

        taMerchantAdapter = new TaMerchantAdapter(mContext, listMerchant);
        flvMerchant.setAdapter(taMerchantAdapter);
    }

    private void initView() {
        lhTvTitle.setText("TA的服务");
    }

    private void initListener() {
        taMerchantAdapter.setOnAllClickListener(new TaMerchantAdapter.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                IntentUtil.intStart_activity(mActivity,
                        MerchantDetailActivity.class, new Pair<String, Integer>(MerchantDetailActivity.MERCHANT_ID, taMerchantAdapter.getItem(position).merchantId));
            }
        });
    }

    private void getNetworkData() {
        showPd();
        getOtherMerchantList();
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
                    listMerchant = ((APIM_MerchantList) response).merchantList;

                    fillAdapter(listMerchant, false);
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
            tvMerchantNumber.setText("TA共在0家商户提供服务");
        } else {
            tvMerchantNumber.setText("TA共在" + list.size() + "家商户提供服务");
            taMerchantAdapter.setData(list, isLoadMore);
        }
        bsvContainer.post(new Runnable() {
            @Override
            public void run() {

                bsvContainer.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back})
    public void Onclick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                this.finish();
                break;
        }
    }
}
