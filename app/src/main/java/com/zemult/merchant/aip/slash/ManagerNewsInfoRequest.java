package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//查看方案详情
public class ManagerNewsInfoRequest extends PostStringRequest<Type> {

    public static class Input {
        public int	operateUserId			;	//	操作用户id
        public int	newsId			;	//	信息id


        public String ejson;

        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("newsId", newsId + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("newsId", newsId + "")));
        }
    }

    public ManagerNewsInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_INFO, input.ejson, new TypeToken<APIM_ManagerNewsInfo>() {
        }.getType(), listener);

    }
}
