package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/1/22.
 */
//修改用户状态(服务管家状态)
public class UserEditStateRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public int state;    //	获取第x页的数据

        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("state", state+"")));
        }

    }

    public UserEditStateRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_EDITSTATE,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
