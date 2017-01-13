package com.android.volley.base;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import zema.volley.network.VolleyUtil;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public  class   NetRequest extends  BaseNetRequest {
    private Object tag;
    Request request ;
    Object requestTag;
    public NetRequest(Object arg0) {
        requestTag=  arg0;
        this.tag = arg0;
    }

    @Override
    public void doGetAction(String url, OnSuccessListener successlis, Response.ErrorListener errolis) {
        request = new StringRequest(Request.Method.POST,url,successlis,errolis) ;
        request.setTag(tag);
        request.setShouldCache(true);
        request.setCacheTime(2 * 24 * 60 * 6000);
        request.setTag(requestTag);
        VolleyUtil.getRequestQueue().add(request) ;
    }

    @Override
    public void cancelRequest() {
        if(request!=null){
            request.cancel();
        }
    }


    public static String uploadFile(File arg0, Map<String, String> arg1, String arg2){
        return  "";
    }
    public String postMultiParams(String arg0, Map<String, String> arg1, Map<String, File> arg2) throws IOException {
        return  "";
    }

}
