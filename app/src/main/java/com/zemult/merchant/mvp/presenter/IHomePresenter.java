package com.zemult.merchant.mvp.presenter;

/**
 * 首页p的接口
 * @author djy
 * @time 2016/8/31 11:01
 */
public interface IHomePresenter {
    // 首页商家列表          全是已上线的
    void merchant_firstpage_List(int industryId, int page, int rows, boolean isLoadMore);
}
