package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取用户挂靠的商家列表(服务管家)
public class UserSaleMerchantList_1_2_2Request extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public String center;    //	经纬度
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("center", center)
            ));
        }
    }

    public UserSaleMerchantList_1_2_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_SALE_MERCHANTLIST_1_2_2, input.ejson, new TypeToken<APIM_MerchantList>() {
        }.getType(), listener);

    }
}
