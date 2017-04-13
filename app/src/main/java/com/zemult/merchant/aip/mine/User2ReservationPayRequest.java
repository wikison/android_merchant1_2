package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成定金支付单(用户确认预约单)
public class User2ReservationPayRequest extends PostStringRequest<Type> {

    public static class Input {

        public int userId;    //	用户id
        public int merchantId;    //		商户id
        public int saleUserId;    //	服务管家的用户id
        public double money;    //	定金金额
        public int reservationId;    //	预约单id
        public String consumeMoney;    //	是	当次支付总金额(money+rewardMoney)
        public double rewardMoney;    //	否	打赏金额(没有为0)

        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("money", money + ""),
                    new Pair<String, String>("consumeMoney", consumeMoney ),
                    new Pair<String, String>("rewardMoney", rewardMoney + ""),
                    new Pair<String, String>("reservationId", reservationId + "")
                    ));

        }

    }

    public User2ReservationPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_RESERVATION_PAY, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
