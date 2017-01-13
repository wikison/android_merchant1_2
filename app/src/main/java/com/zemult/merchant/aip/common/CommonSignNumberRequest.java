package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成支付宝签名字符串
public class CommonSignNumberRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String number;       //手机号
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("number", number)));
        }

    }

    public CommonSignNumberRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_SIGN_NUMBER,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
