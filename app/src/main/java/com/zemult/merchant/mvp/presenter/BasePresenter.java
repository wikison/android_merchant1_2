package com.zemult.merchant.mvp.presenter;

import com.android.volley.Request;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.VolleyUtil;

/**
 * BasePresenter
 * @author djy
 * @time 2016/7/20 9:37
 */
public class BasePresenter {

    protected ArrayList<WeakReference<Request>> listJsonRequest;


    public void setListJsonRequest(ArrayList<WeakReference<Request>> listJsonRequest){
        this.listJsonRequest = listJsonRequest;
    }

    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
    }
}
