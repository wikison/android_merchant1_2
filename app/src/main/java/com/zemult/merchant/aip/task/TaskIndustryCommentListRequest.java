package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsCommentList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   1.1 获取角色任务的评论列表
public class TaskIndustryCommentListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	操作用户id(游客可不传)
        public int	taskIndustryRecordId			;	//	用户角色任务领取 id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            if(operateUserId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId + ""),
                        new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId + ""),
                        new Pair<String, String>("page", page + ""), new Pair<String, String>("rows", rows + "")));

        }
    }

    public TaskIndustryCommentListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMMENTLIST,input.ejson , new TypeToken<APIM_ManagerNewsCommentList>() {
        }.getType() , listener);

    }
}
