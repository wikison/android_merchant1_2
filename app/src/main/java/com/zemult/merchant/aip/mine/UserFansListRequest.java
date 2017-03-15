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

//获取 用户的 可能熟悉的人(推荐服务管家)
public class UserFansListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int operateUserId;    //	用户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


        public String ejson;


        public void convertJosn(){
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserFansListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_SYS_SALEUSERLIST,input.ejson , new TypeToken<APIM_UserFansList>() {
        }.getType() , listener);

    }
}
