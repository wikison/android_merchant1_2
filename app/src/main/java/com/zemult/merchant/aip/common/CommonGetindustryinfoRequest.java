package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustryinfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取单个行业角色详情

public class CommonGetindustryinfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int industryId;       //行业id
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("industryId", industryId+"")));
        }

    }

    public CommonGetindustryinfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETROLEINFO,input.ejson , new TypeToken<APIM_CommonGetindustryinfo>() {
        }.getType() , listener);

    }
}
