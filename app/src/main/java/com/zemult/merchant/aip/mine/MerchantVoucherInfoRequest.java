package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2016/8/10.
 */

//历史记录中商家的代金券明细
public class MerchantVoucherInfoRequest extends PostStringRequest<Type> {

    public static class Input {
        public int	voucherId				;	//	代金券id


        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("voucherId", voucherId+"")));
        }

    }

    public MerchantVoucherInfoRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_VOUCHER_INFO,input.ejson , new TypeToken<APIM_UserVoucherList>() {
        }.getType() , listener);

    }
}

