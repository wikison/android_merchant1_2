package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//服务管家用户的商家列表(所有)
public class Merchant2SaleUserMerchantListRequest extends PostStringRequest<Type> {

    public static class Input {
        public int operateUserId;    //	操作的用户id(预留)
        public String center;    //	用户中心点坐标(type=1时 必填) 规则：经度和纬度用","分割;例 "119.971736,31.829737"
        public int saleUserId;    //	被查看用户的id

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("operateUserId", operateUserId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("center", center)));

        }

    }

    public Merchant2SaleUserMerchantListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.MERCHANT2_SALEUSER_MERCHANTLIST, input.ejson, new TypeToken<APIM_MerchantList>() {
        }.getType(), listener);

    }
}
