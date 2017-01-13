package com.zemult.merchant.aip.mine;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserFansList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//我的关注
public class UserAttractListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;    //	用户id
        public String name;    //	手机号或昵称(模糊搜索)
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


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

    public UserAttractListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_ATTRACTLIST,input.ejson , new TypeToken<APIM_UserFansList>() {
        }.getType() , listener);

    }
}
