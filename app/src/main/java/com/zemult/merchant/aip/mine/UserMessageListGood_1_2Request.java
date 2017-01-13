package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonSysMessageList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取 赞 消息列表
public class UserMessageListGood_1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数
        public int userId;
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserMessageListGood_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MESSAGELIST_GOOD_1_2,input.ejson , new TypeToken<APIM_CommonSysMessageList>() {
        }.getType() , listener);

    }
}
