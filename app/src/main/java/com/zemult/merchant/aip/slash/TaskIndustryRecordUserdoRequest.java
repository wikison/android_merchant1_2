package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_TaskSearchIndustryRecordList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/31.
 */

//角色--猜你喜欢
public class TaskIndustryRecordUserdoRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数

        public String ejson;


        public void convertJosn() {

            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + "")
            ));
        }
    }

    public TaskIndustryRecordUserdoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.TASK_INDUSTRY_RECORD_USERDO_1_2, input.ejson, new TypeToken<APIM_TaskSearchIndustryRecordList>() {
        }.getType(), listener);

    }
}

