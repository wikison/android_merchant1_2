package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//通过预约单快捷生成支付买单(1)
public class UserReservationPayAddRequest extends PostStringRequest<Type>  {

    public static class Input {

        public int userId;	//	用户id
        public int reservationId;	//	预约单id
        public double consumeMoney;	//	消费总金额
        public double money;	//	买单金额, 实际支付金额

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("reservationId", reservationId+""),
                    new Pair<String, String>("consumeMoney", consumeMoney+""),
                    new Pair<String, String>("money", money+"")));

        }

    }

    public UserReservationPayAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_RESERVATION_PAY_ADD ,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
