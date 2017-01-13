package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.task.TaskIndustryInfoRequest;
import com.zemult.merchant.aip.task.TaskIndustryRecordInfoRequest;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.mvp.view.ITaskDetailView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * TaskDetailPresenter
 *
 * @author djy
 * @time 2016/7/20 9:11
 */
public class TaskDetailPresenter extends BasePresenter implements ITaskDetailPresenter {
    private ITaskDetailView view;

    public TaskDetailPresenter(ArrayList<WeakReference<Request>> listJsonRequest, ITaskDetailView taskDetailView) {
        setListJsonRequest(listJsonRequest);
        this.view = taskDetailView;
    }

    /**
     * 任务
     */
    private TaskIndustryRecordInfoRequest taskIndustryRecordInfoRequest; // 查看角色任务记录详情(已经完成的)
    private TaskIndustryInfoRequest taskIndustryInfoRequest; // 查看角色任务详情(新任务/未完成/已结束)

    @Override
    public void getTaskDetail(int taskIndustryRecordId) {
        if (taskIndustryRecordInfoRequest != null) {
            taskIndustryRecordInfoRequest.cancel();
        }
        TaskIndustryRecordInfoRequest.Input input = new TaskIndustryRecordInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId();
        }
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.convertJosn();

        taskIndustryRecordInfoRequest = new TaskIndustryRecordInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryInfo) response).status == 1) {
                    view.setTaskDetailInfo((((APIM_TaskIndustryInfo) response).taskIndustryRecordInfo));
                } else {
                    view.showError(((APIM_TaskIndustryInfo) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryRecordInfoRequest);
    }

    @Override
    public void task_industry_info(int taskIndustryId) {
        if (taskIndustryInfoRequest != null) {
            taskIndustryInfoRequest.cancel();
        }
        TaskIndustryInfoRequest.Input input = new TaskIndustryInfoRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.taskIndustryId = taskIndustryId;
        input.convertJosn();

        taskIndustryInfoRequest = new TaskIndustryInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_TaskIndustryInfo) response).status == 1) {
                    view.setTaskDetailInfo((((APIM_TaskIndustryInfo) response).taskIndustryInfo));
                } else {
                    view.showError(((APIM_TaskIndustryInfo) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryInfoRequest);
    }
}
