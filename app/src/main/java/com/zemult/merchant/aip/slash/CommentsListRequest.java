package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsCommentList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/4/26.
 */

public class CommentsListRequest extends PostStringRequest<Type> {

    public static class Input {
        public int	saleUserId			;	//	方案id
        public int merchantId;
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("saleUserId", saleUserId+""),
                    new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("page", page+""),
                    new Pair<String, String>("rows", rows+"")));
        }

    }

    public CommentsListRequest(CommentsListRequest.Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_SALEUSER_COMMENTLIST,input.ejson , new TypeToken<APIM_ManagerNewsCommentList>() {
        }.getType() , listener);

    }
}

