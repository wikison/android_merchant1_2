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
 * 查看服务管家详情
 */

public class User2SaleUserInfoRequest extends PostStringRequest<Type> {


    public static class Input {
        public int operateUserId;  //操作的用户id(预留)
        public int saleUserId;  //服务管家id(预留)
        public int merchantId;  //商家(场景)的id
        public String center;  //坐标

        public String ejson;

        public void convertJosn() {

            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("operateUserId", operateUserId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("center", center)
            ));
        }
    }

    public User2SaleUserInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_SALEUSER_INFO, input.ejson, new TypeToken<APIM_MerchantGetinfo>() {
        }.getType(), listener);

    }

}
