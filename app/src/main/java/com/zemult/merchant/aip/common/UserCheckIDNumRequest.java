package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//修改支付密码-忘记密码(验证实名认证)
public class UserCheckIDNumRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;       //用户id
        public String name;       //支付密码(经过MD5加密过后的)
        public String idNum;//身份证号
        public String code;//验证码
        public String ejson;



        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("code", code),
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("idNum", idNum)));
        }

    }

    public UserCheckIDNumRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_CHECKIDNUM,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
