package com.zemult.merchant.mvp.presenter;

/**
 * ICompleteListPresenter
 *
 * @author djy
 * @time 2016/7/28 14:27
 */
public interface ICompleteTaskPresenter {
    // 查看探索完成详情的其它更多列表
    void task_industry_recordList_other_1_2(int taskIndustryRecordId, int orderType, int page, int rows, boolean isLoadMore);

    // 查看单任务已经完成的记录列表
    void task_industry_recordList_task_1_2(int taskIndustryId, int orderType, int page, int rows, boolean isLoadMore);
}
