package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取手机号是否绑定微信账号

public class UserBandWxInfoPhoneRequest extends PostStringRequest<Type> {

    public UserBandWxInfoPhoneRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_BAND_WX_INFO_PHONE, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String phone;       //手机号
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("phone", phone)));
        }

    }
}
