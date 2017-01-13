package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取用户的单场景下的角色列表
public class UserMerchantIndustryListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId			;	//	用户id
        public int	merchantId			;	//	场景id
        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserMerchantIndustryListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_MERCHANT_INDUSTRYLIST,input.ejson , new TypeToken<APIM_MerchantGetrecruitroleList>() {
        }.getType() , listener);

    }
}
