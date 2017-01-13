package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//删除标签--我的标签包
public class UserTagDel_1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	tagId				;	//	标签id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(new Pair<String, String>("tagId", tagId+""),
                    new Pair<String, String>("userId", userId+"")));
        }
    }

    public UserTagDel_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_TAG_DEL_1_2,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
