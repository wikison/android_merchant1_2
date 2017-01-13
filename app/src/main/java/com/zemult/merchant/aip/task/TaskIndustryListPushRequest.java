package com.zemult.merchant.aip.task;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskIndustryListNew;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   1.1  用户获取自己发布的任务(全部,进行中,已结束)
public class TaskIndustryListPushRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	操作的用户id(预留)
        public int merchantId;   //商家id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public int state;    //	任务状态(-1:全部,0:进行中,1:已结束)

        public String ejson;


        public void convertJosn() {
            if(-1==merchantId){//自己发布的任务
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("state", state + ""), new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            }else{
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""), new Pair<String, String>("merchantId", merchantId + ""),
                        new Pair<String, String>("state", state + ""), new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")));
            }

        }
    }

    public TaskIndustryListPushRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRYLISTPUSH, input.ejson, new TypeToken<APIM_TaskIndustryListNew>() {
        }.getType(), listener);

    }
}
