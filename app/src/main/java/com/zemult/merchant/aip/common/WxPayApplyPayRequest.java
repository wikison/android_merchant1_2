package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 获取微信支付参数
public class WxPayApplyPayRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userPayId;       // 订单id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userPayId", userPayId + "")));
        }

    }

    public WxPayApplyPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.WXPAY_APPLYPAY, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
