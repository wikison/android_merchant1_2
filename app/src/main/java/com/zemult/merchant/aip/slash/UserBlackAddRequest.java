package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户拉黑其它人
public class UserBlackAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	blackUserId			;	//	要拉黑用户 id
        public String ejson;


        public void convertJosn(){
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("blackUserId", blackUserId+"")));
        }
    }

    public UserBlackAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_BLACK_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
