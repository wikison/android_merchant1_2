package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/20.
 */
//查看语音预约消息信息
public class User2RemindIMInfoRequest extends PostStringRequest<Type> {

    public static class Input {

        public int remindIMId;    //	语音预约消息id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("remindIMId", remindIMId+"" ))
            );
        }
    }

    public User2RemindIMInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_REMINDIM_INFO, input.ejson, new TypeToken<M_Reservation>() {
        }.getType(), listener);

    }
}