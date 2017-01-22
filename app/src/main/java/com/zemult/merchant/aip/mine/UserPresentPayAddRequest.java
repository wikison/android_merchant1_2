package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户赠送礼物-生成支付单
public class UserPresentPayAddRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public int toUserId;
        public int presentId;
        public double money;

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("toUserId", toUserId + ""),
                    new Pair<String, String>("presentId", presentId + ""),
                    new Pair<String, String>("money", money + "")
            ));
        }

    }

    public UserPresentPayAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PRESENT_PAY_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
