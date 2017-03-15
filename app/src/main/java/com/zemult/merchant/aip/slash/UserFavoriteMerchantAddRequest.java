package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户添加收藏商家
public class UserFavoriteMerchantAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	merchantId			;	//	商户 id
        public String ejson;


        public void convertJosn(){
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("merchantId", merchantId+"")));
        }
    }

    public UserFavoriteMerchantAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FAVORITE_MERCHANT_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
