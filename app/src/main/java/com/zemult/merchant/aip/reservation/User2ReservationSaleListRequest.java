package com.zemult.merchant.aip.reservation;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取用户在某个约客下的某家商户下的预约单列表(确认未支付)
public class User2ReservationSaleListRequest extends PostStringRequest<Type> {

    public static class Input {
        public String ejson;
        public int userId;       //				是	用户id
        public int merchantId;   //				是	商户id
        public int saleUserId;   //				是	约客的用户id
        public int page;
        public int rows;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + "")));
        }

    }

    public User2ReservationSaleListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_RESERVATION_SALE_LIST, input.ejson, new TypeToken<APIM_UserReservationList>() {
        }.getType(), listener);

    }
}
