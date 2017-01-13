package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户删除相册照片
public class UserPicDelRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public String	picIds				;	//	照片ids（多个用","分隔）


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("picIds",picIds)));
        }

    }

    public UserPicDelRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_PIC_DEL,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
