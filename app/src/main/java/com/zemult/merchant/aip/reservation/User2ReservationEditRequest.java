package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//服务管家修改预约单(未确认的)
public class User2ReservationEditRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String reservationId;       //预约单id
        public int num;       //是	人数
        public String note;       //是	包厢/房间号(备注)
        public String reservationTime;       //是	预约时间(格式为"yyyy-MM-dd HH:mm:ss")
        public String reservationMoney;       //否	定金
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("reservationId", reservationId),
                    new Pair<String, String>("userId", num+""),
                    new Pair<String, String>("replayNote", note),
                    new Pair<String, String>("reservationMoney", reservationMoney),
                    new Pair<String, String>("replayNote", note)));
        }

    }

    public User2ReservationEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RESERVATION_EDIT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
