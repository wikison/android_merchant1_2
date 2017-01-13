package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//订单详情
public class UserPayInfoRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userPayId;    //订单id

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userPayId", userPayId + "")));
        }

    }

    public UserPayInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PAY_INFO, input.ejson, new TypeToken<APIM_UserBillInfo>() {
        }.getType(), listener);

    }
}
