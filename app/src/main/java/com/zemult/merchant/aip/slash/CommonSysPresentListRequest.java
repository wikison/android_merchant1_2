package com.zemult.merchant.aip.slash;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import com.zemult.merchant.model.apimodel.APIM_PresentList;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * 获取系统的礼物列表
 * @author djy
 * @time 2017/1/22 15:33
 */
public class CommonSysPresentListRequest extends PostStringRequest<Type> {

    public CommonSysPresentListRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_SYSPRESENTLIST, "" , new TypeToken<APIM_PresentList>() {
        }.getType() , listener);

    }
}
