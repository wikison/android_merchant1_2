package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户进行实名认证
public class UserRealnameAttestationRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public String	phone				;	//	手机号
        public String	code				;	//	验证码
        public String	realName				;	//	姓名
        public String	idCard				;	//	身份证号


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("phone", phone)
            ,new Pair<String, String>("code",code),new Pair<String, String>("realName", realName)
                    ,new Pair<String, String>("idCard", idCard)));
        }

    }

    public UserRealnameAttestationRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_REALNAME_ATTESTATION,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
