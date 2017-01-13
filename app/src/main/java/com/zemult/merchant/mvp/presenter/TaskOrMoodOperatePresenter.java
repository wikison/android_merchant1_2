package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.mine.UserFavoriteAddRequest;
import com.zemult.merchant.aip.mine.UserFavoriteDeleteRequest;
import com.zemult.merchant.aip.slash.ManagerNewsGoodaddRequest;
import com.zemult.merchant.aip.slash.ManagerNewsGooddelRequest;
import com.zemult.merchant.aip.task.TaskIndustryGoodaddRequest;
import com.zemult.merchant.aip.task.TaskIndustryGooddelRequest;
import com.zemult.merchant.aip.task.UserFavoriteTaskAddRequest;
import com.zemult.merchant.aip.task.UserFavoriteTaskDelRequest;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.mvp.view.ITaskOrMoodOperateView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * TaskDetailPresenter
 *
 * @author djy
 * @time 2016/7/20 9:11
 */
public class TaskOrMoodOperatePresenter extends BasePresenter implements ITaskOrMoodOperatePresenter {
    private ITaskOrMoodOperateView view;

    public TaskOrMoodOperatePresenter(ArrayList<WeakReference<Request>> listJsonRequest, ITaskOrMoodOperateView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    @Override
    public void addLike(HomeFragment.HomeEnum homeEnum, int detailId) {
        switch (homeEnum){
            case TASK:
                addTaskLike(detailId);
                break;
            case MOOD:
                addMoodLike(detailId);
                break;
        }

    }

    @Override
    public void cancleLike(HomeFragment.HomeEnum homeEnum, int detailId) {
        switch (homeEnum){
            case TASK:
                cancleTaskLike(detailId);
                break;
            case MOOD:
                cancleMoodLike(detailId);
                break;
        }
    }

    @Override
    public void addStar(HomeFragment.HomeEnum homeEnum, int detailId) {
        switch (homeEnum){
            case TASK:
                addTaskStar(detailId);
                break;
            case MOOD:
                addMoodStar(detailId);
                break;
        }
    }

    @Override
    public void cancleStar(HomeFragment.HomeEnum homeEnum, int detailId) {
        switch (homeEnum){
            case TASK:
                cancleTaskStar(detailId);
                break;
            case MOOD:
                cancleMoodStar(detailId);
                break;
        }
    }

    /**
     * 任务
     */
    private TaskIndustryGoodaddRequest taskIndustryGoodaddRequest; // 用户对角色任务点赞
    private TaskIndustryGooddelRequest taskIndustryGooddelRequest; // 用户对角色任务取消点赞
    private UserFavoriteTaskAddRequest userFavoriteTaskAddRequest; // 用户添加收藏--角色任务记录
    private UserFavoriteTaskDelRequest userFavoriteTaskDelRequest; // 用户取消收藏--角色任务记录

    @Override
    public void addTaskLike(int taskIndustryRecordId) {
        view.showProgressDialog();
        if (taskIndustryGoodaddRequest != null) {
            taskIndustryGoodaddRequest.cancel();
        }
        TaskIndustryGoodaddRequest.Input input = new TaskIndustryGoodaddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id

        input.convertJosn();
        taskIndustryGoodaddRequest = new TaskIndustryGoodaddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addLikeSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryGoodaddRequest);
    }

    @Override
    public void cancleTaskLike(int taskIndustryRecordId) {
        view.showProgressDialog();
        if (taskIndustryGooddelRequest != null) {
            taskIndustryGooddelRequest.cancel();
        }
        TaskIndustryGooddelRequest.Input input = new TaskIndustryGooddelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id

        input.convertJosn();
        taskIndustryGooddelRequest = new TaskIndustryGooddelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.cancleLikeSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryGooddelRequest);
    }

    @Override
    public void addTaskStar(int taskIndustryRecordId) {
        view.showProgressDialog();
        if (userFavoriteTaskAddRequest != null) {
            userFavoriteTaskAddRequest.cancel();
        }
        UserFavoriteTaskAddRequest.Input input = new UserFavoriteTaskAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id

        input.convertJosn();
        userFavoriteTaskAddRequest = new UserFavoriteTaskAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addStarSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userFavoriteTaskAddRequest);
    }

    @Override
    public void cancleTaskStar(int taskIndustryRecordId) {
        view.showProgressDialog();
        if (userFavoriteTaskDelRequest != null) {
            userFavoriteTaskDelRequest.cancel();
        }
        UserFavoriteTaskDelRequest.Input input = new UserFavoriteTaskDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id

        input.convertJosn();
        userFavoriteTaskDelRequest = new UserFavoriteTaskDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.cancleStarSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userFavoriteTaskDelRequest);
    }

    private ManagerNewsGoodaddRequest newsGoodaddRequest; // 用户对方案点赞
    private ManagerNewsGooddelRequest newsGooddelRequest; // 用户对方案取消点赞
    private UserFavoriteAddRequest favoriteAddRequest; // 添加收藏
    private UserFavoriteDeleteRequest favoriteDeleteRequest; // 取消收藏

    @Override
    public void addMoodLike(int newsId) {
        view.showProgressDialog();
        if (newsGoodaddRequest != null) {
            newsGoodaddRequest.cancel();
        }
        ManagerNewsGoodaddRequest.Input input = new ManagerNewsGoodaddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id

        input.convertJosn();
        newsGoodaddRequest = new ManagerNewsGoodaddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addLikeSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(newsGoodaddRequest);
    }

    @Override
    public void cancleMoodLike(int newsId) {
        view.showProgressDialog();
        if (newsGooddelRequest != null) {
            newsGooddelRequest.cancel();
        }
        ManagerNewsGooddelRequest.Input input = new ManagerNewsGooddelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id

        input.convertJosn();
        newsGooddelRequest = new ManagerNewsGooddelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.cancleLikeSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(newsGooddelRequest);
    }

    @Override
    public void addMoodStar(int newsId) {
        view.showProgressDialog();
        if (favoriteAddRequest != null) {
            favoriteAddRequest.cancel();
        }
        UserFavoriteAddRequest.Input input = new UserFavoriteAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id

        input.convertJosn();
        favoriteAddRequest = new UserFavoriteAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addStarSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(favoriteAddRequest);
    }

    @Override
    public void cancleMoodStar(int newsId) {
        view.showProgressDialog();
        if (favoriteDeleteRequest != null) {
            favoriteDeleteRequest.cancel();
        }
        UserFavoriteDeleteRequest.Input input = new UserFavoriteDeleteRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id

        input.convertJosn();
        favoriteDeleteRequest = new UserFavoriteDeleteRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.cancleStarSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(favoriteDeleteRequest);
    }
}
