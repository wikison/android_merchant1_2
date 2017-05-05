package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//推荐商家列表          签约商家
public class Merchant2SearchListBandRequest extends PostStringRequest<Type> {

        public static class Input {
        public int operateUserId;    //	操作的用户id(预留)
        public int industryId;    //	行业id
        public String center;    //	用户中心点坐标规则：经度和纬度用","分割;例 "119.971736,31.829737"
        public String name;    //	场景名称(模糊)
        public String city;    //	市编号
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("industryId", industryId + ""),
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("center", center),
                        new Pair<String, String>("city", city),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("industryId", industryId + ""),
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("center", center),
                        new Pair<String, String>("city", city),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));

        }

    }

    public Merchant2SearchListBandRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT2_SEARCH_LIST_BAND, input.ejson, new TypeToken<APIM_MerchantList>() {
        }.getType(), listener);

    }
}
