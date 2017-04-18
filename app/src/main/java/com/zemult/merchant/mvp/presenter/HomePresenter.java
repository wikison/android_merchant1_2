package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.activity.slash.User2FirstSaleUserRequest;
import com.zemult.merchant.aip.slash.MerchantFirstPageListRequest;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.mvp.view.IHomeView;
import com.zemult.merchant.util.SlashHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zema.volley.network.ResponseListener;

/**
 * 首页p
 * @author djy
 * @time 2016/8/31 11:08
 */
public class HomePresenter extends BasePresenter implements IHomePresenter {
    private IHomeView view;
    private int industryId, page, rows;
    private boolean isLoadMore;

    public HomePresenter(ArrayList<WeakReference<Request>> listJsonRequest, IHomeView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    private MerchantFirstPageListRequest merchantFirstPageListRequest; // 首页商家列表          全是已上线的

    @Override
    public void merchant_firstpage_List(final M_Merchant myMerchant) {
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
                    List<M_Merchant> merchantList = ((APIM_MerchantList) response).merchantList;

                    if(myMerchant != null){
                        if(merchantList == null)
                            merchantList = new ArrayList<>();
                        merchantList.add(0, myMerchant);
                    }

                    view.setMerchantList(merchantList, isLoadMore,
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


    private User2FirstSaleUserRequest firstSaleUserRequest;

    @Override
    public void user2_first_saleUser() {
        if (firstSaleUserRequest != null) {
            firstSaleUserRequest.cancel();
        }
        User2FirstSaleUserRequest.Input input = new User2FirstSaleUserRequest.Input();
        input.operateUserId = SlashHelper.userManager().getUserId();
        input.center = Constants.CENTER;
        input.convertJosn();

        firstSaleUserRequest = new User2FirstSaleUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.stopRefreshOrLoad();
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                if (((CommonResult) response).status == 1) {
                    CommonResult result = (CommonResult) response;
                    M_Merchant myMerchant = null;
                    if(result.isSaleUser == 1){
                        myMerchant = new M_Merchant();
                        myMerchant.setMerchantId(result.merchantId);
                        myMerchant.setName(result.name);
                        myMerchant.setSaleUserId(result.saleUserId);
                        myMerchant.setSaleUserName(result.saleUserName);
                        myMerchant.setSaleUserHead(result.saleUserHead);
                        myMerchant.setPerMoney(result.perMoney);
                        myMerchant.setDistance(result.distance);
                        myMerchant.setSaleUserFanNum(result.saleUserFanNum);
                        myMerchant.setSaleUserExperience(result.saleUserExperience);
                        myMerchant.setSaleUserSumScore(result.saleUserSumScore);
                        myMerchant.setReviewstatus(result.reviewstatus);
                        myMerchant.setPic(result.pic);
                        myMerchant.setPics(result.pics);
                        myMerchant.setSaleUserTags(result.saleUserTags);
                    }
                    merchant_firstpage_List(myMerchant);
                } else {
                    view.showError(((CommonResult) response).info);
                }
                view.stopRefreshOrLoad();
                view.hideProgressDialog();
            }
        });
        sendJsonRequest(firstSaleUserRequest);
    }

    @Override
    public void merchant_firstpage(int industryId, int page, int rows, boolean isLoadMore) {
        this.industryId = industryId;
        this.page = page;
        this.rows = rows;
        this.isLoadMore = isLoadMore;
        if(industryId == -1
                && isLoadMore == false
                && SlashHelper.userManager().getUserId() != 0)
            user2_first_saleUser();
        else
            merchant_firstpage_List(null);
    }
}
