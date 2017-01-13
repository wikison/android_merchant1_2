package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.task.UserFavoriteTaskIndustryAddRequest;
import com.zemult.merchant.aip.task.UserFavoriteTaskIndustryDelRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.mvp.view.ISearchDetailOperateView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * SearchDetailOperatePresenter
 * @author djy
 * @time 2016/10/25 11:00
 */
public class SearchDetailOperatePresenter extends BasePresenter implements ISearchDetailOperatePresenter {
    private ISearchDetailOperateView view;

    public SearchDetailOperatePresenter(ArrayList<WeakReference<Request>> listJsonRequest, ISearchDetailOperateView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }


    /**
     * 任务
     */
    private UserFavoriteTaskIndustryAddRequest userFavoriteTaskAddRequest; // 用户添加收藏
    private UserFavoriteTaskIndustryDelRequest userFavoriteTaskDelRequest; // 用户取消收藏


    @Override
    public void addSearchDetailStar(int taskIndustryId) {
        view.showProgressDialog();
        if (userFavoriteTaskAddRequest != null) {
            userFavoriteTaskAddRequest.cancel();
        }
        UserFavoriteTaskIndustryAddRequest.Input input = new UserFavoriteTaskIndustryAddRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryId = taskIndustryId; // 信息id

        input.convertJosn();
        userFavoriteTaskAddRequest = new UserFavoriteTaskIndustryAddRequest(input, new ResponseListener() {
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
    public void cancleSearchDetailStar(int taskIndustryId) {
        view.showProgressDialog();
        if (userFavoriteTaskDelRequest != null) {
            userFavoriteTaskDelRequest.cancel();
        }
        UserFavoriteTaskIndustryDelRequest.Input input = new UserFavoriteTaskIndustryDelRequest.Input();
        input.userId = SlashHelper.userManager().getUserId(); // 用户id
        input.taskIndustryId = taskIndustryId; // 信息id

        input.convertJosn();
        userFavoriteTaskDelRequest = new UserFavoriteTaskIndustryDelRequest(input, new ResponseListener() {
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


}
