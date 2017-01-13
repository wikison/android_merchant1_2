package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.slash.ManagerNewsInfoRequest;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsInfo;
import com.zemult.merchant.mvp.view.IMoodDetailView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * MoodDetailPresenter
 *
 * @author djy
 * @time 2016/7/20 9:11
 */
public class MoodDetailPresenter extends BasePresenter implements IMoodDetailPresenter {
    private IMoodDetailView view;

    public MoodDetailPresenter(ArrayList<WeakReference<Request>> listJsonRequest, IMoodDetailView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    /**
     * 心情
     */
    private ManagerNewsInfoRequest newsInfoRequest; // 查看方案详情

    @Override
    public void getMoodDetail(int newsId) {
        if (newsInfoRequest != null) {
            newsInfoRequest.cancel();
        }
        ManagerNewsInfoRequest.Input input = new ManagerNewsInfoRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.operateUserId = SlashHelper.userManager().getUserId();
        }
        input.newsId = newsId;
        input.convertJosn();

        newsInfoRequest = new ManagerNewsInfoRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_ManagerNewsInfo) response).status == 1) {
                    view.setMoodDetailInfo(((APIM_ManagerNewsInfo) response).newsInfo);
                } else {
                    view.showError(((APIM_ManagerNewsInfo) response).info);
                }
            }
        });
        sendJsonRequest(newsInfoRequest);
    }
}
