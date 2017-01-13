package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//  查看单任务已经完成的记录列表
public class TaskIndustryRecordListTaskRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	taskIndustryId			;	//	任务 id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+""),
                    new Pair<String, String>("taskIndustryId", taskIndustryId + "")));
        }
    }

    public TaskIndustryRecordListTaskRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORDLIST_TASK,input.ejson , new TypeToken<APIM_TaskIndustryListNew>() {
        }.getType() , listener);

    }
}
