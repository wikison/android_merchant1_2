package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对已经确认的预约单(服务单)评价
public class User2ReservationCommontAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String ejson;
        public int  userId;//				是	用户id
        public int reservationId;//				预约单id
        public int comment;//				评分(1-5)
        public String note;		//		评价内容

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("reservationId", reservationId+""),
                    new Pair<String, String>("comment", comment+""),
                    new Pair<String, String>("note", note)));
        }

    }

    public User2ReservationCommontAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RESERVATION_COMMONT_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
