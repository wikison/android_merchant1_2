package com.zemult.merchant.activity.mine;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.search.SearchUserAdpater;
import com.zemult.merchant.aip.slash.UserBlackListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

public class BlackListActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;

    private SearchUserAdpater mAdapter;
    private UserBlackListRequest blackListRequest;

    private Context mContext;
    private int page = 1;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_black_list);
    }

    @Override
    public void init() {
        initData();
        initView();
        initListener();
        user_black_list(false);
    }

    public void initData() {
        mContext = this;
    }

    private void initView() {
        lhTvTitle.setText("黑名单管理");
        mAdapter = new SearchUserAdpater(mContext, new ArrayList<M_Userinfo>());
        mAdapter.setFromBlackList();
        smoothListView.setAdapter(mAdapter);
    }

    private void initListener() {
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        mAdapter.setOnUserDetailClickListener(new SearchUserAdpater.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                gotoUserDetail(mAdapter.getItem(position));
            }
        });
        mAdapter.setOnAllClickListener(new SearchUserAdpater.OnAllClickListener() {
            @Override
            public void onAllClick(int position) {
                gotoUserDetail(mAdapter.getItem(position));
            }
        });
    }


    private void gotoUserDetail(M_Userinfo userinfo) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.USER_ID, userinfo.getUserId());
        intent.putExtra(UserDetailActivity.USER_NAME, userinfo.getUserName());
        intent.putExtra(UserDetailActivity.USER_HEAD, userinfo.getUserHead());
        intent.putExtra(UserDetailActivity.USER_SEX, userinfo.getSex());
        startActivity(intent);
    }

    //用户的黑名单列表
    private void user_black_list(final boolean isLoadMore) {
        if (blackListRequest != null) {
            blackListRequest.cancel();
        }
        UserBlackListRequest.Input input = new UserBlackListRequest.Input();

        input.userId = SlashHelper.userManager().getUserId();
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        blackListRequest = new UserBlackListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    fillAdapter(((APIM_SearchUsersList) response).blackUserList,
                            ((APIM_SearchUsersList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(mContext, ((APIM_SearchUsersList) response).info);
                }
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

        });
        sendJsonRequest(blackListRequest);
    }


    private void fillAdapter(List<M_Userinfo> list, int maxpage, boolean isLoadMore) {
        if (list == null || list.size() == 0) {
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
        user_black_list(false);
    }

    @Override
    public void onLoadMore() {
        user_black_list(true);
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
