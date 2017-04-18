package com.zemult.merchant.activity.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 首页 用户自己所在的服务指数最高的商家
public class User2FirstSaleUserRequest extends PostStringRequest<Type> {
    public static class Input {
        public int operateUserId;       //  用户id
        public String center;       // 用户中心点坐标规则：经度和纬度用","分割;默认"119.969499,31.817949"
        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("operateUserId", operateUserId + ""),
                new Pair<String, String>("center", center)
        ));
    }
    }

    public User2FirstSaleUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_FIRST_SALEUSER, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}