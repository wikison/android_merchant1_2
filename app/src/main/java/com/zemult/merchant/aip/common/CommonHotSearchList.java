package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_HotList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//热门搜索词

public class CommonHotSearchList extends PostStringRequest<Type> {
    public CommonHotSearchList(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_HOT_SEARCHLIST, input.ejson, new TypeToken<APIM_HotList>() {
        }.getType(), listener);
    }

    public static class Input {
        public int page;       //页编号
        public int rows;       //每页条数
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + "")
            ));
        }
    }
}
