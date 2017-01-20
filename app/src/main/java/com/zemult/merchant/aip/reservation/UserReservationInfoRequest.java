package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户预约单详情
public class UserReservationInfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String phone;       //手机号
        public String code;       //验证码
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("code", code),
                    new Pair<String, String>("phone", phone)));
        }

    }

    public UserReservationInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_RESERVATION_INFO,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
