package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//商家入驻
public class MerchantAddentity1_1Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public  String	name			;	//	场景名称
        public  String	address			;	//	地址
        public  String	userName			;	//	联系人
        public  String	tel			;	//	联系电话


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("address", address),
                    new Pair<String, String>("userName", userName),
                    new Pair<String, String>("tel", tel)));
        }

    }

    public MerchantAddentity1_1Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_ADDENTITY_1_1,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
