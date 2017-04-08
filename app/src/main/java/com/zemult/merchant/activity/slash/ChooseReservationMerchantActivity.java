package com.zemult.merchant.activity.slash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.MerchantOtherMerchantListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.CreateBespeakActivity;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.SPUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lv)
    ListView lv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private MerchantOtherMerchantListRequest merchantOtherMerchantListRequest; // 挂靠的商家
    private Context mContext;
    private Activity mActivity;
    private int userId;// 用户id(要查看的用户)
    String actionFrom;

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

    private void initData() {
        userId = getIntent().getIntExtra(UserDetailActivity.USER_ID,  111019);
        actionFrom = getIntent().getStringExtra("actionFrom");
        mContext = this;
        mActivity = this;
    }
    private void initView() {
        lhTvTitle.setText("选择商户");
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                M_Merchant m_merchant = (M_Merchant) lv.getAdapter().getItem(position);
                if (null != actionFrom && actionFrom.equals("CreateBespeakActivity")) {
                    Intent intent = new Intent();
                    intent.putExtra("tags", m_merchant.tags);
                    intent.putExtra("shopName", m_merchant.name);
                    intent.putExtra("merchantId", m_merchant.merchantId);

                    setResult(RESULT_OK, intent);
                    finish();
                }
                if (null != actionFrom && actionFrom.equals("UserDetailActivity")) {
                    Intent intent = new Intent(mContext, CreateBespeakActivity.class);
                    intent.putExtra("serviceId", userId);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("m_merchant", m_merchant);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

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
                    fillAdapter(((APIM_MerchantList) response).merchantList);
                } else {
                    ToastUtils.show(mContext, ((APIM_MerchantList) response).info);
                }
                dismissPd();
            }
        });
        sendJsonRequest(merchantOtherMerchantListRequest);
    }


    // 填充数据
    private void fillAdapter(List<M_Merchant> list) {
        if (list == null || list.isEmpty()) {
            lv.setVisibility(View.GONE);
        } else {
            lv.setVisibility(View.VISIBLE);

            lv.setAdapter(new CommonAdapter<M_Merchant>(mContext, R.layout.item_choose_merchant, list) {
                @Override
                public void convert(CommonViewHolder holder, M_Merchant entity, final int position) {
                    // 商家名称
                    if (!TextUtils.isEmpty(entity.name))
                        holder.setText(R.id.tv_name, entity.name);
                    // 商家封面
                    if (!TextUtils.isEmpty(entity.pic))
                        holder.setRoundImage(R.id.iv_cover, entity.pic);
                }
            });
        }
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
