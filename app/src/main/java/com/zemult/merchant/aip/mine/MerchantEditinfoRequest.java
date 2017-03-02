package com.zemult.merchant.aip.mine;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//修改商家(场景)信息
public class MerchantEditinfoRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	merchantId				;	//是  	商家(场景)的id
//        public String	head				;	//否  	商家头像
        public String	pic				;	//否	  商家封面
//        public double	commissionDiscount				;	//否	  佣金
//        public String	tel				;	//  否	联系电话--店里的
//        public String	detail				;	//  否	商家简介



        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("merchantId", merchantId+""),
//                    new Pair<String, String>("head", head),
//                    new Pair<String, String>("commissionDiscount", commissionDiscount + ""),
//                    new Pair<String, String>("tel", tel),
                    new Pair<String, String>("pic", pic)));
        }

    }

    public MerchantEditinfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_EDITINFO,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
