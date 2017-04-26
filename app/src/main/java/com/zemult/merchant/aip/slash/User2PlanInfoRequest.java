package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_PlanInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 服务方案详情
public class User2PlanInfoRequest extends PostStringRequest<Type> {
    public static class Input {
        public int planId;       //  方案id
        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("planId", planId + "")
        ));
    }
    }

    public User2PlanInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_PLAN_INFO, input.ejson ,new TypeToken<APIM_PlanInfo>() {
        }.getType(), listener);
    }
}