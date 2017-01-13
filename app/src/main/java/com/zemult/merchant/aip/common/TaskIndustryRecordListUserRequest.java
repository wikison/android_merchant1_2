package com.zemult.merchant.aip.common;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;

import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/9/5.
 */

//TA完成的探索列表
public class TaskIndustryRecordListUserRequest extends PostStringRequest<Type> {

    public static class Input {

        public int operateUserId;//操作用户id
        public int userId;       //用户id(被查看的)
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;


        public void convertJosn() {
            if(operateUserId==0){
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            }
            else{
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId + ""),
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            }
        }

    }

    public TaskIndustryRecordListUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORDLIST_USER_1_2, input.ejson, new TypeToken<APIM_TaskSearchIndustryRecordList>() {
        }.getType(), listener);

    }
}

