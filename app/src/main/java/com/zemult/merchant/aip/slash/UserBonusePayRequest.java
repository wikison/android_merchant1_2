package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//生成红包支付单
public class UserBonusePayRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public String	name				;	//	任务名称
        public int	payType				;	//	支付类型(0:账户余额,1:支付宝)
        public double	money				;	//	买单金额
        public int	taskIndustryId				;	//任务id
        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("name", name),
                    new Pair<String, String>("taskIndustryId", taskIndustryId+""),
                    new Pair<String, String>("payType", payType+""), new Pair<String, String>("money", money+""),
                    new Pair<String, String>("userId", userId+"")));
        }
    }

    public UserBonusePayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_BONUSE_PAY,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
