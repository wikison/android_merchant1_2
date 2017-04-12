package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/4/11.
 */
//TA的粉丝
public class User2SaleUserFanListRequest extends PostStringRequest<Type> {

    public static class Input {
        public int saleUserId;   //服务管家用户id
        public String name;    //昵称
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


        public String ejson;


        public void convertJosn() {
            if(StringUtils.isBlank(name))
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("saleUserId", saleUserId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + ""))
                );
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("saleUserId", saleUserId + ""),
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + ""))
                );

        }

    }

    public User2SaleUserFanListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_SALEUSER_FANSLIST, input.ejson, new TypeToken<APIM_UserFansList>() {
        }.getType(), listener);

    }
}
