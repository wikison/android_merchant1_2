package com.zemult.merchant.mvp.presenter;


/**
 * IPicPresenter
 * @author djy
 * @time 2016/11/25 9:16
 */
public interface IPicPresenter {
    // 获取商家相册的照片列表(非证件照)
    void merchant_picList(int merchantId);
    // 获取用户相册的照片列表
    void user_picList(int userId);

    // 商家添加相册照片
    void merchant_pic_add(int merchantId, String pics);
    // 用户添加相册照片
    void user_pic_add(int userId, String pics);

    // 商家删除相册照片
    void merchant_pic_del(int merchantId, String picIds);
    // 用户删除相册照片
    void user_pic_del(int userId, String picIds);
}
