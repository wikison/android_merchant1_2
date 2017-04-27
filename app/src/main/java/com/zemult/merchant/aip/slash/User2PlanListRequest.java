package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.model.apimodel.APIM_PlanList;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   服务方案库列表
public class User2PlanListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	saleUserId			;	//	服务管家的用户id
        public int	merchantId		;	//	商家id
        public int	state		;	//	状态(-1:全部,0:未启用,1:已启用)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("saleUserId", saleUserId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("state", state + ""),
                    new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
        }
    }

    public User2PlanListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_PLAN_LIST,input.ejson , new TypeToken<APIM_PlanList>() {
        }.getType() , listener);

    }
}
