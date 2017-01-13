package com.zemult.merchant.mvp.view;

/**
 * 心情小记或者任务记录 点赞收藏的操作
 *
 * @author djy
 * @time 2016/7/20 15:26
 */
public interface ITaskOrMoodOperateView {
    void showProgressDialog();
    void hideProgressDialog();
    void showError(String error);

    void addLikeSuccess();
    void cancleLikeSuccess();
    void addStarSuccess();
    void cancleStarSuccess();
}
