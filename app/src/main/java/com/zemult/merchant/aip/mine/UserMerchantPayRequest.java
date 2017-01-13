package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//选择买单(找TA买单)
public class UserMerchantPayRequest extends PostStringRequest<Type>  {

    public static class Input {

        public String number				;	//	订单号
        public int	payType				;	//	支付类型(0:账户余额,1:支付宝)

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("number", number),
                    new Pair<String, String>("payType", payType+"")));

        }

    }

    public UserMerchantPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MERCHANT_PAY_DO,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
