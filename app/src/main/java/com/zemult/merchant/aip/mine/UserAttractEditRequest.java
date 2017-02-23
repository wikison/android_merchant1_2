package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//添加关注
public class UserAttractEditRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	attractId				;	//	被关注的用户id
        public String	note				;	//	备注名


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("attractId", attractId+""),
                    new Pair<String, String>("note", note)
                    ));
        }

    }

    public UserAttractEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_ATTRACT_EDIT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
