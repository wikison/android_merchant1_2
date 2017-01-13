package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetinfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取商户(场景)信息
public class MerchantGetinfoRequest extends PostStringRequest<Type>  {

    public static class Input {
       public  int	merchantId			;	//	商家(场景)的id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson( new Pair<String, String>("merchantId", merchantId+"")));
        }

    }

    public MerchantGetinfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_GETINFO,input.ejson , new TypeToken<APIM_MerchantGetinfo>() {
        }.getType() , listener);

    }
}
