package com.zemult.merchant.aip.discover;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetadvertList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取  广告列表
public class CommonGetadvertListRequest extends PostStringRequest<Type>  {

    public CommonGetadvertListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_GETADVERLIST, input.ejson, new TypeToken<APIM_CommonGetadvertList>() {
        }.getType(), listener);

    }

    public static class Input {
        public int page;    //	页面编号(0:app开启页1:首页广告位2:我的斜杠3:我是商家 4:发现)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("page", page+"")));
        }

    }
}
