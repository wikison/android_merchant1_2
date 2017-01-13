package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.slash.UserIndustryListRequest;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.mvp.view.IUserIndustryView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * UserIndustryPresenter
 * @author djy
 * @time 2016/7/30 14:55
 */
public class UserIndustryPresenter extends BasePresenter implements IUserIndustryPresenter {
    private IUserIndustryView view;

    public UserIndustryPresenter(ArrayList<WeakReference<Request>> listJsonRequest, IUserIndustryView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    private UserIndustryListRequest userIndustryListRequest;

    @Override
    public void getUserIndustryList(int userId, int page, int rows, final boolean isLoadMore) {
        if (userIndustryListRequest != null) {
            userIndustryListRequest.cancel();
        }
        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        input.userId = userId;
        input.page = page;
        input.rows = rows;

        input.convertJosn();
        userIndustryListRequest = new UserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    view.setUserIndustryList(((APIM_MerchantGetrecruitroleList) response).industryList, isLoadMore,
                            ((APIM_MerchantGetrecruitroleList) response).maxpage);
                } else {
                    view.showError(((APIM_MerchantGetrecruitroleList) response).info);
                }
                view.stopRefreshOrLoad();
            }
        });
        sendJsonRequest(userIndustryListRequest);
    }
}
