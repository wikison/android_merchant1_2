package com.zemult.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.aip.discover.CommonGetadvertListRequest;
import com.zemult.merchant.aip.discover.TaskSearchIndustryRecordListRequest;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Ad;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_CommonGetadvertList;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.mvp.presenter.TaskDetailPresenter;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.HeaderAdViewView;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.ToastUtils;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/6/3.
 */
public class DiscoverFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener, ITaskDetailView {

    @Bind(R.id.lh_btn_back)
    Button lhBtnBack;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.lh_tv_title)
    TextView lhTvTitle;
    @Bind(R.id.lh_btn_right)
    Button lhBtnRight;
    @Bind(R.id.smoothListView)
    SmoothListView smoothListView;

    private Context mContext;
    private Activity mActivity;
    private int selectedTaskPos;
    private M_Task selectedTask;

    private static final int REQ_TASK_DETAIL = 0x110;
    private HeaderAdViewView listViewAdHeaderView; // 广告视图
    private HomeTaskAdapter completeAdapter;
    int page = 1;
    private boolean hasLoaded = false;
    private List<M_Task> mTaskList = new ArrayList<>();
    CommonGetadvertListRequest commonGetadvertListRequest;
    TaskSearchIndustryRecordListRequest taskSearchIndustryRecordListRequest;
    private TaskDetailPresenter taskDetailPresenter;

    private List<M_Ad> adList = new ArrayList<M_Ad>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initView();
        initListener();

        showPd();
        common_getadvertList();
        taskSearchIndustryRecordList(false);
    }

    private void initListener() {
        completeAdapter.setOnPlanDetailClickListener(new HomeTaskAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {
                selectedTaskPos = position;
                selectedTask = completeAdapter.getItem(position);
                M_Task task = completeAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivityForResult(intent, REQ_TASK_DETAIL);


            }
        });
        // 用户详情
        completeAdapter.setOnUserDetailClickListener(new HomeTaskAdapter.OnUserDetailClickListener() {
            @Override
            public void onUserDetailClick(int position) {
                Intent intent = new Intent(getActivity(), UserDetailActivity.class);
                intent.putExtra(UserDetailActivity.USER_ID, completeAdapter.getItem(position).userId);
                intent.putExtra(UserDetailActivity.USER_NAME, completeAdapter.getItem(position).userName);
                intent.putExtra(UserDetailActivity.USER_HEAD, completeAdapter.getItem(position).userHead);
                intent.putExtra(UserDetailActivity.USER_SEX, completeAdapter.getItem(position).userSex);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mContext = getActivity();
        mActivity = getActivity();

        taskDetailPresenter = new TaskDetailPresenter(listJsonRequest, this);

        M_Ad mad = new M_Ad();
        mad.setImg("");
        mad.setUrl("empty");
        adList.add(mad);
    }

    private void initView() {
        lhTvTitle.setText("发现");
        llBack.setVisibility(View.INVISIBLE);

        // 设置广告数据 加入到smoothListView的headerView
        listViewAdHeaderView = new HeaderAdViewView(mActivity);
        listViewAdHeaderView.fillView(adList, smoothListView);

        // 设置ListView数据
        smoothListView.setRefreshEnable(true);
        smoothListView.setLoadMoreEnable(false);
        smoothListView.setSmoothListViewListener(this);
        completeAdapter = new HomeTaskAdapter(mActivity, mTaskList);
        smoothListView.setAdapter(completeAdapter);


    }


    //获取  广告列表
    private void common_getadvertList() {
        if (commonGetadvertListRequest != null) {
            commonGetadvertListRequest.cancel();
        }
        CommonGetadvertListRequest.Input input = new CommonGetadvertListRequest.Input();
        input.page = 4;//页面编号(-1:表示全部;0:app开启页1:首页广告位2:我的斜杠3:我是商家 4:发现)

        input.convertJosn();
        commonGetadvertListRequest = new CommonGetadvertListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_CommonGetadvertList) response).status == 1) {
                    adList.clear();
                    adList.addAll(((APIM_CommonGetadvertList) response).advertList);
                    listViewAdHeaderView.setData(adList);
                } else {
                    ToastUtils.show(getActivity(), ((APIM_CommonGetadvertList) response).info);
                }
            }
        });
        sendJsonRequest(commonGetadvertListRequest);
    }

    public void taskSearchIndustryRecordList(final boolean isLoadMore) {
        if (taskSearchIndustryRecordListRequest != null) {
            taskSearchIndustryRecordListRequest.cancel();
        }
        TaskSearchIndustryRecordListRequest.Input input = new TaskSearchIndustryRecordListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;

        input.convertJosn();
        taskSearchIndustryRecordListRequest = new TaskSearchIndustryRecordListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    fillAdapter(((APIM_TaskSearchIndustryRecordList) response).recordList, isLoadMore,
                            ((APIM_TaskSearchIndustryRecordList) response).maxpage);
                } else {
                    ToastUtil.showMessage(((APIM_TaskSearchIndustryRecordList) response).info);
                }
                dismissPd();
                smoothListView.stopRefresh();
                smoothListView.stopLoadMore();
            }
        });
        sendJsonRequest(taskSearchIndustryRecordListRequest);
    }

    private void fillAdapter(List<M_Task> list, boolean isLoadMore, int maxpage) {
        if (list == null) {
            completeAdapter.setData(new ArrayList<M_Task>(), isLoadMore);
            smoothListView.setLoadMoreEnable(false);
        } else {
            smoothListView.setLoadMoreEnable(page < maxpage);
            completeAdapter.setData(list, isLoadMore);
        }
    }

    @Override
    protected void lazyLoad() {
//        if (!hasLoaded) {
//            hasLoaded = true;
//            showPd();
//            common_getadvertList();
//            taskSearchIndustryRecordList(false);
//        }
    }

    /**
     * =================================处理刷新请求========================================
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQ_TASK_DETAIL) {
                taskDetailPresenter.getTaskDetail(selectedTask.taskIndustryRecordId);
            }
        }
    }

    @Override
    public void showError(String error) {
        ToastUtils.show(mContext, error);
    }

    @Override
    public void setTaskDetailInfo(M_Task taskIndustryInfo) {
        completeAdapter.refreshOneRecord(taskIndustryInfo, selectedTaskPos);
    }

    @Override
    public void onRefresh() {
        taskSearchIndustryRecordList(false);
    }

    @Override
    public void onLoadMore() {
        taskSearchIndustryRecordList(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
