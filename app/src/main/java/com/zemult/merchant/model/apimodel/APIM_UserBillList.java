package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;

import java.util.List;


/**
 * Created by Wikison on 2016/6/8.
 */

public class APIM_UserBillList extends CommonResult {
    @Expose
    public List<M_Bill> billList;
    @Expose
    public List<M_Bill> userPayList;
    @Expose
    public int maxpage;//当分页获取时，最大的页数

}
