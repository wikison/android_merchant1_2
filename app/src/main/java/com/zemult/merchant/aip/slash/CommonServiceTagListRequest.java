package com.zemult.merchant.aip.slash;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/12/28.
 */
//获取服务人员标签
public class CommonServiceTagListRequest extends PostStringRequest<Type> {

    public CommonServiceTagListRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_SERVICETAGLIST_1_1, "" , new TypeToken<APIM_CommonGetallindustry>() {
        }.getType() , listener);

    }
}
