package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.slash.MerchantFirstPageListRequest;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * 首页p
 * @author djy
 * @time 2016/8/31 11:08
 */
public class HomePresenter extends BasePresenter implements IHomePresenter {
    private IHomeView view;

    public HomePresenter(ArrayList<WeakReference<Request>> listJsonRequest, IHomeView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    private MerchantFirstPageListRequest merchantFirstPageListRequest; // 首页商家列表          全是已上线的

    @Override
    public void merchant_firstpage_List(int industryId, int page, int rows, final boolean isLoadMore) {
        if (merchantFirstPageListRequest != null) {
            merchantFirstPageListRequest.cancel();
        }
        MerchantFirstPageListRequest.Input input = new MerchantFirstPageListRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.industryId = industryId;
        input.page = page;
        input.rows = rows;
        input.city = Constants.CITYID;
        input.center = Constants.CENTER;
        input.convertJosn();

        merchantFirstPageListRequest = new MerchantFirstPageListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
                view.hideProgressDialog();
                view.setMerchantList(null, isLoadMore, 1);
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_MerchantList) response).status == 1) {
                    view.setMerchantList(((APIM_MerchantList) response).merchantList, isLoadMore,
                            ((APIM_MerchantList) response).maxpage );
                } else {
//                    view.showError(((APIM_MerchantList) response).info);
                    view.setMerchantList(null, isLoadMore, 1);
                }
                view.stopRefreshOrLoad();
                view.hideProgressDialog();
            }
        });
        sendJsonRequest(merchantFirstPageListRequest);
    }
}
