package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.task.TaskIndustryRecordListOther1_2Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordListTask1_2Request;
import com.zemult.merchant.aip.task.TaskIndustryRecordListTaskRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.mvp.view.ICompleteTaskView;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * CompleteListPresenter
 *
 * @author djy
 * @time 2016/7/20 9:11
 */
public class CompleteTaskPresenter extends BasePresenter implements ICompleteTaskPresenter {
    private ICompleteTaskView view;

    public CompleteTaskPresenter(ArrayList<WeakReference<Request>> listJsonRequest, ICompleteTaskView recordListView) {
        setListJsonRequest(listJsonRequest);
        this.view = recordListView;
    }

    private TaskIndustryRecordListOther1_2Request recordListOther12Request;
    private TaskIndustryRecordListTaskRequest taskIndustryRecordListTaskRequest;
    private TaskIndustryRecordListTask1_2Request taskIndustryRecordListTask12Request;

    // 查看探索完成详情的其它更多列表
    @Override
    public void task_industry_recordList_other_1_2(int taskIndustryRecordId, int orderType, int page, int rows, final boolean isLoadMore) {
        if (recordListOther12Request != null) {
            recordListOther12Request.cancel();
        }
        TaskIndustryRecordListOther1_2Request.Input input = new TaskIndustryRecordListOther1_2Request.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id
        input.page = page;
        input.rows = rows;
        input.orderType = orderType;

        input.convertJosn();
        recordListOther12Request = new TaskIndustryRecordListOther1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    view.setCompleteList(((APIM_TaskIndustryListNew) response).recordList, isLoadMore,
                            ((APIM_TaskIndustryListNew) response).maxpage);
                } else {
                    view.showError(((APIM_TaskIndustryListNew) response).info);
                }
                view.stopRefreshOrLoad();
            }
        });
        sendJsonRequest(recordListOther12Request);
    }

    @Override
    public void task_industry_recordList_task_1_2(int taskIndustryId, int orderType, int page, int rows, final boolean isLoadMore) {
        if (taskIndustryRecordListTask12Request != null) {
            taskIndustryRecordListTask12Request.cancel();
        }
        TaskIndustryRecordListTask1_2Request.Input input = new TaskIndustryRecordListTask1_2Request.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.taskIndustryId = taskIndustryId; //角色任务id
        input.orderType = orderType;
        input.page = page;
        input.rows = rows;
        input.convertJosn();
        taskIndustryRecordListTask12Request = new TaskIndustryRecordListTask1_2Request(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    view.setCompleteList(((APIM_TaskIndustryListNew) response).recordList, isLoadMore,
                            ((APIM_TaskIndustryListNew) response).maxpage);
                } else {
                    ToastUtil.showMessage(((APIM_TaskIndustryListNew) response).info);
                }
                view.stopRefreshOrLoad();
            }
        });

        sendJsonRequest(taskIndustryRecordListTask12Request);
    }
}
