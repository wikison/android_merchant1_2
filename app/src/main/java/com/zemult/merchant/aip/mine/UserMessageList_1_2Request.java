package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取 我的消息列表
public class UserMessageList_1_2Request extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id


        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + "")));
        }

    }

    public UserMessageList_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MESSAGELIST_1_2, input.ejson, new TypeToken<APIM_CommonSysMessageList>() {
        }.getType(), listener);

    }
}
