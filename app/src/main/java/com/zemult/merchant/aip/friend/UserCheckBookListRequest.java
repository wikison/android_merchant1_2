package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取通讯录中不是平台账户的手机号
public class UserCheckBookListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public String	phones			;	//	通讯录手机号(多个用","分隔)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("phones", phones)));
        }

    }

    public UserCheckBookListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_CHECK_BOOKLIST,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
