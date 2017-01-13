package com.zemult.merchant.aip.friend;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//搜索用户(根据手机号或者斜杠号)--精确搜索
public class UserSearchUserRequest extends PostStringRequest<Type> {

    public UserSearchUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_SEARCHUSER, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String phone;    //	用户手机号码
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("phone", phone + "")));
        }

    }
}
