package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 删除服务方案
public class User2PlanDelRequest extends PostStringRequest<Type> {
    public static class Input {
        public int planId;       //  方案id
        public String pics;       // 图片，多张","分隔
        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("planId", planId + "")
        ));
    }
    }

    public User2PlanDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_PLAN_DEL, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}