package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 查看用户的已完成任务记录列表
public class TaskIndustryRecordListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	operateUserId			;	//	操作的用户id(预留)
        public int	industryId			;	//	角色id(-1表示全部角色)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数


        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("industryId", industryId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("industryId", industryId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
        }
    }

    public TaskIndustryRecordListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORDLIST,input.ejson , new TypeToken<APIM_TaskSearchIndustryRecordList>() {
        }.getType() , listener);

    }
}
