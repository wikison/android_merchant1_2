package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//查看自己的角色任务记录详情(用户未完成的任务)
public class TaskIndustryRecordInfoTodoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	taskIndustryRecordId			;	//	用户角色任务领取 id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+"")));
        }

    }

    public TaskIndustryRecordInfoTodoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORD_INFO_TODO,input.ejson , new TypeToken<APIM_TaskIndustryInfo>() {
        }.getType() , listener);

    }
}
