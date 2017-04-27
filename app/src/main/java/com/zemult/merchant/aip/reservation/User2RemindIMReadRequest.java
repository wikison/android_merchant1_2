package com.zemult.merchant.aip.reservation;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;


//修改 语音预约消息为已读
public class User2RemindIMReadRequest extends PostStringRequest<Type> {

    public static class Input {
        public int remindIMId;       //语音预约消息id
        public String ejson;
        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("remindIMId", remindIMId + "")
                   ));
        }
    }

    public User2RemindIMReadRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_REMINDIM_READ, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
