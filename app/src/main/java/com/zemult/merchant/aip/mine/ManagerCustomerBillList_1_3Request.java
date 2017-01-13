package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserBillList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//消费报表-营销经理角色
public class ManagerCustomerBillList_1_3Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("page", page+""),
                    new Pair<String, String>("rows", rows+""),
                    new Pair<String, String>("userId", userId+"")));
        }
    }

    public ManagerCustomerBillList_1_3Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_CUSTOMER_BILLLIST_1_3,input.ejson , new TypeToken<APIM_UserBillList>() {
        }.getType() , listener);

    }
}
