package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户确认预约单
public class User2ReservationSureRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String reservationId;       //预约单id
        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("reservationId", reservationId)));
        }

    }

    public User2ReservationSureRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RESERVATION_SURE,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
