package com.android.volley.base;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import java.io.File;
import java.util.Map;

import zema.volley.network.ResponseListener;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public abstract class BaseNetRequest {

    public abstract void doGetAction(String url, OnSuccessListener arg1, Response.ErrorListener arg2);

    public abstract void cancelRequest();
}
