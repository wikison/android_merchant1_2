package com.zemult.merchant.aip.common;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetAllTitleList;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取所有行业角色


public class CommonGetAllTitleRequest extends PostStringRequest<Type> {

    public CommonGetAllTitleRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETALLTITLE, "" ,new TypeToken<APIM_CommonGetAllTitleList>() {
        }.getType(), listener);
    }
}
