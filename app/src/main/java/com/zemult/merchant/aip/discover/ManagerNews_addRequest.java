package com.zemult.merchant.aip.discover;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户(经营人)发布方案
public class ManagerNews_addRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	industryId			;	//	角色id
        public String	note			;	//	内容
        public String	pic=""			;	//	图片(多张","分隔)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryId", industryId+""),
                    new Pair<String, String>("note", note),
                    new Pair<String, String>("pic",pic)));
        }

    }

    public ManagerNews_addRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
