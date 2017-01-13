package com.zemult.merchant.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.slash.TaskDetailActivity;
import com.zemult.merchant.activity.slash.UserDetailActivity;
import com.zemult.merchant.adapter.slashfrgment.HomeTaskAdapter;
import com.zemult.merchant.aip.task.TaskIndustryRecordListOther1_2Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordListTask1_2Request;
import com.zemult.merchant.app.BaseFragment;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.view.SmoothListView.SmoothListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import zema.volley.network.ResponseListener;

import static android.app.Activity.RESULT_OK;

/**
 * Created by admin on 2016/9/8.
 */
public class MoreDiscoverFragment extends BaseFragment implements SmoothListView.ISmoothListViewListener {

    @Bind(R.id.morediscover_lv)
    SmoothListView morediscoverLv;
    @Bind(R.id.rl_no_data)
    RelativeLayout rlNoData;

    private HomeTaskAdapter completeAdapter;
    private TaskIndustryRecordListTask1_2Request taskIndustryRecordListTask12Request;
    private int taskIndustryId;
    private int taskIndustryRecordId, pushtype;
    private int page = 1;
    private boolean hasLoaded = false;
    private List<M_Task> datas = new ArrayList<M_Task>();
    private int orderType;//标识网络获取方法
    private TaskIndustryRecordListOther1_2Request recordListOther12Request;
    public static final int FOR_REQUEST = 111;

    public MoreDiscoverFragment() {
    }

    public MoreDiscoverFragment(int taskIndustryId, int taskIndustryRecordId, int pushtype) {
        this.taskIndustryId = taskIndustryId;
        this.taskIndustryRecordId = taskIndustryRecordId;
        this.pushtype = pushtype;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderType = getArguments().getInt("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_morediscover, null, false);
        ButterKnife.bind(this, view);
        morediscoverLv.setRefreshEnable(true);
        morediscoverLv.setLoadMoreEnable(false);
        morediscoverLv.setSmoothListViewListener(this);
        return view;
    }

    private void initView() {
        completeAdapter = new HomeTaskAdapter(getActivity(), datas);
        if(pushtype == 1)
            completeAdapter.showActivity(true);


        morediscoverLv.setAdapter(completeAdapter);

    }

    private void initListener() {
        // 方案详情
        completeAdapter.setOnPlanDetailClickListener(new HomeTaskAdapter.OnPlanDetailClickListener() {
            @Override
            public void onPlanDetailClick(int position) {

                M_Task task = completeAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TaskDetailActivity.class);
                intent.putExtra(TaskDetailActivity.INTENT_TASK, task);
                startActivityForResult(intent, FOR_REQUEST);
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



    private void getNetworkData(boolean isLoadMore) {
        if (taskIndustryRecordId == -111) {  //从探索详情进来
            task_industry_recordList_task_1_2(isLoadMore);
        } else {                               //从探索完成详情进来
            task_industry_recordList_other_1_2(isLoadMore);

        }
    }


    public void task_industry_recordList_task_1_2(final boolean isLoadMore) {
        if (taskIndustryRecordListTask12Request != null) {
            taskIndustryRecordListTask12Request.cancel();
        }
        TaskIndustryRecordListTask1_2Request.Input input = new TaskIndustryRecordListTask1_2Request.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.taskIndustryId = taskIndustryId; //角色任务id
        input.orderType = orderType;
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.convertJosn();
        taskIndustryRecordListTask12Request = new TaskIndustryRecordListTask1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                morediscoverLv.stopRefresh();
                morediscoverLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    setCompleteList(((APIM_TaskIndustryListNew) response).recordList, isLoadMore,
                            ((APIM_TaskIndustryListNew) response).maxpage);
                } else {
                    ToastUtil.showMessage(((APIM_TaskIndustryListNew) response).info);
                }
                dismissPd();
                morediscoverLv.stopRefresh();
                morediscoverLv.stopLoadMore();

            }
        });

        sendJsonRequest(taskIndustryRecordListTask12Request);
    }

    public void task_industry_recordList_other_1_2(final boolean isLoadMore) {
        if (recordListOther12Request != null) {
            recordListOther12Request.cancel();
        }
        TaskIndustryRecordListOther1_2Request.Input input = new TaskIndustryRecordListOther1_2Request.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id
        input.page = isLoadMore ? ++page : (page = 1);
        input.rows = Constants.ROWS;
        input.orderType = orderType;

        input.convertJosn();
        recordListOther12Request = new TaskIndustryRecordListOther1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissPd();
                morediscoverLv.stopRefresh();
                morediscoverLv.stopLoadMore();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {

                    setCompleteList(((APIM_TaskIndustryListNew) response).recordList, isLoadMore,
                            ((APIM_TaskIndustryListNew) response).maxpage);
                } else {
                    ToastUtil.showMessage(((APIM_TaskIndustryListNew) response).info);
                }
                dismissPd();
                morediscoverLv.stopRefresh();
                morediscoverLv.stopLoadMore();
            }
        });
        sendJsonRequest(recordListOther12Request);
    }


    private void setCompleteList(List<M_Task> recordList, boolean isLoadMore, int maxpage) {

        if (completeAdapter == null) {
            initView();
            initListener();
        }


        if (recordList == null || recordList.size() == 0) {
            morediscoverLv.setVisibility(View.GONE);
            rlNoData.setVisibility(View.VISIBLE);
        } else {
            morediscoverLv.setVisibility(View.VISIBLE);
            rlNoData.setVisibility(View.GONE);

            morediscoverLv.setLoadMoreEnable(page < maxpage);
            completeAdapter.setData(recordList, isLoadMore);
        }

    }

    @Override
    protected void lazyLoad() {

        if (!hasLoaded) {
            hasLoaded = true;
            showPd();
            getNetworkData(false);
        }

    }

    @Override
    public void onRefresh() {
        getNetworkData(false);


    }

    @Override
    public void onLoadMore() {
        getNetworkData(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FOR_REQUEST) {
            getNetworkData(false);
        }
    }
}
