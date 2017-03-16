package com.zemult.merchant.aip.friend;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFriendList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//搜索好友列表(条件:名称--用户名/所在公司/职位/角色名)
public class UserFriendListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public String	name			;	//	名称(用户名/所在公司/职位/角色名)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数


        public String ejson;


        public void convertJosn(){
            if(TextUtils.isEmpty(name))
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserFriendListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_ATTRACTLIST,input.ejson , new TypeToken<APIM_UserFriendList>() {//USER_FRIENDLIST
        }.getType() , listener);

    }
}
