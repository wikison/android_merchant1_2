package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * 首页商家列表          全是已上线的
 */

public class MerchantFirstPageListRequest extends PostStringRequest<Type> {


    public static class Input {
        public int	operateUserId			;	//	操作的用户id(预留)
        public String center; //用户中心点坐标规则：经度和纬度用","分割;默认"119.969499,31.817949"
        public String  city;  //市编号
        public int   industryId;  //行业id(为-1时 表示全行业,-2表示推荐:附近商家 3000m)

        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数
        public String ejson;


        public void convertJosn(){
            if (operateUserId == 0)
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("center", center),
                        new Pair<String, String>("city", city),
                        new Pair<String, String>("industryId", industryId+""),
                        new Pair<String, String>("page", page+""),
                        new Pair<String, String>("rows", rows+""))
                );
            else
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("center", center),
                        new Pair<String, String>("city", city),
                        new Pair<String, String>("industryId", industryId+""),
                        new Pair<String, String>("page", page+""),
                        new Pair<String, String>("rows", rows+""))
                );
        }

    }

    public MerchantFirstPageListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_FIRSTPAGE_LIST,input.ejson , new TypeToken<APIM_MerchantList>() {
        }.getType() , listener);

    }

}
