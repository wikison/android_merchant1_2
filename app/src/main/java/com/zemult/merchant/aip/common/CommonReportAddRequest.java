package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//添加举报
public class CommonReportAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;       //用户id
        public int type;       //举报类型(1:色情低俗,2:广告骚扰,3:政治敏感,4:欺诈骗钱,5:违法(暴力恐怖、违禁品),6:抄袭、诽谤、冒用，10:其它)
        public int infoType;       //举报信息类型(1:角色任务,2:任务完成记录,3:心情小记,4:任务完成记录评论,5:心情小记评论)
        public int infoId;       //对应的举报信息类型的id
        public String note;       //补充说明
        public String pic;       //图片(多张","分隔)
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("type", type + ""),
                    new Pair<String, String>("infoType", infoType + ""),
                    new Pair<String, String>("infoId", infoId + ""),
                    new Pair<String, String>("note", note),
                    new Pair<String, String>("pic", pic)
            ));
        }

    }

    public CommonReportAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_REPORT_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
