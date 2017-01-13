package com.zemult.merchant.aip.friend;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//设置好友资料
public class UserFriendinfoSetRequest extends PostStringRequest<Type>  {

    public static class Input {
        int	userId			;	//	用户id
        int	friendId			;	//	好友的用户id
        String	remark			;	//	备注名
        int	isShow			;	//	好友是否不能看我的资源圈信息(0:否,1:是)
        int	isLook			;	//	是否不看好友的资源圈信息(0:否,1:是)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
            new Pair<String, String>("remark", remark),new Pair<String, String>("isShow", isShow+""),new Pair<String, String>("isLook", isLook+""),
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("friendId", friendId+"")));
        }

    }

    public UserFriendinfoSetRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FRIENDINFO_SET,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
