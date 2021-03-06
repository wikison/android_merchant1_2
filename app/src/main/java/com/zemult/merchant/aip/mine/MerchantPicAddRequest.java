package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//商家添加相册照片
public class MerchantPicAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	merchantId				;	//	商家id
        public String	pics				;	//	上传的照片地址(多个用"，"分隔)


        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("merchantId", merchantId+""), new Pair<String, String>("pics",pics)));
        }

    }

    public MerchantPicAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_PIC_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
