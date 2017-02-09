package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//验证用户原安全密码
public class UserCheckpaypwdRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;       //用户id
        public String password;       //安全密码(经过MD5加密过后的)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("password", password)));
        }

    }

    public UserCheckpaypwdRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_CHECKPAYPWD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
