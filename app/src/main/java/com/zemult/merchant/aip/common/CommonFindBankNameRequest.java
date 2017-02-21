package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonAppVersion;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//根据银行卡号获取银行卡名称
public class CommonFindBankNameRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String number;       //卡号
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("number", number)));
        }

    }

    public CommonFindBankNameRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_FIND_BANKNAME,input.ejson , new TypeToken<APIM_CommonAppVersion>() {
        }.getType() , listener);

    }
}
