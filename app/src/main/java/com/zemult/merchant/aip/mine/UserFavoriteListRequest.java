package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//我的收藏--搜索用户收藏列表(条件:名称--用户名/方案内容)
public class UserFavoriteListRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public String name;    //	场景名称(模糊)
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数


        public String ejson;


        public void convertJosn() {
            if(name == null)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""), new Pair<String, String>("name", name),
                        new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
        }

    }

    public UserFavoriteListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FAVORITE_LIST, input.ejson, new TypeToken<APIM_ManagerSearchnewsList>() {
        }.getType(), listener);
    }
}
