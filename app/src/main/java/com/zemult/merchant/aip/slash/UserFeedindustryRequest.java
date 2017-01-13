package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//提交新角色
public class UserFeedindustryRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public  String	industryName			;	//	角色行业
        public String	roleName			;	//	角色名称
        public String	merchantName			;	//	角色场景(商户)
        public String	note			;	//	角色职能描述

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryName", industryName),
                    new Pair<String, String>("roleName", roleName+""), new Pair<String, String>("merchantName", merchantName),
                    new Pair<String, String>("note", note+"")));
        }

    }

    public UserFeedindustryRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_FEEDINDUSTRY,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
