package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Merchant;

import java.util.List;

/**
 * 首页v的接口
 * @author djy
 * @time 2016/8/31 11:07
 */
public interface IHomeView {
    void hideProgressDialog();
    void showError(String error);

    void setMerchantList(List<M_Merchant> list, boolean isLoadMore, int maxpage);
    void stopRefreshOrLoad();
}
