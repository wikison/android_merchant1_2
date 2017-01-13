package com.zemult.merchant.activity.role;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.role.RoleClassifyAdapter;
import com.zemult.merchant.aip.slash.CommonAllIndustryListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.model.M_IndustryClass;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_CommonAllIndustryList;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/10/17.
 */
//角色分类
public class RoleClassifyFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.roles_lv)
    SmoothListView rolesLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    RoleClassifyAdapter roleClassifyAdapter;
    CommonAllIndustryListRequest commonAllIndustryListRequest;
    private boolean hasLoaded ;
    private List<M_UserRole> roledatas = new ArrayList<M_UserRole>();
    private List<M_IndustryClass>  alldatas = new ArrayList<M_IndustryClass>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPd();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roleclassify_fragment, null, false);
        ButterKnife.bind(this, view);
        rolesLv.setRefreshEnable(true);
        rolesLv.setLoadMoreEnable(false);
        rolesLv.setSmoothListViewListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  getNetworkData(false);
    }



    @Override
    protected void lazyLoad() {
        if (!hasLoaded) {
            hasLoaded = true;
            showPd();
            getNetworkData(false);
        }

    }


//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        // TODO Auto-generated method stub
//        if (isVisibleToUser) {
//            //fragment可见时加载数据
//
//            getNetworkData(false);
//        } else {
//            //不可见时不执行操作
//        }
//        super.setUserVisibleHint(isVisibleToUser);
//    }



    private void getNetworkData(boolean isLoadMore) {
        commonAllIndustryList(isLoadMore);
    }

    private void commonAllIndustryList(boolean isLoadMore) {
        if (commonAllIndustryListRequest != null) {
            commonAllIndustryListRequest.cancel();
        }
        CommonAllIndustryListRequest.Input input = new CommonAllIndustryListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.convertJosn();
        commonAllIndustryListRequest = new CommonAllIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                rolesLv.stopRefresh();
                rolesLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonAllIndustryList) response).status == 1) {
                    dismissPd();
                    if (null == roleClassifyAdapter) {
                        roleClassifyAdapter = new RoleClassifyAdapter(getActivity(), alldatas);
                        rolesLv.setAdapter(roleClassifyAdapter);
                    }
                    alldatas=((APIM_CommonAllIndustryList) response).typeList;
                    roleClassifyAdapter.setDatas(alldatas);

                }else {
                    ToastUtils.show(getActivity(), ((APIM_CommonAllIndustryList) response).info);
                }

                rolesLv.stopRefresh();
                rolesLv.stopLoadMore();
            }
        });
        sendJsonRequest(commonAllIndustryListRequest);
    }

    @Override
    public void onRefresh() {
        getNetworkData(false);

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
