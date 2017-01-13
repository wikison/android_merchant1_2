package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//删除标签--大家眼中的我
public class UserLabelDel_1_2Request extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	labelId				;	//	标签id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(new Pair<String, String>("labelId", labelId+""),
                    new Pair<String, String>("userId", userId+"")));
        }

    }

    public UserLabelDel_1_2Request(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_LABEL_DEL_1_2,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
