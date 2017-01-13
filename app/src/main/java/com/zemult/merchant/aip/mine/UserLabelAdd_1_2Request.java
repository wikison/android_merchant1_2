package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//贴标签
public class UserLabelAdd_1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	operateUserId				;	//	操作的用户id
        public int	tagId				;	//	标签id
        public String note;
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(new Pair<String, String>("tagId", tagId+""),new Pair<String, String>("note", note),
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("operateUserId", operateUserId+"")));
        }

    }

    public UserLabelAdd_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_LABEL_ADD_1_2,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
