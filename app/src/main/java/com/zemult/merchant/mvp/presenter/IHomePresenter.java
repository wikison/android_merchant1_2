package com.zemult.merchant.mvp.presenter;

import com.zemult.merchant.model.M_Merchant;

/**
 * 首页p的接口
 * @author djy
 * @time 2016/8/31 11:01
 */
public interface IHomePresenter {
    // 首页商家列表          全是已上线的
    void merchant_firstpage_List(M_Merchant myMerchant);
    // 首页 用户自己所在的服务指数最高的商家
    void user2_first_saleUser();
    // 首页
    void merchant_firstpage(int industryId, int page, int rows, boolean isLoadMore);


}
