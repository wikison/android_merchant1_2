package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户 成为某经营人角色(参与角色)
public class ManagerAddmanagerRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	industryId			;	//	角色id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryId", industryId+"")));
        }

    }

    public ManagerAddmanagerRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_ADDMANAGER,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
