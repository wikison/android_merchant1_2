package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_SearchUsersList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * 商家下的营销经理列表(最近联系)
 */

public class MerchantSaleuserListLatestRequest extends PostStringRequest<Type> {


    public static class Input {
        public int	operateUserId			;	//	操作的用户id(预留)
        public int   merchantId;  //商家(场景)的id

        public int	page			;	//	获取第x页的数据
        public int	rows			;	//	每次获取的数据个数
        public String ejson;


        public void convertJosn(){
            if (operateUserId == 0)
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("merchantId", merchantId+""),
                        new Pair<String, String>("page", page+""),
                        new Pair<String, String>("rows", rows+""))
                );
            else
                ejson= Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("operateUserId", operateUserId+""),
                        new Pair<String, String>("merchantId", merchantId+""),
                        new Pair<String, String>("page", page+""),
                        new Pair<String, String>("rows", rows+""))
                );
        }

    }

    public MerchantSaleuserListLatestRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_SALEUSER_LIST_LATEST_1_1,
                input.ejson , new TypeToken<APIM_SearchUsersList>() {
        }.getType() , listener);

    }

}
