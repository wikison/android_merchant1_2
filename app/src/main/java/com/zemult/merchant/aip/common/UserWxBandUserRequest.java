package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//根据微信号获取绑定的用户信息

public class UserWxBandUserRequest extends PostStringRequest<Type> {

    public UserWxBandUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_WX_BAND_USER, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String openid;       //手微信账号的用户的标识，对当前公众号唯一
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("openid", openid)));
        }

    }
}
