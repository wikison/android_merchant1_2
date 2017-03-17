package com.zemult.merchant.aip.common;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.apimodel.APIM_PresentList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取打赏金额种类
public class CommonRewardRequest extends PostStringRequest<Type>  {

    public CommonRewardRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_REWARD,"", new TypeToken<APIM_PresentList>() {
        }.getType() , listener);

    }
}
