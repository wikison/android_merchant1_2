package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//更新用户位置
public class CommonChangeUserCityRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;       //用户id
        public String city;       //城市编号
        public String area;       //地区编号
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("city", city + ""),
                    new Pair<String, String>("area", area)));
        }

    }

    public CommonChangeUserCityRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.COMMON_CHANGEUSERCITY, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
