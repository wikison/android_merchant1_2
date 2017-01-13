/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import android.support.v4.BuildConfig;
import android.util.Log;

import com.android.volley.GsonCallBack.Listener;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;

import zema.volley.network.VolleyUtil;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class GsonRequest extends Request<String> {
    private static final String TAG = GsonRequest.class.getSimpleName();
    private ErrorListener mErrorListener;
    private Listener mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      GsonCallBack to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public GsonRequest(int method, String url, Listener listener,
                       ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mErrorListener = errorListener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      GsonCallBack to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public GsonRequest(String url, Listener listener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    protected void deliverResponse(String response) {
        try {
            Gson gson = new Gson();
            mListener.onResponse(gson.fromJson(response, mListener.mType.getClass()));
        } catch (JsonSyntaxException e) {
            mErrorListener.onErrorResponse(new VolleyError("Json返回值解析异常!"));
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Json返回值解析异常!");
                e.printStackTrace();
            }

        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }


    public GsonRequest start(Object tag) {
        setTag(tag);
        setShouldCache(true);
        setCacheTime(2 * 24 * 60 * 6000);
        VolleyUtil.getRequestQueue().add(this);
        return this;
    }

    public GsonRequest start() {
        setShouldCache(true);
        setCacheTime(2 * 24 * 60 * 6000);
        VolleyUtil.getRequestQueue().add(this);
        return this;
    }

}
