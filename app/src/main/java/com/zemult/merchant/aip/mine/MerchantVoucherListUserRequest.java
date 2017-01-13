package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户在某商家下的可用的的优惠券列表
public class MerchantVoucherListUserRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;    //	用户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public int merchantId;    //商家(场景)id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),  new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public MerchantVoucherListUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_VOUCHERLIST_USER,input.ejson , new TypeToken<APIM_UserVoucherList>() {
        }.getType() , listener);

    }
}
