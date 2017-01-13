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

public class MerchantInfoRequest extends PostStringRequest<Type> {


    public static class Input {
        public int   operateUserId;  //操作的用户id(预留)
        public int   merchantId;  //商家(场景)的id

        public String ejson;


        public void convertJosn(){
            if(operateUserId == 0)
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("merchantId", merchantId+"")
                ));
            else
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("merchantId", merchantId+"")
                ));
        }
    }

    public MerchantInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_INFO,input.ejson , new TypeToken<APIM_MerchantGetinfo>() {
        }.getType() , listener);

    }

}
