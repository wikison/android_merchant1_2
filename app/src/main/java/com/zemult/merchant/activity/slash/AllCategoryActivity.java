package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.AllCategoryAdapter;
import com.zemult.merchant.aip.slash.CommonFirstpageAllIndustryListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Industry;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 015全部分类
 */
public class AllCategoryActivity extends BaseActivity {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.lh_btn_rightiamge)
    Button lhBtnRightiamge;
    @Bind(R.id.lv_category)
    SmoothListView lvCategory;
    String requesttype;
    private Context mContext;
    private List<M_Industry> categories;
    private AllCategoryAdapter adapter;
    // 获取首页的所有行业分类(包含行业)
    private CommonFirstpageAllIndustryListRequest allIndustryListRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_all_category);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();

        getNetworkData();
    }

    private void initData() {
        requesttype = getIntent().getStringExtra("requesttype");
        mContext = this;
        categories = new ArrayList<>();
    }

    private void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText(R.string.title_all_category);
        adapter = new AllCategoryAdapter(this, categories);
        lvCategory.setAdapter(adapter);
        lvCategory.setRefreshEnable(false);
        lvCategory.setLoadMoreEnable(false);
    }

    private void initListener() {
        adapter.setOnArrowClickListner(new AllCategoryAdapter.OnArrowClickListner() {
            @Override
            public void onArrowClick(int parPosition) {
                // + 1是因为第一项加了个下拉
                lvCategory.smoothScrollToPositionFromTop(parPosition + 1, 0);
            }
        });
        adapter.setOnItemClickListner(new AllCategoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(M_Industry industry) {
                if (null != requesttype && requesttype.equals(Constants.BROCAST_EDITMERCHANT)) {
                    Intent intent = new Intent();
                    intent.putExtra("industryId", industry.id);
                    intent.putExtra("industryName", industry.name);
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (null != requesttype && requesttype.equals(Constants.BROCAST_PUBLISH_TASK_ROLE)) {
                    Intent intent = new Intent(mContext, IndustryRoleActivity.class);
                    intent.putExtra("industryId", industry.id);
                    intent.putExtra("industryName", industry.name);
                    intent.putExtra("requestType", requesttype);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(mContext, SingleKindPlanListActivity.class);
                    intent.putExtra(SingleKindPlanListActivity.INDUSTRY_ID, industry.id);
                    intent.putExtra(SingleKindPlanListActivity.INDUSTRY_NAME, industry.name);
                    startActivity(intent);
                }
            }
        });
    }

    private void getNetworkData() {
        common_firstpage_allindustryList();
    }


    // 获取首页的所有行业分类(包含行业)
    private void common_firstpage_allindustryList() {
        showPd();
        if (allIndustryListRequest != null) {
            allIndustryListRequest.cancel();
        }
        allIndustryListRequest = new CommonFirstpageAllIndustryListRequest(new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_CommonGetallindustry) response).status == 1) {
                    setData(((APIM_CommonGetallindustry) response).typeList);
                } else {
                    ToastUtils.show(mContext, ((APIM_CommonGetallindustry) response).info);
                }
            }
        });
        sendJsonRequest(allIndustryListRequest);
    }

    private void setData(List<M_Industry> list) {
        if (list != null && !list.isEmpty()) {
            adapter.setData(list);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
                onBackPressed();
                break;
            case R.id.ll_back:
                onBackPressed();
                break;
        }
    }
}
