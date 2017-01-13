package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//取消收藏
public class UserFavoriteDeleteRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	newsId				;	//	信息id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("newsId", newsId+"")));
        }

    }

    public UserFavoriteDeleteRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FAVORITE_DELETE_NEWS,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
