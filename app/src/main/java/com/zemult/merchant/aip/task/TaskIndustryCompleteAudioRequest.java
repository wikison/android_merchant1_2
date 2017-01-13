package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户完成角色任务--语音
public class TaskIndustryCompleteAudioRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	taskIndustryRecordId				;	//用户角色任务领取 id
        public String audio;//音频文件地址
        public String      audioTime;//音频时长


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
            new Pair<String, String>("audio", audio), new Pair<String, String>("audioTime", audioTime)
            ));
        }

    }

    public TaskIndustryCompleteAudioRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMPLATE_AUDIO,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
