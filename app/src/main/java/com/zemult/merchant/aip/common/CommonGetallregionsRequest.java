package com.zemult.merchant.aip.common;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallregions;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取所有的省市区列表


public class CommonGetallregionsRequest extends PostStringRequest<Type> {

    public CommonGetallregionsRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETREGIONS, "" ,new TypeToken<APIM_CommonGetallregions>() {
        }.getType(), listener);
    }
}
