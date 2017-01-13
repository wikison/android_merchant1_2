package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取 我的消息中未读的所有数量
public class UserMessageAllNumUnread_1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+"")));
        }

    }

    public UserMessageAllNumUnread_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MESSAGE_ALLNUM_UNREAD_1_2,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
