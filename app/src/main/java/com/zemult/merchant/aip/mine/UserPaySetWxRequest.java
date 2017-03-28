package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//设置支付单为微信支付
public class UserPaySetWxRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userPayId;    //	订单id
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userPayId", userPayId + "")
            ));
        }
    }

    public UserPaySetWxRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PAY_SETWX, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
