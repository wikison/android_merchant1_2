package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 用户删除自己的评论
public class ManagerNewsCommentDelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	commentId			;	//	评论id(要删除的)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("commentId", commentId+"")));
        }

    }

    public ManagerNewsCommentDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_COMMENT_DEL,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
