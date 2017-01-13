package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//实体商户入驻--添加全新的商户时
public class MerchantAddentityNewRequest extends PostStringRequest<Type>  {

    public static class Input {
       public  int	userId			;	//	用户id
        public   int	industryId			;	//	行业(场景分类)id
        public   String	name			;	//	场景名称
        public   String	shortName			;	//	简称
        public   String	tel			;	//	联系电话--店里的
        public   String	province			;	//	场景省 地区编号
        public   String	city			;	//	场景市 地区编号
        public   String	area			;	//	场景区 地区编号
        public   String	address			;	//	详细地址
        public   String	east			;	//	商家地址的经度 (高德地图上的)
        public   String	west			;	//	商家地址的纬度 (高德地图上的)
        public   String	IDphotos			;	//	商家证件照(多张照片地址以","分隔,例如:"http://www.inroids.com/x1.jpg,http://www.inroids.com/x2.jpg")
        public   String	bankCard			;	//	商户银行卡号
        public   double	commissionDiscount			;	//	挂靠的营销经理的佣金百分比(0-100)

        public String ejson;

        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""), new Pair<String, String>("industryId", industryId+""),
                    new Pair<String, String>("name", name), new Pair<String, String>("shortName", shortName),
                    new Pair<String, String>("tel", tel),
                    new Pair<String, String>("commissionDiscount", commissionDiscount + ""),
                    new Pair<String, String>("province", province), new Pair<String, String>("city", city),
                    new Pair<String, String>("area", area), new Pair<String, String>("address", address),
                    new Pair<String, String>("east", east), new Pair<String, String>("west", west),
                    new Pair<String, String>("bankCard", bankCard),
                    new Pair<String, String>("IDphotos", IDphotos)));
        }

    }

    public MerchantAddentityNewRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_ADDENTITY_NEW,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
