package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取用户的场景列表
public class MerchantOtherMerchantListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	operateUserId			;	//	操作的用户id(预留)
        public int	userId			;	//	被查看用户的id
        public String	center			;	//	用户中心点坐标(type=1时 必填) 规则：经度和纬度用","分割;例 "119.971736,31.829737"
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            if(operateUserId == 0)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("userId", userId+""),
                        new Pair<String, String>("center", center),
                        new Pair<String, String>("page", page+""),
                        new Pair<String, String>("rows", rows+"")
                        ));
            else
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("userId", userId+""),  new Pair<String, String>("center", center),
                        new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));

        }

    }

    public MerchantOtherMerchantListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_OTHER_LIST,input.ejson , new TypeToken<APIM_MerchantList>() {
        }.getType() , listener);

    }
}
