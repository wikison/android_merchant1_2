package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/12/28.
 */

public class UserSaleMerchantEditRequest  extends PostStringRequest<Type> {

    public static class Input {
        public int userId;       //用户id
        public int merchantId ; //商户id
        public String tags;       //服务标签(多个用“,”分隔)
        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("tags", tags)));
        }

    }

    public UserSaleMerchantEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_SALE_MERCHANT_EDIT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}

