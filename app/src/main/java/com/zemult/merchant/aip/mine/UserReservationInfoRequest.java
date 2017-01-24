package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.M_Reservation;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/20.
 */
//用户预约单详情
public class UserReservationInfoRequest extends PostStringRequest<Type> {

    public static class Input {

        public String reservationId;    //	预约单id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("reservationId", reservationId ))
            );
        }

    }

    public UserReservationInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_RESERVATION_INFO, input.ejson, new TypeToken<M_Reservation>() {
        }.getType(), listener);

    }
}