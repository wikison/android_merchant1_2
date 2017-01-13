package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//新的朋友(接受列表)
public class UserRequestFriendListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public  int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数
        public String lasttime;//前端最新一条记录的时间(格式为:yyyy-MM-dd HH:mm:ss)


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("lasttime", lasttime),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserRequestFriendListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_REQUEST_FRIENDLIST,input.ejson , new TypeToken<APIM_UserFriendList>() {
        }.getType() , listener);

    }
}
