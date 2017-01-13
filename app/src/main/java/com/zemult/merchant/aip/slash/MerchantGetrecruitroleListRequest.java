package com.zemult.merchant.aip.slash;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_MerchantGetrecruitroleList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取 商家的可申请角色列表（上线商户为申请,未上线商户为预申请）--用户申请商户经营角色不需审核，直接通过,已经申请过的显示已申请
public class MerchantGetrecruitroleListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public  int	operateUserId			;	//	操作的用户id(预留)
        public  int	merchantId			;	//	商家(场景)id
        public  int	page			;	//	获取第x页的数据
        public  int	rows			;	//	每次获取的数据个数

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("operateUserId", operateUserId+""), new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public MerchantGetrecruitroleListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_GETRECRUITROLELIST,input.ejson , new TypeToken<APIM_MerchantGetrecruitroleList>() {
        }.getType() , listener);

    }
}
