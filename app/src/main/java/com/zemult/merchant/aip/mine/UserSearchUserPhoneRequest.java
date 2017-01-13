package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//搜索用户(手机号或者昵称)--精确搜索
public class UserSearchUserPhoneRequest extends PostStringRequest<Type> {

    public UserSearchUserPhoneRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_SEARCHUSER_PHONE, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public String name;    //	用户手机号码
        public int operateUserId;    //	操作的用户id(预留)
        public String ejson;

        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("name", name + ""),
                    new Pair<String, String>("operateUserId", operateUserId + "")
                    ));
        }

    }
}
