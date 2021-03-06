package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//删除约客下的某个商家
public class UserSaleMerchantDelRequest extends PostStringRequest<Type> {

    public static class Input {
        public int merchantId;    //	商家id
        public int userId;    //	用户id

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("merchantId", merchantId + ""), new Pair<String, String>("userId", userId + "")));
        }

    }

    public UserSaleMerchantDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_SALE_MERCHANT_DEL, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
