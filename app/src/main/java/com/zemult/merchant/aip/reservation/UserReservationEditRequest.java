package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//约客修改预约单(答复)
public class UserReservationEditRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int reservationId;       //预约单id
        public int userId;       //用户id
        public String replayNote;       //答复
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("reservationId", reservationId+""),
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("replayNote", replayNote)));
        }

    }

    public UserReservationEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_RESERVATION_EDIT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
