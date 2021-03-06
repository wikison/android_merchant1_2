package com.zemult.merchant.aip.slash;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//搜索场景列表 条件:场景名称,用户中心坐标,行业,是否上线
public class MerchantSearchmerchantListRequest extends PostStringRequest<Type> {

        public static class Input {
        public int operateUserId;    //	操作的用户id(预留)
        public String name;    //	场景名称(模糊)
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0){
                if(TextUtils.isEmpty(name))
                    ejson = Convert.securityJson(Convert.pairsToJson(
                            new Pair<String, String>("page", page + ""),
                            new Pair<String, String>("rows", rows + "")));
                else
                    ejson = Convert.securityJson(Convert.pairsToJson(
                            new Pair<String, String>("name", name),
                            new Pair<String, String>("page", page + ""),
                            new Pair<String, String>("rows", rows + "")));
            }

            else{
                if(TextUtils.isEmpty(name))
                    ejson = Convert.securityJson(Convert.pairsToJson(
                            new Pair<String, String>("operateUserId", operateUserId + ""),
                            new Pair<String, String>("page", page + ""),
                            new Pair<String, String>("rows", rows + "")));
                else
                    ejson = Convert.securityJson(Convert.pairsToJson(
                            new Pair<String, String>("operateUserId", operateUserId + ""),
                            new Pair<String, String>("name", name),
                            new Pair<String, String>("page", page + ""),
                            new Pair<String, String>("rows", rows + "")));
            }
        }

    }

    public MerchantSearchmerchantListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_SEARCHMERCHANTLIST, input.ejson, new TypeToken<APIM_MerchantList>() {
        }.getType(), listener);

    }
}
