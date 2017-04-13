package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成支付单(找TA买单)--(关联预约单)
public class User2PayAddRequest extends PostStringRequest<Type> {

    public static class Input {

        public int userId;    //	用户id
        public int merchantId;    //		商户id
        public int saleUserId;    //	服务管家的用户id
        public int type;    //	支付类型支付单类型(0:买单--(直接买单/关联预约单无定金买单-包含合并赞赏金额),6有定金的预约单买单(包含合并赞赏金额))
        public double consumeMoney;    //	是	总金额(总消费金额-包含定金)
        public double money;    //	当次支付的消费金额(consumeMoney-FRewardMoney)
        public int reservationId;    //	预约单id
        public double reservationMoney;    //定金金额(没有为0)
        public double rewardMoney;    //	预约单id
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("type", type + ""),
                    new Pair<String, String>("consumeMoney", consumeMoney + ""),
                    new Pair<String, String>("money", money + ""),
                    new Pair<String, String>("reservationId", reservationId + ""),
                    new Pair<String, String>("reservationMoney", reservationMoney + ""),
                    new Pair<String, String>("rewardMoney", rewardMoney + "")
            ));

        }

    }

    public User2PayAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_PAY_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
