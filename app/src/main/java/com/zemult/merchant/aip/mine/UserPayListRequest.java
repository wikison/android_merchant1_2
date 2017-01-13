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
 * 我的订单
 * Created by wikison on 2016/11/14.
 */
public class UserPayListRequest extends PostStringRequest<Type> {
    public UserPayListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PAYLIST, input.ejson, new TypeToken<APIM_UserBillList>() {
        }.getType(), listener);
    }

    public static class Input {
        public int userId;    //	用户id
        public int state;    //	状态(-1:全部,0:未付款,1:已付款,2:已失效(超时未支付))
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""), new Pair<String, String>("state", state + ""), new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + "")));
        }

    }

}
