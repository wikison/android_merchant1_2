package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户查看和他人的共同好友列表
public class UserFriendListOtherRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	 operateUserId;	//操作的用户id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("operateUserId", operateUserId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserFriendListOtherRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FRIENDLIST_OTHER,input.ejson , new TypeToken<APIM_UserFriendList>() {
        }.getType() , listener);

    }
}
