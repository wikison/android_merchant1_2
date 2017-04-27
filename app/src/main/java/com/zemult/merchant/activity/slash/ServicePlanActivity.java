package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.slashfrgment.ServicePlanAdapter;
import com.zemult.merchant.aip.slash.User2PlanDelRequest;
import com.zemult.merchant.aip.slash.User2PlanInfoRequest;
import com.zemult.merchant.aip.slash.User2PlanListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Plan;
import com.zemult.merchant.model.apimodel.APIM_PlanInfo;
import com.zemult.merchant.model.apimodel.APIM_PlanList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;
import com.zemult.merchant.view.common.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class ServicePlanActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    // 查看单个方案 仅传planId
    public static final String INTENT_PLAN_ID = "planId";
    // 查看服务方案列表 传saleUserId merchantId
    public static final String INTENT_MERCHANT_ID = "merchantId";
    public static final String INTENT_SALEUSER_ID = "saleUserId";
    private static final int REQ_ADD = 100;
    private static final int REQ_EDIT = 200;

    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.ll_add_new_plan)
    LinearLayout llAddNewPlan;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private int saleUserId, merchantId, planId, page = 1;

    private ServicePlanAdapter mAdapter;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_service_plan);
    }

    @Override
    public void init() {
        lhTvTitle.setText("服务方案");
        mContext = this;

        saleUserId = getIntent().getIntExtra(INTENT_SALEUSER_ID, -1);
        merchantId = getIntent().getIntExtra(INTENT_MERCHANT_ID, -1);
        planId = getIntent().getIntExtra(INTENT_PLAN_ID, -1);

        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

        if(planId > 0){
            smoothListView.setRefreshEnable(false);
            use2_plan_info();
            return;
        }
        if(saleUserId == SlashHelper.userManager().getUserId()){
            llAddNewPlan.setVisibility(View.VISIBLE);
        }
        use2_plan_list(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back, R.id.ll_add_new_plan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                onBackPressed();
                break;
            case R.id.ll_add_new_plan:
                Intent intent = new Intent(mContext, NewServicePlanActivity.class);
                intent.putExtra(INTENT_MERCHANT_ID, merchantId);
                startActivityForResult(intent, REQ_ADD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            use2_plan_list(false);
    }

    // 服务方案详情
    private User2PlanInfoRequest planInfoRequest;

    private void use2_plan_info() {
        showPd();
        if (planInfoRequest != null) {
            planInfoRequest.cancel();
        }

        User2PlanInfoRequest.Input input = new User2PlanInfoRequest.Input();
        input.planId = planId;//	角色行业
        input.convertJosn();
        planInfoRequest = new User2PlanInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PlanInfo) response).status == 1) {
                    M_Plan plan = ((APIM_PlanInfo) response).planInfo;
                    if(plan != null){
                        List<M_Plan> list = new ArrayList<>();
                        list.add(plan);
                        fillAdapter(list, 1 ,false);
                    }
                } else {
                    ToastUtils.show(mContext, ((APIM_PlanInfo) response).info);
                }
                dismissPd();

            }
        });
        sendJsonRequest(planInfoRequest);
    }

    // 服务方案详情
    private User2PlanDelRequest planDelRequest;

    private void use2_plan_del(int planId, final int pos) {
        showPd();
        if (planDelRequest != null) {
            planDelRequest.cancel();
        }

        User2PlanDelRequest.Input input = new User2PlanDelRequest.Input();
        input.planId = planId;//	角色行业
        input.convertJosn();
        planDelRequest = new User2PlanDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtil.showMessage("删除成功");
                    mAdapter.delOneRecord(pos);
                } else {
                    ToastUtils.show(mContext, ((CommonResult) response).info);
                }
                dismissPd();

            }
        });
        sendJsonRequest(planDelRequest);
    }

    // 服务方案列表
    private User2PlanListRequest planListRequest;

    private void use2_plan_list(final boolean isLoadMore) {
        if (planListRequest != null) {
            planListRequest.cancel();
        }

        User2PlanListRequest.Input input = new User2PlanListRequest.Input();
        input.saleUserId = saleUserId;//
        input.merchantId = merchantId;//
        input.state = saleUserId == SlashHelper.userManager().getUserId() ? -1 : 1;//
        input.page = page;//
        input.rows = Constants.ROWS;
        input.convertJosn();
        planListRequest = new User2PlanListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PlanList) response).status == 1) {
                    fillAdapter(((APIM_PlanList) response).planList,
                            ((APIM_PlanList) response).maxpage,
                            isLoadMore);

                } else {
                    ToastUtil.showMessage(((APIM_PlanList) response).info);
                }
                dismissPd();
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
            }
        });
        sendJsonRequest(planListRequest);
    }

    // 填充数据
    private void fillAdapter(List<M_Plan> list, int maxpage, boolean isLoadMore) {
        if (mAdapter == null) {
            mAdapter = new ServicePlanAdapter(mContext, new ArrayList<M_Plan>());
            mAdapter.setSaleUserId(saleUserId);
            mAdapter.setOnItemClickListener(new ServicePlanAdapter.OnItemClickListener() {
                @Override
                public void onEditClick(M_Plan plan) {
                    Intent intent = new Intent(mContext, NewServicePlanActivity.class);
                    intent.putExtra(INTENT_PLAN_ID, plan.planId);
                    startActivityForResult(intent, REQ_EDIT);
                }

                @Override
                public void onChooseClick(M_Plan plan) {
//                    状态(0:未启用,1:已启用)
                    if(plan.state==0){
                        ToastUtil.showMessage("方案未启用不能选择");
                    }
                    else{
                        Intent intent =new Intent();
                        intent.putExtra("planId",plan.planId);
                        intent.putExtra("planName",plan.name);
                        setResult(RESULT_OK,intent);
                        finish();
                    }

                }

                @Override
                public void onLongClick(final int planId, final int pos) {
                    CommonDialog.showDialogListener(mContext, null, "取消", "确定", "删除该服务方案", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonDialog.DismissProgressDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            use2_plan_del(planId, pos);
                            CommonDialog.DismissProgressDialog();
                        }
                    });
                }
            });
            smoothListView.setAdapter(mAdapter);
        }
        if (list == null || list.isEmpty()) {
            smoothListView.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            smoothListView.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            smoothListView.setLoadMoreEnable(page < maxpage);
            mAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        use2_plan_list(false);
    }

    @Override
    public void onLoadMore() {
        use2_plan_list(true);
    }
}
