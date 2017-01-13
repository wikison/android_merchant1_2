package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//更换绑定手机号
public class UserEditphoneBandRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public String	phone				;	//	新的手机号
        public String	idCard;// 身份证号
        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("phone", phone+""),
                    new Pair<String, String>("idCard", idCard+"")));
        }

    }

    public UserEditphoneBandRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_EDITPHONE_BAND,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
