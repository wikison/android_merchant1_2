package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对买单评价         1:买单已支付
public class User2PayCommontAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String ejson;
        public int  userId;//				是	用户id
        public int userPayId;//				订单id
        public int comment;//				评分(1-5)
        public String note;		//		否	备注

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("userPayId", userPayId+""),
                    new Pair<String, String>("comment", comment+""),
                    new Pair<String, String>("note", note)));
        }

    }

    public User2PayCommontAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_PAY_COMMONT_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
