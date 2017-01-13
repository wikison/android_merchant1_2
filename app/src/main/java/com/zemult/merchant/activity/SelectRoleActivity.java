package com.zemult.merchant.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.UserMerchantIndustryListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

import static com.zemult.merchant.config.Constants.ROWS;

/**
 * Created by wikison on 2016/6/14.
 */
public class SelectRoleActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lv_my_role)
    SmoothListView lv_my_role;
    @Bind(R.id.lh_btn_back)
    Button lh_btn_back;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.ll_back)
    LinearLayout llBack;

    List<M_Industry> industryList = new ArrayList<M_Industry>();
    CommonAdapter commonAdapter;
    UserMerchantIndustryListRequest userMerchantIndustryListRequest;
    private int page = 1;
    int merchantId;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_select_role);
    }

    public void init() {
        merchantId = getIntent().getIntExtra("merchantId", 0);
        lhTvTitle.setText("记录斜杠生活");
        lv_my_role.setRefreshEnable(true);
        lv_my_role.setLoadMoreEnable(false);
        lv_my_role.setSmoothListViewListener(this);
        user_merchant_industryList();


        lv_my_role.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                IntentUtil.intStart_activity(SelectRoleActivity.this, RecordLifeActivity.class,
                        new Pair<String, Integer>("merchantId", merchantId), new Pair<String, Integer>("industryId", industryList.get(position - 1).industryId)

                );
            }
        });

    }


    //获取用户的单场景下的角色列表
    private void user_merchant_industryList() {
        if (userMerchantIndustryListRequest != null) {
            userMerchantIndustryListRequest.cancel();
        }


        UserMerchantIndustryListRequest.Input input = new UserMerchantIndustryListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.merchantId = merchantId;
        input.page = page;
        input.rows = ROWS;

        input.convertJosn();
        userMerchantIndustryListRequest = new UserMerchantIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lv_my_role.stopRefresh();
                lv_my_role.stopLoadMore();

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantGetrecruitroleList) response).status == 1) {
                    industryList = ((APIM_MerchantGetrecruitroleList) response).industryList;

                } else {
                    ToastUtils.show(SelectRoleActivity.this, ((APIM_MerchantGetrecruitroleList) response).info);
                }
                lv_my_role.stopRefresh();
                lv_my_role.stopLoadMore();
                lv_my_role.setAdapter(commonAdapter = new CommonAdapter<M_Industry>(SelectRoleActivity.this, R.layout.item_my_role, industryList) {
                    @Override
                    public void convert(CommonViewHolder holder, M_Industry m_industry, int position) {
                        holder.setImage(R.id.iv_role_head, m_industry.icon);
                        holder.setText(R.id.tv_role_name, m_industry.name);
                    }

                });
            }
        });
        sendJsonRequest(userMerchantIndustryListRequest);
    }

    @Override
    public void onRefresh() {
        page = 1;
        user_merchant_industryList();
    }

    @Override
    public void onLoadMore() {
        ++page;
        user_merchant_industryList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
