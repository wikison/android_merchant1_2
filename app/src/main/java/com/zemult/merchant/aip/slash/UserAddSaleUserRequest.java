package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

public class UserAddSaleUserRequest extends PostStringRequest<Type> {
    public static class Input {
        public int userId;       //  用户id
        public int merchantId;       //  商家(场景)的id
        public String tags;       //服务标签(多个用“,”分隔)
        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("userId", userId + ""),
                new Pair<String, String>("merchantId", merchantId + ""),
                new Pair<String, String>("tags", tags)

        ));

    }
    }

    public UserAddSaleUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_ADD_SALEUSER_1_1, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}