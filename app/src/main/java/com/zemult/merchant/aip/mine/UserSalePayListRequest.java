package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * 约客服务记录列表
 * Created by wikison on 2016/12/30.
 */
public class UserSalePayListRequest extends PostStringRequest<Type> {
    public UserSalePayListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_SALE_PAYLIST, input.ejson, new TypeToken<APIM_UserBillList>() {
        }.getType(), listener);
    }

    public static class Input {
        public int saleUserId;    //	服务管家用户id
        public int merchantId;//商户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + "")));
        }

    }

}
