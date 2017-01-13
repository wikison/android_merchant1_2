package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//删除商家下的某个营销经理 删除营销经理下的某个商家
public class MerchantSaleuserDelRequest extends PostStringRequest<Type> {

    public static class Input {
        public int merchantId;    //	商家id
        public int saleUserId;    //	营销经理用户id

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("merchantId", merchantId + ""), new Pair<String, String>("saleUserId", saleUserId + "")));
        }

    }

    public MerchantSaleuserDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.MERCHANT_SALEUSER_DEL, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
