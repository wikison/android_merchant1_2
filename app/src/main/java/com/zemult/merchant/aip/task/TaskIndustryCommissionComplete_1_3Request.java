package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户参与活动探索- -（消费类的）
public class TaskIndustryCommissionComplete_1_3Request extends PostStringRequest<Type>  {

    public TaskIndustryCommissionComplete_1_3Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMMISSION_COMPLETE_1_3, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);
    }

    public static class Input {
        public int	userId				;	//	用户id
        public int taskIndustryId;    //探索记录 id

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""), new Pair<String, String>("taskIndustryId", taskIndustryId + "")
            ));
        }

    }
}
