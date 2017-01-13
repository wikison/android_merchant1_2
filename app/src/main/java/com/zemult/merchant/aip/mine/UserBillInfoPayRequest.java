package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的账户明细详情-支付 type=2
public class UserBillInfoPayRequest extends PostStringRequest<Type> {

    public static class Input {
        public int billId;    //	账单id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("billId", billId + "")));
        }

    }

    public UserBillInfoPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_BILL_INFO_PAY, input.ejson, new TypeToken<APIM_UserBillInfo>() {
        }.getType(), listener);

    }
}
