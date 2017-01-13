package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.slash.ManagerNewsCommentAddRequest;
import com.zemult.merchant.aip.slash.ManagerNewsCommentDelRequest;
import com.zemult.merchant.aip.slash.ManagerNewsCommentListRequest;
import com.zemult.merchant.aip.task.TaskIndustryCommentAddRequest;
import com.zemult.merchant.aip.task.TaskIndustryCommentDelRequest;
import com.zemult.merchant.aip.task.TaskIndustryCommentGoodaddRequest;
import com.zemult.merchant.aip.task.TaskIndustryCommentGooddelRequest;
import com.zemult.merchant.aip.task.TaskIndustryCommentListRequest;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsCommentList;
import com.zemult.merchant.mvp.view.ICommentView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * CommentPresenter
 *
 * @author djy
 * @time 2016/7/28 10:08
 */
public class CommentPresenter extends BasePresenter implements ICommentPresenter {
    private ICommentView view;

    public CommentPresenter(ArrayList<WeakReference<Request>> listJsonRequest, ICommentView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    @Override
    public void getCommentList(HomeFragment.HomeEnum homeEnum, int id, int page, int rows, boolean isLoadMore) {
        switch (homeEnum){
            case TASK:
                getTaskCommentList(id, page, rows, isLoadMore);
                break;
            case MOOD:
                getMoodCommentList(id, page, rows, isLoadMore);
                break;
        }
    }


    @Override
    public void addComment(HomeFragment.HomeEnum homeEnum, int id, String note, int type, int ruserId) {
        switch (homeEnum){
            case TASK:
                addTaskComment(id, note, type, ruserId);
                break;
            case MOOD:
                addMoodComment(id, note, type, ruserId);
                break;
        }
    }

    @Override
    public void delComment(HomeFragment.HomeEnum homeEnum, int commentId) {
        switch (homeEnum){
            case TASK:
                delTaskComment(commentId);
                break;
            case MOOD:
                delMoodComment(commentId);
                break;
        }
    }

    /**
     * 心情
     */
    private ManagerNewsCommentListRequest commentListRequest; // 获取单个方案下的评论列表
    private ManagerNewsCommentAddRequest commentAddRequest; // 用户对方案评论
    private ManagerNewsCommentDelRequest commentDelRequest; // 用户删除自己的评论


    @Override
    public void getMoodCommentList(int newsId, int page, int rows, final boolean isLoadMore) {
        if (commentListRequest != null) {
            commentListRequest.cancel();
        }
        ManagerNewsCommentListRequest.Input input = new ManagerNewsCommentListRequest.Input();
        input.newsId = newsId;
        input.page = page;
        input.rows = rows;
        input.convertJosn();

        commentListRequest = new ManagerNewsCommentListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerNewsCommentList) response).status == 1) {
                    view.setCommentList(((APIM_ManagerNewsCommentList) response).commentList, isLoadMore,
                            ((APIM_ManagerNewsCommentList) response).maxpage);
                } else {
                    view.showError(((APIM_ManagerNewsCommentList) response).info);
                }
                view.stopRefreshOrLoad();
            }
        });
        sendJsonRequest(commentListRequest);
    }


    @Override
    public void addMoodComment(int newsId, String note, int type, int ruserId) {
        view.showProgressDialog();
        if (commentAddRequest != null) {
            commentAddRequest.cancel();
        }
        ManagerNewsCommentAddRequest.Input input = new ManagerNewsCommentAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.newsId = newsId; // 信息id
        input.note = note;
        input.type = type; // 类型(0:对信息评论;1:回复别人的评论 )
        if (type == 1)
            input.ruserId = ruserId;

        input.convertJosn();
        commentAddRequest = new ManagerNewsCommentAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addCommentSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commentAddRequest);
    }

    @Override
    public void delMoodComment(int commentId) {
        view.showProgressDialog();
        if (commentDelRequest != null) {
            commentDelRequest.cancel();
        }
        ManagerNewsCommentDelRequest.Input input = new ManagerNewsCommentDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.commentId = commentId; // 评论id(要删除的)

        input.convertJosn();
        commentDelRequest = new ManagerNewsCommentDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.delCommentSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commentDelRequest);
    }

    /**
     * 任务
     */
    private TaskIndustryCommentListRequest taskIndustryCommentListRequest; //   1.1 获取角色任务的评论列表
    private TaskIndustryCommentAddRequest taskIndustryCommentAddRequest; // 用户对角色任务评论
    private TaskIndustryCommentDelRequest taskIndustryCommentDelRequest; // 用户删除自己的角色任务评论


    @Override
    public void getTaskCommentList(int taskIndustryRecordId, int page, int rows, final boolean isLoadMore) {
        if (taskIndustryCommentListRequest != null) {
            taskIndustryCommentListRequest.cancel();
        }
        TaskIndustryCommentListRequest.Input input = new TaskIndustryCommentListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.taskIndustryRecordId = taskIndustryRecordId;
        input.page = page;
        input.rows = rows;
        input.convertJosn();

        taskIndustryCommentListRequest = new TaskIndustryCommentListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerNewsCommentList) response).status == 1) {
                    view.setCommentList(((APIM_ManagerNewsCommentList) response).commentList, isLoadMore,
                            ((APIM_ManagerNewsCommentList) response).maxpage );
                } else {
                    view.showError(((APIM_ManagerNewsCommentList) response).info);
                }
                view.stopRefreshOrLoad();
            }
        });
        sendJsonRequest(taskIndustryCommentListRequest);
    }


    @Override
    public void addTaskComment(int taskIndustryRecordId, String note, int type, int ruserId) {
        view.showProgressDialog();
        if (taskIndustryCommentAddRequest != null) {
            taskIndustryCommentAddRequest.cancel();
        }
        TaskIndustryCommentAddRequest.Input input = new TaskIndustryCommentAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordId = taskIndustryRecordId; // 信息id
        input.note = note;
        input.type = type; // 类型(0:对信息评论;1:回复别人的评论 )
        if (type == 1)
            input.ruserId = ruserId;

        input.convertJosn();
        taskIndustryCommentAddRequest = new TaskIndustryCommentAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addCommentSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryCommentAddRequest);
    }

    @Override
    public void delTaskComment(int commentId) {
        view.showProgressDialog();
        if (taskIndustryCommentDelRequest != null) {
            taskIndustryCommentDelRequest.cancel();
        }
        TaskIndustryCommentDelRequest.Input input = new TaskIndustryCommentDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.commentId = commentId; // 评论id(要删除的)

        input.convertJosn();
        taskIndustryCommentDelRequest = new TaskIndustryCommentDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.delCommentSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(taskIndustryCommentDelRequest);
    }


    private TaskIndustryCommentGoodaddRequest commentGoodaddRequest; // 用户对探索记录评论点赞
    private TaskIndustryCommentGooddelRequest commentGooddelRequest; // 用户对探索记录评论取消点赞

    @Override
    public void addCommentLike(int taskIndustryRecordCommentId, final int pos) {
        view.showProgressDialog();
        if (commentGoodaddRequest != null) {
            commentGoodaddRequest.cancel();
        }
        TaskIndustryCommentGoodaddRequest.Input input = new TaskIndustryCommentGoodaddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordCommentId = taskIndustryRecordCommentId; // 信息id

        input.convertJosn();
        commentGoodaddRequest = new TaskIndustryCommentGoodaddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addCommentLikeSuccess(pos);
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commentGoodaddRequest);
    }

    @Override
    public void cancleCommentLike(int taskIndustryRecordCommentId, final int pos) {
        view.showProgressDialog();
        if (commentGooddelRequest != null) {
            commentGooddelRequest.cancel();
        }
        TaskIndustryCommentGooddelRequest.Input input = new TaskIndustryCommentGooddelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryRecordCommentId = taskIndustryRecordCommentId; // 信息id

        input.convertJosn();
        commentGooddelRequest = new TaskIndustryCommentGooddelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.cancleCommentLikeSuccess(pos);
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(commentGooddelRequest);
    }
}
