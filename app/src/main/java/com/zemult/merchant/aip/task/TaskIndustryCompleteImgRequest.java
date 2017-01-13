package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户完成角色任务--图文
public class TaskIndustryCompleteImgRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	taskIndustryRecordId				;	//用户角色任务领取 id
        public String note;//内容
        public String      pic="";//图片(多张","分隔)


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
                    new Pair<String, String>("note", note), new Pair<String, String>("pic", pic)
            ));
        }

    }

    public TaskIndustryCompleteImgRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMPLATE_IMG,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
