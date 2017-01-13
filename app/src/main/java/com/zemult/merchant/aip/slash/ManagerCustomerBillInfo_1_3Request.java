package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//消费总额-营销经理角色
public class ManagerCustomerBillInfo_1_3Request extends PostStringRequest<Type>  {

    public static class Input {
       public  int	userId			;	//	用户id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+"")));
        }

    }

    public ManagerCustomerBillInfo_1_3Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_CUSTOMER_BILLINFO_1_3,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
