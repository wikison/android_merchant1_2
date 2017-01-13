package com.zemult.merchant.activity.slash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.common.CommonGetindustrychildsRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustrychilds;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by Wikison on 2016/8/3.
 */
public class IndustryRoleActivity extends BaseActivity {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    int industryId;
    String requestType;
    List<M_UserRole> mDatas = new ArrayList<>();
    CommonAdapter commonAdapter;
    CommonGetindustrychildsRequest commonGetindustrychildsRequest;
    private Context mContext;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_industry_role);
    }

    @Override
    public void init() {

        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        lhTvTitle.setText("选择角色");
        industryId = getIntent().getIntExtra("industryId", -1);
        requestType = getIntent().getStringExtra("requestType");
        smoothListView.setLoadMoreEnable(false);
    }

    private void initView() {
        getRoleList();
    }

    private void initListener() {
        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;
                if (requestType.equals(Constants.BROCAST_PUBLISH_TASK_ROLE)) {
                    Intent intent = new Intent(Constants.BROCAST_PUBLISH_TASK_ROLE);
                    intent.putExtra("role_id", mDatas.get(position).id);
                    intent.putExtra("role_name", mDatas.get(position).name);
                    setResult(RESULT_OK, intent);
                    sendBroadcast(intent);
                    finish();
                }
            }
        });

    }

    private void getRoleList() {
        if (commonGetindustrychildsRequest != null) {
            commonGetindustrychildsRequest.cancel();
        }

        CommonGetindustrychildsRequest.Input input = new CommonGetindustrychildsRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.name = "";
        input.page = 1;
        input.rows = Constants.ROWS * 2;
        input.convertJosn();

        commonGetindustrychildsRequest = new CommonGetindustrychildsRequest(input, new ResponseListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {

                if (((APIM_CommonGetindustrychilds) response).status == 1) {
                    mDatas = ((APIM_CommonGetindustrychilds) response).industryList;
                } else {
                    ToastUtils.show(IndustryRoleActivity.this, ((APIM_CommonGetindustrychilds) response).info);
                }

                smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_UserRole>(mContext, R.layout.item_select_slash, mDatas) {
                    @Override
                    public void convert(CommonViewHolder holder, M_UserRole userRole, final int position) {
                        holder.setCircleHasBorderImage(R.id.iv, userRole.icon, 0xffdcdcdc, 1);
                        holder.setCircleImage(R.id.iv, userRole.icon);
                        holder.setText(R.id.tv, userRole.name);
                        holder.setText(R.id.tv_describe, userRole.tag);
                        if (SlashHelper.userManager().getUserinfo() != null) {
                            holder.setText(R.id.tv_num, userRole.num + "人参与 " + "      杠友" + userRole.friendNum);
                        } else {
                            holder.setText(R.id.tv_num, userRole.num + "人参与");

                        }

                    }

                });

            }
        });
        sendJsonRequest(commonGetindustrychildsRequest);
    }

    @OnClick({R.id.lh_btn_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lh_btn_back:
            case R.id.ll_back:
                finish();
                break;
        }

    }
}
