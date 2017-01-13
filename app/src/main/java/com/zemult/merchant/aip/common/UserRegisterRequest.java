package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户注册

public class UserRegisterRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String phone;       //手机号
        public String password;//密码(经过MD5加密过后的)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("password", password),
                    new Pair<String, String>("phone", phone)));
        }

    }

    public UserRegisterRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_REGISTER,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
