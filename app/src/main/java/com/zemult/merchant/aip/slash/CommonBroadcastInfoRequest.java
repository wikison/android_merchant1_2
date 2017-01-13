package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 获取广播消息
public class CommonBroadcastInfoRequest extends PostStringRequest<Type> {

    public static class Input {
        public String city;       // 城市编号
        public String lasttime;   //前端最新一条记录的时间(格式为:yyyy-MM-dd HH:mm:ss)---返回这时间之后的
        public int page;
        public int rows;

        public String ejson;


        public void convertJosn(){
            if(city != null){
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("lasttime", lasttime),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + ""),
                        new Pair<String, String>("city", city)
                ));
            }else {
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("lasttime", lasttime),
                        new Pair<String, String>("page", page + ""),
                        new Pair<String, String>("rows", rows + "")
                ));
            }
        }

    }

    public CommonBroadcastInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_BROADCAST_INFOLIST,input.ejson , new TypeToken<APIM_CommonSysMessageList>() {
        }.getType() , listener);

    }
}
