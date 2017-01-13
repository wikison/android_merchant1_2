package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对单笔买单评价          1:买单已支付,金额大于100元
public class UserMerchantPayCommont_1_1Request extends PostStringRequest<Type>  {

    public static class Input {
        public int userPayId;       //订单id
        public int comment;       //评分(1-5)
        public int userId;       //用户id(申请的)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),new Pair<String, String>("comment", comment+""),
                    new Pair<String, String>("userPayId", userPayId+"")));
        }

    }

    public UserMerchantPayCommont_1_1Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MERCHANT_PAY_COMMONT_1_1,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
