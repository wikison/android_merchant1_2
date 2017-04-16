package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;


//修改服务管家信息(标签,状态,职位)
public class User2SaleMerchantEditRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;       //用户id
        public int merchantId;   //服务管家id
        public String tags;   //服务标签
        public int state;   //状态
        public String position;//职位
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("tags", tags),
                    new Pair<String, String>("state", state + ""),
                    new Pair<String, String>("position", position)));
        }

    }

    public User2SaleMerchantEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_SALE_MERCHANT_EDIT, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
