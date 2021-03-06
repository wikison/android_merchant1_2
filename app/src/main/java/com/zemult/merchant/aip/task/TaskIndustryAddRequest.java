package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户领取角色任务  添加到task_industry_record
public class TaskIndustryAddRequest extends PostStringRequest<Type>  {

    public TaskIndustryAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);
    }

    public static class Input {
        public int	userId				;	//	用户id
        public int taskIndustryId;    //用户角色任务领取 id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""), new Pair<String, String>("taskIndustryId", taskIndustryId + "")
            ));
        }

    }
}
