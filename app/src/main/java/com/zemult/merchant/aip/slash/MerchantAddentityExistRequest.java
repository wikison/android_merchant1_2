package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//实体商家入驻--选择已经存在的商户时
public class MerchantAddentityExistRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public  int	merchantId			;	//	商户id
        public  String	shortName			;	//	简称
        public  String	IDphotos			;	//	商家证件照(多张照片地址以","分隔,例如:"http://www.inroids.com/x1.jpg,http://www.inroids.com/x2.jpg")
        public  String	bankCard			;	//	商户银行卡号
        public  String	tel			;	//	联系电话--店里的
        public  double	commissionDiscount			;	//	挂靠的营销经理的佣金百分比(0-100)


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("shortName", shortName),
                    new Pair<String, String>("bankCard", bankCard),
                    new Pair<String, String>("IDphotos", IDphotos),
                    new Pair<String, String>("tel", tel),
                    new Pair<String, String>("commissionDiscount", commissionDiscount +"")));
        }

    }

    public MerchantAddentityExistRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_ADDENTITY_EXIST,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
