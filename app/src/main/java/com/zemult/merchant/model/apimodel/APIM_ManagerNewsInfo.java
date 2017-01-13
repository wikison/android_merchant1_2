package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_News;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_ManagerNewsInfo extends CommonResult {
    @Expose
    public M_News newsInfo;

}
