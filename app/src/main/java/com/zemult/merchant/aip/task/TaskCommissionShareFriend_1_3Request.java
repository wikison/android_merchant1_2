package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//营销经理分享消费任务- -发送给好友
public class TaskCommissionShareFriend_1_3Request extends PostStringRequest<Type>  {

    public TaskCommissionShareFriend_1_3Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_COMMISSION_SHARE_FRIEND_1_3, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);
    }

    public static class Input {
        public int	userId				;	//	用户id
        public int taskIndustryRecordId;    //探索记录 id
        public String friends;    //好友的ids（多个用","分隔）

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson( new Pair<String, String>("friends", friends),
                    new Pair<String, String>("userId", userId + ""), new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId + "")
            ));
        }

    }
}
