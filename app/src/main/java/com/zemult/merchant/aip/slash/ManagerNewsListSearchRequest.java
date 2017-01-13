package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerSearchnewsList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   1.1  新增 筛选 心情小记列表 (心情小记)
public class ManagerNewsListSearchRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	操作的用户id(预留)
        public String	name = ""			;	//	名称(用户名/内容文字)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("name", name),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
        }
    }

    public ManagerNewsListSearchRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWSLIST_SEARCH,input.ejson , new TypeToken<APIM_ManagerSearchnewsList>() {
        }.getType() , listener);

    }
}
