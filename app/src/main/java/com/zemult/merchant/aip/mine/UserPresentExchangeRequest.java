package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户兑换礼物
public class UserPresentExchangeRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + "")));
        }

    }

    public UserPresentExchangeRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PRESENT_EXCHANGE, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
