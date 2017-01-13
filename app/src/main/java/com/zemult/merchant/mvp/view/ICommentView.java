package com.zemult.merchant.mvp.view;

import com.zemult.merchant.model.M_Comment;

import java.util.List;

/**
 * 评论相关的接口
 *
 * @author djy
 * @time 2016/7/28 13:02
 */
public interface ICommentView {
    void showProgressDialog();
    void hideProgressDialog();
    void showError(String error);

    void setCommentList(List<M_Comment> list, boolean isLoadMore, int maxpage);
    void addCommentSuccess();
    void delCommentSuccess();
    void stopRefreshOrLoad();
    void addCommentLikeSuccess(int posistion);
    void cancleCommentLikeSuccess(int posistion);
}
