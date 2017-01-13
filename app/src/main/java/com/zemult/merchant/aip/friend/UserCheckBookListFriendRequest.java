package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取通讯录中不是用户好友的手机号(是平台账号,发过好友申请的也不显示)
public class UserCheckBookListFriendRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	用户id
        public String	phones			;	//	通讯录手机号(多个用","分隔)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("operateUserId", operateUserId+""), new Pair<String, String>("phones", phones)));
        }

    }

    public UserCheckBookListFriendRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_CHECK_BOOKLIST_FRIEND,input.ejson , new TypeToken<APIM_UserFriendList>() {
        }.getType() , listener);

    }
}
