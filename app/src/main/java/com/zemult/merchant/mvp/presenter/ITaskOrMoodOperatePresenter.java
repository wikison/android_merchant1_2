package com.zemult.merchant.mvp.presenter;


import com.zemult.merchant.fragment.HomeFragment;

/**
 * ITaskOrMoodOperatePresenter
 *
 * @author djy
 * @time 2016/7/20 9:37
 */
public interface ITaskOrMoodOperatePresenter {
    // 点赞
    void addLike(HomeFragment.HomeEnum homeEnum, int detailId);
    // 取消点赞
    void cancleLike(HomeFragment.HomeEnum homeEnum, int detailId);
    // 添加收藏
    void addStar(HomeFragment.HomeEnum homeEnum, int detailId);
    // 取消收藏
    void cancleStar(HomeFragment.HomeEnum homeEnum, int detailId);

    // 用户对角色任务点赞
    void addTaskLike(int taskIndustryRecordId);
    // 用户对心情小记取消点赞
    void cancleTaskLike(int taskIndustryRecordId);
    // 添加收藏--角色任务
    void addTaskStar(int taskIndustryRecordId);
    // 取消收藏--角色任务
    void cancleTaskStar(int taskIndustryRecordId);

    // 用户对心情小记点赞
    void addMoodLike(int newsId);
    // 用户对心情小记取消点赞
    void cancleMoodLike(int newsId);
    // 添加收藏--心情小记
    void addMoodStar(int newsId);
    // 取消收藏--心情小记
    void cancleMoodStar(int newsId);
}
