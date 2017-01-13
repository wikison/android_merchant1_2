package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserLevelList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的等级排行榜
public class UserLevelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;    //	用户id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+"")));
        }

    }

    public UserLevelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_LEVEL,input.ejson , new TypeToken<APIM_UserLevelList>() {
        }.getType() , listener);

    }
}
