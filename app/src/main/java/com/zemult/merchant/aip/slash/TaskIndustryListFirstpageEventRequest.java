package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 首页活动列表
public class TaskIndustryListFirstpageEventRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            if(userId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")
                ));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")
                ));

        }
    }

    public TaskIndustryListFirstpageEventRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_LIST_FIRSTPAGE_EVENT_1_3,input.ejson , new TypeToken<APIM_TaskSearchIndustryRecordList>() {
        }.getType() , listener);

    }
}
