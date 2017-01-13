package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//查看角色任务详情(新任务)
public class TaskIndustryInfoRequest extends PostStringRequest<Type>  {

    public TaskIndustryInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_INFO, input.ejson, new TypeToken<APIM_TaskIndustryInfo>() {
        }.getType(), listener);

    }

    public static class Input {
        public int userId;    //	用户角色任务领取id(值为-1，表示新任务-即用户还没有领取任务)
        public int	taskIndustryId			;	//	角色任务id


        public String ejson;


        public void convertJosn(){
            if(userId == 0)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("taskIndustryId", taskIndustryId+"")));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("taskIndustryId", taskIndustryId+"")));

        }
    }
}
