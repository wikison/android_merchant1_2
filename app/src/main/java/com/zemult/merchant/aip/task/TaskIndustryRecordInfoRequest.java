package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryInfo;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//查看角色任务记录详情(已经完成的)  如果是投票类型的，需要获取投票相关信息
public class TaskIndustryRecordInfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	taskIndustryRecordId			;	//	用户角色任务领取 id
        public int	userId			;	//	操作用户id(游客可不传)


        public String ejson;


        public void convertJosn(){
            if(userId == 0)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+"")));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+"")));

        }

    }

    public TaskIndustryRecordInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORD_INFO_1_2,input.ejson , new TypeToken<APIM_TaskIndustryInfo>() {
        }.getType() , listener);

    }
}
