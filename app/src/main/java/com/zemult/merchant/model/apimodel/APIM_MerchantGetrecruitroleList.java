package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Industry;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_MerchantGetrecruitroleList extends CommonResult {
    @Expose
    public List<M_Industry> industryList;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
