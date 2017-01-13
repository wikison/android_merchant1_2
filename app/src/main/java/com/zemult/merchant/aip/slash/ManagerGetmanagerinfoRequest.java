package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerGetmanagerinfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 用户 单角色的详情
public class ManagerGetmanagerinfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	industryId			;	//	角色id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + "") ,
                    new Pair<String, String>("industryId", industryId + "")));
        }

    }

    public ManagerGetmanagerinfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_GETMANAGERINFO,input.ejson , new TypeToken<APIM_ManagerGetmanagerinfo>() {
        }.getType() , listener);

    }
}
