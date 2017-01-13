package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Bill;


/**
 * Created by Wikison on 2016/6/8.
 */

public class APIM_UserBillInfo extends CommonResult {
    @Expose
    public M_Bill billInfo;
    @Expose
    public M_Bill userPayInfo;
}
