package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户登录

public class UserLogin2_3Request extends PostStringRequest<Type> {


    public static class Input {
        public String account;       //  账号(手机号或者账号)
        public String code;          //
        public String device_token;       //IM推送的设备唯一标识
        public String ejson;

    public void convertJosn(){
        ejson=Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("account", account),
                new Pair<String, String>("code", code),
                new Pair<String, String>("device_token", device_token)));
    }
    }

    public UserLogin2_3Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_LOGIN_2_3, input.ejson, new TypeToken<APIM_UserLogin>() {
        }.getType(), listener);
    }
}
