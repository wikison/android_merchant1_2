package com.zemult.merchant.aip.reservation;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;


//用户发送语音预约消息
public class User2RemindIMAddRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;       //用户id
        public int merchantId;   //服务管家id
        public String reservationTime;   //预约时间
        public String num;   //人数
        public String perMoney;   //人均预算
        public String tags;   //服务管家标签
        public int saleUserId;   //服务管家id
        public String replayNote;//否	语音地址
        public String timeNum;//否	语音时长

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("reservationTime", reservationTime),
                    new Pair<String, String>("num", num),
                    new Pair<String, String>("perMoney", perMoney),
                    new Pair<String, String>("tags", tags ),
                    new Pair<String, String>("timeNum", timeNum ),
                    new Pair<String, String>("replayNote", replayNote)));
        }

    }

    public User2RemindIMAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_REMINDIM_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
