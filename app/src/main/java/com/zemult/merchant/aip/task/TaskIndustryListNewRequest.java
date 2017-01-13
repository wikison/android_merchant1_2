package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   1.1  用户获取可领取的角色任务列表(新任务)             task_industry取,用户有该角色的任务,且没有领取过的
public class TaskIndustryListNewRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	操作的用户id(预留)
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
        }
    }

    public TaskIndustryListNewRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRYLIST_NEW,input.ejson , new TypeToken<APIM_TaskIndustryListNew>() {
        }.getType() , listener);

    }
}
