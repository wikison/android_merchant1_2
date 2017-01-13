package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对探索记录评论取消点赞
public class TaskIndustryCommentGooddelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	taskIndustryRecordCommentId				;	//用户探索记录的评论 id


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("taskIndustryRecordCommentId", taskIndustryRecordCommentId+"")
            ));
        }

    }

    public TaskIndustryCommentGooddelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMMENT_GOODDEL,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
