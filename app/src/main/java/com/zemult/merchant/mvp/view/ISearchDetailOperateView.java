package com.zemult.merchant.mvp.view;

/**
 * ISearchDetailOperateView
 * @author djy
 * @time 2016/10/25 11:13
 */
public interface ISearchDetailOperateView {
    void showProgressDialog();
    void hideProgressDialog();
    void showError(String error);

    void addStarSuccess();
    void cancleStarSuccess();
}
