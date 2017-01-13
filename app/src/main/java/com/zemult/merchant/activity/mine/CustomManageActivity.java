package com.zemult.merchant.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.mine.UserSaleUserListRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SearchView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/12/26.
 */

public class CustomManageActivity extends BaseActivity implements SmoothListView.ISmoothListViewListener{
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.searchview)
    SearchView searchview;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;


    List<M_Fan> mDatas = new ArrayList<M_Fan>();
    CommonAdapter commonAdapter;
    private Context mContext;


    private int page = 1;
    String name ="";


    UserSaleUserListRequest userSaleUserListRequest;

    @Override
    public void setContentView() {
        setContentView(R.layout.custommanage_activity);
    }

    @Override
    public void init() {
        mContext=this;
        showPd();
        userAttractLis();
        initView();
        initListener();
    }


    private void initView() {
        lhTvTitle.setText("客户管理");
        searchview.setTvCancelVisible(View.GONE);
        searchview.setBgColor(getResources().getColor(R.color.divider_c1));
        searchview.setSearchViewListener(new SearchView.SearchViewListener() {
            @Override
            public void onSearch(String text) {
               name = text;
                onRefresh();
            }
        });
        searchview.setStrHint("搜索");


    smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);

    }

    private void initListener() {

        smoothListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CustomManageActivity.this, UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, mDatas.get(position-1).getUserId());
                intent.putExtra(UserDetailActivity.USER_NAME, mDatas.get(position-1).getUserName());
                intent.putExtra(UserDetailActivity.USER_HEAD, mDatas.get(position-1).getUserHead());
                startActivity(intent);
            }
        });

    }


    //获取我的关注数据
    private void userAttractLis() {
        if (userSaleUserListRequest != null) {
            userSaleUserListRequest.cancel();
        }
        UserSaleUserListRequest.Input input = new UserSaleUserListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.name=name;
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的页数

        input.convertJosn();

        userSaleUserListRequest = new UserSaleUserListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();
                if (((APIM_UserFansList) response).status == 1) {
                    if (page == 1) {
                        mDatas = ((APIM_UserFansList) response).userList;//这边是获取到的list

                        if (mDatas == null || mDatas.size() == 0) {
                            smoothListView.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            smoothListView.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);

                            if (mDatas != null && !mDatas.isEmpty()) {
                                smoothListView.setAdapter(commonAdapter = new CommonAdapter<M_Fan>(mContext, R.layout.item_custommanage, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_Fan mfollow, final int position) {
                                        if (!TextUtils.isEmpty(mfollow.userHead)) {
                                            holder.setCircleImage(R.id.head_iv, mfollow.userHead);
                                        }
                                        if (!TextUtils.isEmpty(mfollow.userName))
                                            holder.setText(R.id.name_tv, mfollow.userName);
                                    }
                                });

                            }
                        }
                    } else {
                        mDatas.addAll(((APIM_UserFansList) response).userList);
                        commonAdapter.notifyDataSetChanged();
                    }
                    if (((APIM_UserFansList) response).maxpage <= page) {
                        smoothListView.setLoadMoreEnable(false);
                    } else {
                        smoothListView.setLoadMoreEnable(true);
                        page++;
                    }

                } else {
                    ToastUtils.show(mContext, ((APIM_UserFansList) response).info);
                }
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();

            }
        });
        sendJsonRequest(userSaleUserListRequest);
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
    public void onRefresh() {
        page = 1;
        userAttractLis();
    }

    @Override
    public void onLoadMore() {
        userAttractLis();
    }
}
