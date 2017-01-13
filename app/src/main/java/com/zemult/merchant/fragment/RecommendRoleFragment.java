package com.zemult.merchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.role.RoleHomeActivity;
import com.zemult.merchant.adapter.CommonAdapter;
import com.zemult.merchant.adapter.CommonViewHolder;
import com.zemult.merchant.aip.slash.CommonUserIndustryListRequest;
import com.zemult.merchant.aip.slash.ManagerAddmanagerRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;

import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustrychilds;

import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/9/1.
 */
//探索推荐--猜你喜欢
public class RecommendRoleFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {
    CommonUserIndustryListRequest commonUserIndustryListRequest;
    ManagerAddmanagerRequest managerAddmanagerRequest;
    @Bind(R.id.roles_lv)
    SmoothListView rolesLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;
    private int page = 1;
    private List<M_UserRole> mDatas = new ArrayList<M_UserRole>();
    CommonAdapter commonAdapter;
    public static final String Call_SLASHMENUWINDOW_REFRESH = "call_SlashMenuWindow_refresh";

    private boolean hasLoaded;

    @Override
    protected void lazyLoad() {
        if (!hasLoaded) {
            hasLoaded = true;
            showPd();
            commonUserIndustryList();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPd();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendrole, container, false);
        ButterKnife.bind(this, view);
        rolesLv.setRefreshEnable(true);
        rolesLv.setLoadMoreEnable(false);
        rolesLv.setSmoothListViewListener(this);
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        rolesLv.setRefreshEnable(true);
//        rolesLv.setLoadMoreEnable(false);
//        rolesLv.setSmoothListViewListener(this);
//        commonUserIndustryList();
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    //  获取推荐角色
    private void commonUserIndustryList() {

        if (commonUserIndustryListRequest != null) {
            commonUserIndustryListRequest.cancel();
        }

        CommonUserIndustryListRequest.Input input = new CommonUserIndustryListRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.page = page;
        input.rows = Constants.ROWS;     //每页显示的行数
        input.convertJosn();

        commonUserIndustryListRequest = new CommonUserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                rolesLv.stopRefresh();
                rolesLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                dismissPd();

                if (((APIM_CommonGetindustrychilds) response).status == 1) {

                    if (page == 1) {
                        mDatas = ((APIM_CommonGetindustrychilds) response).industryList;
                        if (mDatas == null || mDatas.size() == 0) {
                            rolesLv.setVisibility(View.GONE);
                            rlNoData.setVisibility(View.VISIBLE);
                        } else {
                            rolesLv.setVisibility(View.VISIBLE);
                            rlNoData.setVisibility(View.GONE);
                            for (M_UserRole bean : mDatas) {
                                bean.isManager = 0;
                            }
                            if (mDatas != null && !mDatas.isEmpty()) {
                                rolesLv.setAdapter(commonAdapter = new CommonAdapter<M_UserRole>(getActivity(), R.layout.item_recommendrole, mDatas) {
                                    @Override
                                    public void convert(CommonViewHolder holder, M_UserRole mUserRole, final int position) {

                                        if (!TextUtils.isEmpty(mUserRole.icon)) {
                                            holder.setCircleImage(R.id.head_iv, mUserRole.icon);
                                        }
//                                        holder.setText(R.id.src_tv, "根据" + mUserRole.typeName + "推荐");
                                        holder.setText(R.id.name_tv, mUserRole.name);
                                        holder.setText(R.id.note_tv, mUserRole.tag);
                                        holder.setText(R.id.others_tv, mUserRole.num + "人参与" + "   杠友" + mUserRole.friendNum + "人");
                                        holder.setroleState(R.id.go_btn, mUserRole.isManager);
                                        holder.setOnclickListener(R.id.go_btn, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (mDatas.get(position).isManager == 0) {       //未
                                                    manager_addmanager(mDatas.get(position).id, position); //申请

                                                } else if (mDatas.get(position).isManager == 1) {         //已经申请的状态
                                                    Intent intent = new Intent(getActivity(), RoleHomeActivity.class);
                                                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, mDatas.get(position).id);
                                                    getActivity().startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    } else {
                        mDatas.addAll(((APIM_CommonGetindustrychilds) response).industryList);
                        commonAdapter.notifyDataSetChanged();
                    }
                    if (((APIM_CommonGetindustrychilds) response).maxpage <= page) {
                    } else {
                        page++;
                    }

                } else {
                    ToastUtils.show(getActivity(), ((APIM_CommonGetindustrychilds) response).info);
                }
                rolesLv.stopRefresh();
                rolesLv.stopLoadMore();

            }
        });

        sendJsonRequest(commonUserIndustryListRequest);
    }


    //用户 成为某经营人角色(参与角色)
    private void manager_addmanager(int industryId, final int position) {
        if (managerAddmanagerRequest != null) {
            managerAddmanagerRequest.cancel();
        }
        ManagerAddmanagerRequest.Input input = new ManagerAddmanagerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.industryId = industryId;
        input.convertJosn();
        managerAddmanagerRequest = new ManagerAddmanagerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    ToastUtils.show(getActivity(), "参与成功");
                    mDatas.get(position).isManager = 1;
                    EventBus.getDefault().post(Call_SLASHMENUWINDOW_REFRESH);
                    commonAdapter.setDataChanged(mDatas);  //改变按钮样式
                    Intent intent = new Intent(getActivity(), RoleHomeActivity.class);
                    intent.putExtra(RoleHomeActivity.INTENT_INDUSTRY_ID, mDatas.get(position).id);
                    getActivity().startActivity(intent);

                } else {
                    ToastUtils.show(getActivity(), ((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(managerAddmanagerRequest);
    }

    @Override
    public void onRefresh() {
        page = 1;
        commonUserIndustryList();

    }

    @Override
    public void onLoadMore() {
        commonUserIndustryList();

    }
}
