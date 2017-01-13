package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * 商家详情
 */

public class MerchantInfoRequest2 extends PostStringRequest<Type> {


    public static class Input {
        public int   userId;  //请求用户id
        public int   merchantId;  //商家(场景)的id

        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("merchantId", merchantId+""))
            );

        }

    }

    public MerchantInfoRequest2(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_INFO_REQUEST,input.ejson , new TypeToken<APIM_MerchantGetinfo>() {
        }.getType() , listener);

    }

}
