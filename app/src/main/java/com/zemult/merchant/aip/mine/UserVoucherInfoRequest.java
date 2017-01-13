package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的代金券详情信息
public class UserVoucherInfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userVoucherId;    //	用户id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userVoucherId", userVoucherId+"")));
        }

    }

    public UserVoucherInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_VOUCHER_INFO,input.ejson , new TypeToken<APIM_UserVoucherList>() {
        }.getType() , listener);

    }
}
