package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserBillInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的账单详情-接收打/赞赏类型的(type=10)
public class UserBillInfoRewardGetRequest extends PostStringRequest<Type> {

    public static class Input {
        public int billId;    //	账单id
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("billId", billId + "")));
        }

    }

    public UserBillInfoRewardGetRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_BILL_INFO_REWARD_GET, input.ejson, new TypeToken<APIM_UserBillInfo>() {
        }.getType(), listener);

    }
}
