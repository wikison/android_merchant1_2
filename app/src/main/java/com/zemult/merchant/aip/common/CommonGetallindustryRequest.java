package com.zemult.merchant.aip.common;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取所有行业角色


public class CommonGetallindustryRequest extends PostStringRequest<Type> {

    public CommonGetallindustryRequest( ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETINDUSTRYROLES, "" ,new TypeToken<APIM_CommonGetallindustry>() {
        }.getType(), listener);
    }
}
