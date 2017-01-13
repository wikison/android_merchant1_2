package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.zemult.merchant.aip.mine.MerchantPicAddRequest;
import com.zemult.merchant.aip.mine.MerchantPicDelRequest;
import com.zemult.merchant.aip.mine.MerchantPicListRequest;
import com.zemult.merchant.aip.mine.UserPicAddRequest;
import com.zemult.merchant.aip.mine.UserPicDelRequest;
import com.zemult.merchant.aip.mine.UserPicListRequest;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.mvp.view.IPicView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/11/25.
 */

public class PicPresenter extends BasePresenter implements IPicPresenter {
    private IPicView view;

    public PicPresenter(ArrayList<WeakReference<Request>> listJsonRequest, IPicView view) {
        setListJsonRequest(listJsonRequest);
        this.view = view;
    }

    private MerchantPicListRequest merchantPicListRequest;
    private UserPicListRequest userPicListRequest;
    private UserPicAddRequest userPicAddRequest;
    private UserPicDelRequest userPicDelRequest;
    private MerchantPicAddRequest merchangtPicAddRequest;
    private MerchantPicDelRequest merchangtPicDelRequest;

    @Override
    public void merchant_picList(int merchantId) {
        view.showProgressDialog();
        if (merchantPicListRequest != null) {
            merchantPicListRequest.cancel();
        }
        MerchantPicListRequest.Input input = new MerchantPicListRequest.Input();
        input.merchantId = merchantId;
        input.page = 1;
        input.rows = Constants.ABLUM_NUMS;
        input.convertJosn();

        merchantPicListRequest = new MerchantPicListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
                view.setPicList(null);
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    view.setPicList(((APIM_PicList) response).picList );
                } else {
                    view.showError(((APIM_PicList) response).info);
                    view.setPicList(null);
                }
                view.hideProgressDialog();
            }
        });
        sendJsonRequest(merchantPicListRequest);
    }

    @Override
    public void user_picList(int userId) {
        view.showProgressDialog();
        if (userPicListRequest != null) {
            userPicListRequest.cancel();
        }
        UserPicListRequest.Input input = new UserPicListRequest.Input();
        input.userId = userId;
        input.page = 1;
        input.rows = Constants.ABLUM_NUMS;
        input.convertJosn();

        userPicListRequest = new UserPicListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
                view.setPicList(null);
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_PicList) response).status == 1) {
                    view.setPicList(((APIM_PicList) response).picList );
                } else {
                    view.showError(((APIM_PicList) response).info);
                    view.setPicList(null);
                }
                view.hideProgressDialog();
            }
        });
        sendJsonRequest(userPicListRequest);
    }

    @Override
    public void merchant_pic_add(int merchantId, String pics) {
        view.showProgressDialog();
        if (merchangtPicAddRequest != null) {
            merchangtPicAddRequest.cancel();
        }
        MerchantPicAddRequest.Input input = new MerchantPicAddRequest.Input();
        input.merchantId = merchantId;
        input.pics = pics;

        input.convertJosn();
        merchangtPicAddRequest = new MerchantPicAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addPicSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchangtPicAddRequest);
    }

    @Override
    public void user_pic_add(int userId, String pics) {
        view.showProgressDialog();
        if (userPicAddRequest != null) {
            userPicAddRequest.cancel();
        }
        UserPicAddRequest.Input input = new UserPicAddRequest.Input();
        input.userId = userId;
        input.pics = pics;

        input.convertJosn();
        userPicAddRequest = new UserPicAddRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.addPicSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userPicAddRequest);
    }

    @Override
    public void merchant_pic_del(int merchantId, String picIds) {
        view.showProgressDialog();
        if (merchangtPicDelRequest != null) {
            merchangtPicDelRequest.cancel();
        }
        MerchantPicDelRequest.Input input = new MerchantPicDelRequest.Input();
        input.merchantId = merchantId;
        input.picIds = picIds;

        input.convertJosn();
        merchangtPicDelRequest = new MerchantPicDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.delPicSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(merchangtPicDelRequest);
    }

    @Override
    public void user_pic_del(int userId, String picIds) {
        view.showProgressDialog();
        if (userPicDelRequest != null) {
            userPicDelRequest.cancel();
        }
        UserPicDelRequest.Input input = new UserPicDelRequest.Input();
        input.userId = userId;
        input.picIds = picIds;

        input.convertJosn();
        userPicDelRequest = new UserPicDelRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                view.hideProgressDialog();
            }

            @Override
            public void onResponse(Object response) {
                view.hideProgressDialog();
                if (((CommonResult) response).status == 1) {
                    view.delPicSuccess();
                } else {
                    view.showError(((CommonResult) response).info);
                }
            }
        });
        sendJsonRequest(userPicDelRequest);
    }
}
