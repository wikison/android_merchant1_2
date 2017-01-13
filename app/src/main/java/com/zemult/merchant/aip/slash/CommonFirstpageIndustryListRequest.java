package com.zemult.merchant.aip.slash;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;
import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 获取首页的行业列表
public class CommonFirstpageIndustryListRequest extends PostStringRequest<Type>  {

    public CommonFirstpageIndustryListRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_FIRSTPAGE_INDUSTRYLIST, "" , new TypeToken<APIM_CommonGetallindustry>() {
        }.getType() , listener);

    }
}
