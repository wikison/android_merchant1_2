package com.zemult.merchant.activity.mine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.minefragment.BusinessManAdapter;
import com.zemult.merchant.aip.mine.MerchantRequestListRequest;
import com.zemult.merchant.aip.mine.MerchantUserMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 057我是商家页
 */
public class BusinessManActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener{
    public static final String TAG = BusinessManActivity.class.getSimpleName();
    private static final int MERCHANT_ADD_REQ = 0x110;
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


    private Context mContext;
    private Activity mActivity;
    private BusinessManAdapter mAdapter;
    private MerchantUserMerchantListRequest request;
    private List<M_Merchant> datas = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MERCHANT_ADD_REQ) {
                datas.clear();
                getUserMerchantList();
            }
        }
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_business_man);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getUserMerchantList();
    }

    private void initData() {
        mContext = this;
        mActivity = this;
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);

        lhTvTitle.setText(getResources().getString(R.string.title_business));
        tvRight.setText("入驻申请");

        mAdapter = new BusinessManAdapter(mContext, new ArrayList<M_Merchant>());
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        mAdapter.setItemClickListener(new BusinessManAdapter.OnBusinessMenItemClickListener() {
            @Override
            public void rightClick(int pos) {
                if(mAdapter.getItem(pos).daiqiyue){
                    ToastUtil.showMessage("商户审核中");
                    return;
                }
                Intent intent = new Intent(mContext, MerchantManageActivity.class);
                intent.putExtra("merchantId", mAdapter.getItem(pos).merchantId);
                startActivity(intent);
            }

            @Override
            public void manageClick(int pos) {
                Intent intent = new Intent(mContext, MerchantManagerManageActivity.class);
                intent.putExtra("merchantId", mAdapter.getItem(pos).merchantId);
                startActivity(intent);
            }

            @Override
            public void billClick(int pos) {
                Intent intent = new Intent(mContext, BusinessAccountActivity.class);
                intent.putExtra(BusinessAccountActivity.INTENT_MERCHANTID, mAdapter.getItem(pos).merchantId);
                startActivity(intent);
            }
        });
    }

    @OnClick({R.id.ll_back, R.id.lh_btn_back, R.id.tv_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.tv_right:
                Intent it = new Intent(mContext, MerchantEnter2Activity.class);
                startActivityForResult(it, MERCHANT_ADD_REQ);
                break;
        }
    }


    /**
     * 获取用户的商家列表
     */
    private void getUserMerchantList() {
        if (request != null) {
            request.cancel();
        }
        MerchantUserMerchantListRequest.Input input = new MerchantUserMerchantListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = 1;
        input.rows = 1000;

        input.convertJosn();

        request = new MerchantUserMerchantListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    if(((APIM_MerchantList) response).merchantList!= null
                            && !((APIM_MerchantList) response).merchantList.isEmpty())
                        datas.addAll(((APIM_MerchantList) response).merchantList);

                    getUserMerchantReqList();
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                smoothListView.stopRefresh();
            }
        });
        sendJsonRequest(request);
    }

    private MerchantRequestListRequest merchantRequestListRequest;

    /**
     * 获取商家申请列表(待签约)
     */
    private void getUserMerchantReqList() {
        if (merchantRequestListRequest != null) {
            merchantRequestListRequest.cancel();
        }
        MerchantRequestListRequest.Input input = new MerchantRequestListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = 1;
        input.rows = 1000;

        input.convertJosn();

        merchantRequestListRequest = new MerchantRequestListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    if(((APIM_MerchantList) response).requestList!= null
                            && !((APIM_MerchantList) response).requestList.isEmpty()){
                        for(M_Merchant m : ((APIM_MerchantList) response).requestList){
                            m.daiqiyue = true;
                        }
                        datas.addAll(((APIM_MerchantList) response).requestList);
                    }

                    fillAdapter();
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                smoothListView.stopRefresh();
            }
        });
        sendJsonRequest(merchantRequestListRequest);
    }

    private void fillAdapter() {
        if (!datas.isEmpty()) {
            smoothListView.setVisibility(View.VISIBLE);
            mAdapter.setData(datas);
        }else {
            smoothListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        datas.clear();
        getUserMerchantList();
    }

    @Override
    public void onLoadMore() {

    }
}
