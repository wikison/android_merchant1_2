package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Task;

import java.util.List;

/**
 * 完成任务列表的接口
 *
 * @author djy
 * @time 2016/7/28 13:02
 */
public interface ICompleteTaskView {
    void showError(String error);

    void setCompleteList(List<M_Task> list, boolean isLoadMore, int maxpage);
    void stopRefreshOrLoad();
}
