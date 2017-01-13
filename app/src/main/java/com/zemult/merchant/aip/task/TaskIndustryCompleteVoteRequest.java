package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户完成角色任务--投票
public class TaskIndustryCompleteVoteRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	taskIndustryRecordId				;	//用户角色任务领取 id
        public String voteIds;//投票选项ids(多个用"，"分隔)


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
                    new Pair<String, String>("voteIds", voteIds+"")
            ));
        }

    }

    public TaskIndustryCompleteVoteRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSRTY_COMPLETE_VOTE,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
