package com.zemult.merchant.aip.task;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户参与探索(完成探索)
public class TaskIndustryComplete_1_2Request extends PostStringRequest<Type>  {

    public static class Input {

        public  int	userId			;	//	用户id
        public  int	taskIndustryId			;	//	探索 id
        public String	note			;	//	内容
        public String	pic=""			;	//	图片(多张","分隔)
        public String	audio			;	//	音频文件地址
        public String	audioTime			;	//	音频时长
        public String	voteIds			;	//	投票选项ids(多个用"，"分隔)
        public String	friends			;	//	@用户的ids(多个用"，"分隔)
        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("taskIndustryId", taskIndustryId+""),
                    new Pair<String, String>("note", note), new Pair<String, String>("pic", pic),
                    new Pair<String, String>("audio", audio), new Pair<String, String>("audioTime", audioTime),
                    new Pair<String, String>("voteIds", voteIds), new Pair<String, String>("friends", friends)
            ));
        }

    }

    public TaskIndustryComplete_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_COMPLETE_1_2,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
