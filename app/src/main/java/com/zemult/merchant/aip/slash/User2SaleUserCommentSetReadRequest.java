package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/4/26.
 */

public class User2SaleUserCommentSetReadRequest extends PostStringRequest<Type> {

    public static class Input {
        public int saleUserId;    //	方案id
        public int merchantId;

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + "")));
        }

    }

    public User2SaleUserCommentSetReadRequest(User2SaleUserCommentSetReadRequest.Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER2_SALEUSER_COMMENT_SETREAD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}

