package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取最新版本的app软件
public class CommonAppVersionRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String type;       //类型(0:安卓,1:ios)
        public String version;       //版本号
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("type", type),
                    new Pair<String, String>("version", version)));
        }

    }

    public CommonAppVersionRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_APP_VERSION,input.ejson , new TypeToken<APIM_CommonAppVersion>() {
        }.getType() , listener);

    }
}
