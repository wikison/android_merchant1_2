package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对角色任务评价
public class TaskIndustryCommentAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	taskIndustryRecordId				;	//	用户角色任务领取 id
        public int	type				;	//	类型(0:对信息评论;1:回复别人的评论 )
        public int	ruserId			;	//	被回复用户id(type=1时必填)
        public String	note				;	//	评论内容


        public String ejson;


        public void convertJosn(){
            if(type == 0)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
                        new Pair<String, String>("type", type+""),
                        new Pair<String, String>("note",note)));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
                        new Pair<String, String>("type", type+""),
                        new Pair<String, String>("ruserId", ruserId+""),
                        new Pair<String, String>("note",note)));

        }

    }

    public TaskIndustryCommentAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMMENT_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
