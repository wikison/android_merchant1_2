package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//商家输码验单 (商家id+验证码)
public class MerchantVoucherCheckRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int  merchantId;//					是	商家(场景)id
        public String code	;//					是	验证码


        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("code", code)
                    ));
        }

    }

    public MerchantVoucherCheckRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_VOUCHER_CHECK,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
