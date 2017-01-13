package com.zemult.merchant.mvp.presenter;


/**
 * ITaskDetailPresenter
 *
 * @author djy
 * @time 2016/7/20 9:37
 */
public interface ITaskDetailPresenter {
    // 查看角色任务记录详情(已经完成的)
    void getTaskDetail(int taskIndustryRecordId);
    // 查看角色任务详情(新任务/未完成/已结束)
    void task_industry_info(int taskIndustryId);
}
