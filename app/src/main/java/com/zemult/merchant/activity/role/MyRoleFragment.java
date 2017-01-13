package com.zemult.merchant.activity.role;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.DiscoverRecommendActivity;
import com.zemult.merchant.adapter.role.MyRoleAdapter;
import com.zemult.merchant.aip.mine.UserIndustryListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_UserIndustryList;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class MyRoleFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.swipeLayout)
    SmoothListView smoothListView;

    private UserIndustryListRequest userIndustryListRequest;
    private List<M_UserRole> roledatas = new ArrayList<M_UserRole>();
    private MyRoleAdapter rolelistAdapter;
    int page = 1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.myrole_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;

    }



    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
        getUserMerchantList(false);
    }

    public void initView() {
        lhTvTitle.setVisibility(View.VISIBLE);
        lhTvTitle.setText("角色");
        llBack.setVisibility(View.GONE);
        View lvhead = LayoutInflater.from(getActivity()).inflate(R.layout.myrolehead, null);
        LinearLayout ll_new_friend = (LinearLayout) lvhead.findViewById(R.id.ll_add_role);
        ll_new_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.start_activity(getActivity(), DiscoverRecommendActivity.class);
            }
        });
        smoothListView.addHeaderView(lvhead);
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        rolelistAdapter = new MyRoleAdapter(getActivity(), roledatas);
        smoothListView.setAdapter(rolelistAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /**
     * 获取我的场景列表
     */
    private void getUserMerchantList(final boolean isLoadMore) {
        if (userIndustryListRequest != null) {
            userIndustryListRequest.cancel();
        }
        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();

        userIndustryListRequest = new UserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserIndustryList) response).status == 1) {
                    fillAdapter(((APIM_UserIndustryList) response).industryList,
                            ((APIM_UserIndustryList) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_UserIndustryList) response).info);
                }


                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();

            }
        });
        sendJsonRequest(userIndustryListRequest);
    }


    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_UserRole> list, int maxpage, boolean isLoadMore) {
        if (list != null && !list.isEmpty()) {
            smoothListView.setLoadMoreEnable(page < maxpage);
            rolelistAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    public void onRefresh() {
        getUserMerchantList(false);
    }

    @Override
    public void onLoadMore() {
        getUserMerchantList(true);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        if (RoleHomeActivity.Call_SLASH_REFRESH.equals(s))
            getUserMerchantList(false);
    }
}
