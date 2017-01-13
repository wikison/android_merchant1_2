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
public class TaskIndustryRecordListTask1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	操作用户id
        public int	taskIndustryId			;	//	任务 id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数
        public int	orderType			;	//	排序方式(0:时间倒排序,1:点赞数倒排序)

        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+""),
                        new Pair<String, String>("taskIndustryId", taskIndustryId + ""),
                        new Pair<String, String>("orderType", orderType + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+""),
                        new Pair<String, String>("taskIndustryId", taskIndustryId + ""),
                        new Pair<String, String>("orderType", orderType + "")));
        }
    }

    public TaskIndustryRecordListTask1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORDLIST_TASK_1_2,input.ejson , new TypeToken<APIM_TaskIndustryListNew>() {
        }.getType() , listener);

    }
}
