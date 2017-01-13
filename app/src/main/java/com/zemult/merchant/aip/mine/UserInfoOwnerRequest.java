package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取用户自身的资料（包含关注数/粉丝数）

public class UserInfoOwnerRequest extends PostStringRequest<Type> {
    public static class Input {
        public String userId;       //  用户id
        public String ejson;

    public void convertJosn(){
        ejson=Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("userId", userId+"")));
    }
    }

    public UserInfoOwnerRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_INFO_OWNER, input.ejson ,new TypeToken<APIM_UserLogin>() {}.getType(), listener);
    }
}
