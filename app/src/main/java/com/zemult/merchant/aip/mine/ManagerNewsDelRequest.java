package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//删除方案
public class ManagerNewsDelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId				;	//	操作的用户id(预留)
        public int	newsId				;	//	信息id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("operateUserId", operateUserId+""), new Pair<String, String>("newsId", newsId+"")));
        }

    }

    public ManagerNewsDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_DEL,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
