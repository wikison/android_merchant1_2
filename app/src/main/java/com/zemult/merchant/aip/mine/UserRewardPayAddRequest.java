package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户打/赞赏-生成支付单（支付宝）
public class UserRewardPayAddRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public int toUserId;
        public String note;
        public double money;

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("toUserId", toUserId + ""),
                    new Pair<String, String>("note", note),
                    new Pair<String, String>("money", money + "")
            ));
        }

    }

    public UserRewardPayAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_REWARD_PAY_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
