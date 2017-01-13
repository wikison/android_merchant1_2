package com.zemult.merchant.aip.slash;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_CommonGetallindustry;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 获取首页的所有行业分类(包含行业)
public class CommonFirstpageAllIndustryListRequest extends PostStringRequest<Type>  {

    public CommonFirstpageAllIndustryListRequest(ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.COMMON_FIRSTPAGE_ALL_INDUSTRYLIST, "" , new TypeToken<APIM_CommonGetallindustry>() {
        }.getType() , listener);

    }
}
