package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//微信授权并绑定手机号登陆(注册)

public class UserLoginWxRequest extends PostStringRequest<Type> {

    public UserLoginWxRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_LOGIN_WX, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String openid;       //微信账号的用户的标识，对当前公众号唯一
        public String name;       //微信账号的昵称
        public String pic;       //微信账号的头像
        public String phone;       //手机号
        public String password;       //登陆密码(经过MD5加密过后的)
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("openid", openid),
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("pic", pic),
                    new Pair<String, String>("phone", phone),
                    new Pair<String, String>("password", password))
            );
        }

    }
}
