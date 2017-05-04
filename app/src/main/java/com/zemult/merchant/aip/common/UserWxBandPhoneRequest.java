package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//手机号绑定微信并且登录

public class UserWxBandPhoneRequest extends PostStringRequest<Type> {

    public UserWxBandPhoneRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_WX_BAND_PHONE, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String openid;
        public String phone;
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("openid", openid),
                    new Pair<String, String>("phone", phone)
            ));
        }
    }
}
