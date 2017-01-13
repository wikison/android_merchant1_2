package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_ManagerNewsCommentList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取单个方案下的评论列表
public class ManagerNewsCommentListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	newsId			;	//	方案id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("newsId", newsId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public ManagerNewsCommentListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MANAGER_NEWS_COMMENT_LIST,input.ejson , new TypeToken<APIM_ManagerNewsCommentList>() {
        }.getType() , listener);

    }
}
