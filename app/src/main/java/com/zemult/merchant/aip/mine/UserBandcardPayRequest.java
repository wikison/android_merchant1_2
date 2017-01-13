package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成支付宝支付单
public class UserBandcardPayRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public double	money				;	//	买单金额

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("money", money+""),
                    new Pair<String, String>("userId", userId+"")));
        }

    }

    public UserBandcardPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_BANDCARD_PAY,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
