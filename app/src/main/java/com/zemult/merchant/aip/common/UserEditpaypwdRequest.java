package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 修改登录密码
public class UserEditpaypwdRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;       // 用户id
        public String password; // 密码(经过MD5加密过后的)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("password", password)));
        }

    }

    public UserEditpaypwdRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_EDITPAYPWD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
