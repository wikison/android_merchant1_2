package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//   首页 任务记录列表
public class TaskIndustryRecordListFirstpageRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int orderType; //排序方式(0:发布时间,1:人气-点赞数)
        public int  sex;  //性别 (-1:全部,0:男,1:女)--筛选条件
        public int taskType;  //任务类型(-1:全部,0:图文,1:语音,2:投票,3:买单)--筛选条件
        public int   friendType;  //好友类型 (-1:全部,0:好友及关注,1:陌生人--非好友且非关注)--筛选条件

        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn() {
            if(userId == 0)
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("orderType", orderType + ""),
                        new Pair<String, String>("sex", sex + ""),
                        new Pair<String, String>("taskType", taskType + ""),
                        new Pair<String, String>("friendType", friendType + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")
                ));
            else
                ejson = Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId + ""),
                        new Pair<String, String>("orderType", orderType + ""),
                        new Pair<String, String>("sex", sex + ""),
                        new Pair<String, String>("taskType", taskType + ""),
                        new Pair<String, String>("friendType", friendType + ""),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")
                ));
        }
    }

    public TaskIndustryRecordListFirstpageRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORDLIST_FIRSTPAGE,input.ejson , new TypeToken<APIM_TaskSearchIndustryRecordList>() {
        }.getType() , listener);

    }
}
