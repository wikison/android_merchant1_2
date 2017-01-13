package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//经营人用户解绑商户
public class ManagerDelmanagerRequest extends PostStringRequest<Type>  {

    public static class Input {
        int	userId			;	//	用户id
        int	industryId			;	//	角色id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryId", industryId+"")));
        }

    }

    public ManagerDelmanagerRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_DELMANAGER,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
