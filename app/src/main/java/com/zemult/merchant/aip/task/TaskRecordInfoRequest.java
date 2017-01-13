package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//通过营销经理分享的探索记录id 获取商户的信息(包含本次买单的折扣)
public class TaskRecordInfoRequest extends PostStringRequest<Type> {

    public TaskRecordInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.TASK_RECORD_INFO, input.ejson, new TypeToken<APIM_TaskIndustryInfo>() {
        }.getType(), listener);

    }

    public static class Input {
        public int taskIndustryRecordId;    //	角色任务id


        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId + "")));
        }
    }
}
