package com.zemult.merchant.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.adapter.createroleadapter.RolelistAdapter;
import com.zemult.merchant.aip.common.CommonGetindustrychildsRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustrychilds;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/4/15.
 */
public class CreateRoleFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    protected WeakReference<View> mRootView;
    CommonGetindustrychildsRequest commonGetindustrychildsRequest;
    String name = "";
    int page = 1;
    //各个fragment
    private boolean isPrepared;
    private int industryId;
    private View view;
    private RelativeLayout relative_role_show;
    //    MerchantSearchmerchantListRequest merchantSearchmerchantListRequest;
    private SmoothListView swipeLayout;
    private List<M_UserRole> roledatas = new ArrayList<M_UserRole>();
    private RolelistAdapter rolelistAdapter;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取当前界面属于那个标签下面,获取标签ID
        industryId = getArguments().getInt("data");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //保存当前界面，防止重新创建
        if (mRootView == null || mRootView.get() == null) {
            view = inflater.inflate(R.layout.createrole_fragment, container, false);
            mRootView = new WeakReference<View>(view);
            isPrepared = true;
            InitFind();//初始化数据
            lazyLoad();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.get().getParent();
            if (parent != null) {
                parent.removeView(mRootView.get());
            }
        }

        swipeLayout.setRefreshEnable(true);
        swipeLayout.setLoadMoreEnable(false);
        swipeLayout.setSmoothListViewListener(this);

        return mRootView.get();
    }

    private void InitFind() {
        relative_role_show = (RelativeLayout) view.findViewById(R.id.relative_role_show);
        swipeLayout = (SmoothListView) view.findViewById(R.id.swipeLayout);

    }

    private void getNetworkData(boolean isLoadMore) {
        commonGetindustrychilds(isLoadMore);

    }


    //获取单个行业下的角色列表
    private void commonGetindustrychilds(final boolean isLoadMore) {
        if (commonGetindustrychildsRequest != null) {
            commonGetindustrychildsRequest.cancel();
        }
        CommonGetindustrychildsRequest.Input input = new CommonGetindustrychildsRequest.Input();

        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.name = name;//名称(模糊)
        input.industryId = industryId;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        commonGetindustrychildsRequest = new CommonGetindustrychildsRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeLayout.stopRefresh();
                swipeLayout.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {

                Log.i("aaa","1"+((APIM_CommonGetindustrychilds) response).status);

                if (((APIM_CommonGetindustrychilds) response).status == 1) {
                    relative_role_show.setVisibility(View.GONE);
                    swipeLayout.setVisibility(View.VISIBLE);
                    if (null == rolelistAdapter) {
                        rolelistAdapter = new RolelistAdapter(getActivity(), roledatas);
                        swipeLayout.setAdapter(rolelistAdapter);
                    }
                    fillAdapter(((APIM_CommonGetindustrychilds) response).industryList,
                            ((APIM_CommonGetindustrychilds) response).maxpage,
                            isLoadMore);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_CommonGetindustrychilds) response).info);
                }
                swipeLayout.stopRefresh();
                swipeLayout.stopLoadMore();

            }
        });
        sendJsonRequest(commonGetindustrychildsRequest);

    }


    //fragment懒加载装修
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        if (roledatas.size() < 1) {
            getNetworkData(false);
        }

    }

    /**
     * 设置数据
     */
    private void fillAdapter(final List<M_UserRole> list, int maxpage, boolean isLoadMore) {
        if (list != null && !list.isEmpty()) {
            swipeLayout.setLoadMoreEnable(page < maxpage);
            rolelistAdapter.setData(list, isLoadMore);
        }
    }




//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case 1:
//                    swipeLayout.setAdapter(rolelistAdapter);
//
//                    break;
//                case 2:
//                    relative_role_show.setVisibility(View.GONE);
//                    swipeLayout.setVisibility(View.VISIBLE);
//                    break;
//                case 3:
//                    break;
//                case 4:
//                    rolelistAdapter.notifyDataSetChanged();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };


    @Override
    public void onRefresh() {
        getNetworkData(false);
    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);
    }
}

