package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的礼物列表
public class UserPresentRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + "")));
        }

    }

    public UserPresentRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PRESENT_LIST, input.ejson, new TypeToken<APIM_PresentList>() {
        }.getType(), listener);

    }
}
