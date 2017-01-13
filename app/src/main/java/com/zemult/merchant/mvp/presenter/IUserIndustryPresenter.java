package com.zemult.merchant.mvp.presenter;

/**
 * IUserIndustryPresenter
 * @author djy
 * @time 2016/7/30 14:50
 */
public interface IUserIndustryPresenter {
    // 获取 用户的角色列表
    void getUserIndustryList(int userId, int page, int rows, boolean isLoadMore);
}
