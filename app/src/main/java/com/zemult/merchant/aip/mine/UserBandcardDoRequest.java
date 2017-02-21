package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//绑定银行卡
public class UserBandcardDoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public String	name				;	//	持卡人姓名
        public String	bankNumber				;	//	银行卡号
        public String	bankName				;	//	银行卡名称
        public String	idCard				;	//	身份证号
        public String	phone				;	//	手机号
        public String	code				;	//	手机号验证码


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("bankNumber", bankNumber),
                    new Pair<String, String>("bankName", bankName),
                    new Pair<String, String>("idCard", idCard),
                    new Pair<String, String>("phone", phone),
                    new Pair<String, String>("code", code)
                    ));
        }

    }

    public UserBandcardDoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_BAND_CARD_DO,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
