package com.zemult.merchant.mvp.presenter;



/**
 * ISearchDetailOperatePresenter
 * @author djy
 * @time 2016/10/25 10:59
 */
public interface ISearchDetailOperatePresenter {
    // 用户添加收藏--探索
    void addSearchDetailStar(int taskIndustryId);
    // 用户取消收藏--探索
    void cancleSearchDetailStar(int taskIndustryId);
}
