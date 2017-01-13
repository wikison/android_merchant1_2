package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成支付单(找TA买单)
public class UserMerchantPayAddRequest extends PostStringRequest<Type>  {

    public static class Input {

        public int userId;	//	用户id
        public int merchantId;	//	商户id
        public int saleUserId;	//	营销经理用户id
        public double consumeMoney;	//	消费总金额
        public double money;	//	买单金额, 实际支付金额

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("saleUserId", saleUserId+""),
                    new Pair<String, String>("consumeMoney", consumeMoney+""),
                    new Pair<String, String>("money", money+"")));

        }

    }

    public UserMerchantPayAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MERCHANT_PAY_ADD ,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
