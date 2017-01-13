package com.zemult.merchant.mvp.presenter;

import com.zemult.merchant.fragment.HomeFragment;

/**
 * ICommentPresenter
 *
 * @author djy
 * @time 2016/7/28 10:07
 */
public interface ICommentPresenter {
    // 获取评论列表
    void getCommentList(HomeFragment.HomeEnum homeEnum, int id, int page, int rows, boolean isLoadMore);
    // 用户评价
    void addComment(HomeFragment.HomeEnum homeEnum, int id, String note, int type, int ruserId);
    // 用户删除评论
    void delComment(HomeFragment.HomeEnum homeEnum, int commentId);


    // 获取角色任务的评论列表
    void getTaskCommentList(int taskIndustryRecordId, int page, int rows, boolean isLoadMore);
    // 用户对角色任务评价
    void addTaskComment(int taskIndustryRecordId, String note, int type, int ruserId);
    // 用户删除自己的角色任务评论
    void delTaskComment(int commentId);

    // 获取单个心情小记下的评论列表
    void getMoodCommentList(int newsId, int page, int rows, boolean isLoadMore);
    // 用户对心情小记评论
    void addMoodComment(int newsId, String note, int type, int ruserId);
    // 用户删除自己的评论(心情小记)
    void delMoodComment(int commentId);

    void addCommentLike(int commentId, int pos);
    void cancleCommentLike(int commentId, int pos);
}
