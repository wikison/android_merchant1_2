package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/16.
 */
public class UserDeleteFavoriteRequest extends PostStringRequest<Type> {

    public static class Input {
        public int	userId				;	//	用户id
        public int	merchantId				;	//	收藏商家id


        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("merchantId", merchantId+"")));
        }

    }

    public UserDeleteFavoriteRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FAVORITE_DELETE,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}

