package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   1.1  用户获取已经领取的任务列表(待完成,已完成,已过期)      task_industry_record取,其中已过期的--状态为已过期或者当前时间超过截止时间未完成,  已过期状态定时器修正
public class TaskIndustryListRequest extends PostStringRequest<Type>  {

    public TaskIndustryListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRYLIST, input.ejson, new TypeToken<APIM_TaskIndustryListNew>() {
        }.getType(), listener);

    }

    public static class Input {
        public int	userId			;	//	操作的用户id(预留)
        public int state;    //	任务状态(-1新任务,0:待完成,1:已完成,2:已过期 )
        public int industryId;   //角色id(-1:全部)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),new Pair<String, String>("state", state + ""),
                    new Pair<String, String>("industryId", industryId + ""),
                    new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
        }
    }
}
