package com.zemult.merchant.aip.reservation;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;


//用户发送语音预约消息
public class User2RemindIMAddRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;       //用户id
        public int saleUserId;   //服务管家id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + "")));
        }

    }

    public User2RemindIMAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_REMINDIM_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
