package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户对方案取消点赞
public class ManagerNewsGooddelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	newsId			;	//	角色id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("newsId", newsId+"")));
        }

    }

    public ManagerNewsGooddelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_GOODDEL,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
