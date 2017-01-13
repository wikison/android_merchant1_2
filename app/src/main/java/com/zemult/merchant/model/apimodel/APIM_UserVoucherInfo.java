package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Voucher;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserVoucherInfo extends CommonResult {
    @Expose
    public M_Voucher voucherUserInfo;//1.1代金券详情信息
    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
