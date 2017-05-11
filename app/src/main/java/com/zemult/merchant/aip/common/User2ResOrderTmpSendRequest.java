package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//发送短信(临时服务订单)

public class User2ResOrderTmpSendRequest extends PostStringRequest<Type> {

    public User2ResOrderTmpSendRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RES_ORDER_TMP_SEND, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public int resOrderId;
        public String phoneNum;
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("resOrderId", resOrderId+""),
                    new Pair<String, String>("phoneNum", phoneNum)
            ));
        }
    }
}
