package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Industry;

import java.util.List;

/**
 * 用户的所有角色列表
 *
 * @author djy
 * @time 2016/7/30 14:44
 */
public interface IUserIndustryView {
    void showError(String error);

    void setUserIndustryList(List<M_Industry> list, boolean isLoadMore, int maxpage);
    void stopRefreshOrLoad();
}
