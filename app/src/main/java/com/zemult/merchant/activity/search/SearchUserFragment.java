package com.zemult.merchant.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.search.SearchUserAdpater;
import com.zemult.merchant.aip.slash.UserSearchuserListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * 搜索vp的子fragment——用户
 *
 * @author djy
 * @time 2016/8/3 10:00
 */
public class SearchUserFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private SearchUserAdpater mAdapter;
    private UserSearchuserListRequest userSearchuserListRequest;

    private Context mContext;
    private int page = 1;
    private String key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_child, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    public void initData() {
        mContext = getActivity();
    }
    private void initView() {
        mAdapter = new SearchUserAdpater(mContext, new ArrayList<M_Userinfo>());
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

    private void gotoUserDetail(M_Userinfo userinfo){
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.USER_ID, userinfo.getUserId());
        intent.putExtra(UserDetailActivity.USER_NAME, userinfo.getUserName());
        intent.putExtra(UserDetailActivity.USER_HEAD, userinfo.getUserHead());
        intent.putExtra(UserDetailActivity.USER_SEX, userinfo.getSex());
        startActivity(intent);
    }

    public void search(String key){
        if(key.equals(this.key))
            return;

        showPd();
        this.key = key;
        getSearchUserList(false);
    }

    //获取用户列表
    private void getSearchUserList(final boolean isLoadMore) {
        if (userSearchuserListRequest != null) {
            userSearchuserListRequest.cancel();
        }
        UserSearchuserListRequest.Input input = new UserSearchuserListRequest.Input();

        input.operateUserId = SlashHelper.userManager().getUserId();
        input.name = key;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        userSearchuserListRequest = new UserSearchuserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopLoadMore();
                smoothListView.stopRefresh();
                dismissPd();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_SearchUsersList) response).status == 1) {
                    fillAdapter(((APIM_SearchUsersList) response).userList,
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
        sendJsonRequest(userSearchuserListRequest);
    }


    private void fillAdapter(List<M_Userinfo> list, int maxpage, boolean isLoadMore) {
        if (mAdapter == null) {
            initView();
            initListener();
        }
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
        getSearchUserList(false);
    }

    @Override
    public void onLoadMore() {
        getSearchUserList(true);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
