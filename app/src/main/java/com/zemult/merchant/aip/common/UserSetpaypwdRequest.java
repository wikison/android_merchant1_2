package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//设置支付密码
public class UserSetpaypwdRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;       //用户id
        public String password;       //支付密码(经过MD5加密过后的)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("password", password)));
        }

    }

    public UserSetpaypwdRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_SETPAYPWD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
