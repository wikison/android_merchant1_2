package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Task;

/**
 * 任务详情接口
 *
 * @author djy
 * @time 2016/7/20 15:26
 */
public interface ITaskDetailView {
    void showError(String error);

    void setTaskDetailInfo(M_Task taskIndustryInfo);
}
