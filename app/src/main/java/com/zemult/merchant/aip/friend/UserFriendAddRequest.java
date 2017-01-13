package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//发送好友请求
public class UserFriendAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	friendId			;	//	好友的用户id
        public String note;//请求内容  可以为空

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("friendId", friendId+""), new Pair<String, String>("note", note)));
        }

    }

    public UserFriendAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FRIEND_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
