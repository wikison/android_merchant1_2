package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetindustrychilds;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取单个行业下的角色列表


public class CommonGetindustrychildsRequest extends PostStringRequest<Type> {


    public CommonGetindustrychildsRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETROLESBYINDUSTRY, input.ejson, new TypeToken<APIM_CommonGetindustrychilds>() {
        }.getType(), listener);
    }

    public static class Input {
        public int industryId;    //	行业id
        public int userId;    //用户id
        public String name;   //名称(模糊)
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("industryId", industryId + ""),
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("name", name + ""),
                    new Pair<String, String>("page", page+""),
                    new Pair<String, String>("rows", rows + "")
                    ));
        }

    }
}
