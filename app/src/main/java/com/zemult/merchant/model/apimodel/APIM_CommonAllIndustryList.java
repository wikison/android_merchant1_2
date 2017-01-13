package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_IndustryClass;

import java.util.List;

/**
 * Created by admin on 2016/10/25.
 */

public class APIM_CommonAllIndustryList extends CommonResult {


    @Expose
    public List<M_IndustryClass> typeList; // 大分类列表
}
