package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//判断用户是否实名认证过

public class UserRealnameShowRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String userId;       //用户id
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId)));
        }

    }

    public UserRealnameShowRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_ISREALNAME,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
