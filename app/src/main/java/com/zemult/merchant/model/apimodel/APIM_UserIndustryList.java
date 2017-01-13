package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_UserRole;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserIndustryList extends CommonResult {

    @Expose
    public List<M_UserRole> industryList;
    @Expose
    public int maxpage;
}
