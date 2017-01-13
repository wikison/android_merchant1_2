package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//找回密码(登录页)


public class UserFindpwdRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String phone;       //手机号
        public String password;//密码(经过MD5加密过后的)
        public String  code;//验证码
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("code", code),
                    new Pair<String, String>("password", password),
                    new Pair<String, String>("phone", phone)));
        }

    }

    public UserFindpwdRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FINDPWD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
