package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetareasbycity;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取单个行业角色详情

public class CommonGetareasbycityRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String city;       //地区编号(市)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("city", city)));
        }

    }

    public CommonGetareasbycityRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETAREASBYCITY,input.ejson , new TypeToken<APIM_CommonGetareasbycity>() {
        }.getType() , listener);

    }
}
