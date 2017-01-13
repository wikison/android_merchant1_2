package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//查看用户(其它人)详情
public class UserInfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id(要查看的用户)
        public int	operateUserId			;	//	操作用户id
        public String ejson;


        public void convertJosn(){
            if(operateUserId == 0)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+"")));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("operateUserId", operateUserId+"")));
        }
    }

    public UserInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_INFO,input.ejson , new TypeToken<APIM_UserLogin>() {
        }.getType() , listener);

    }
}
