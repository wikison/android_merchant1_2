package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Voucher;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserVoucherList extends CommonResult {
    @Expose
    public List<M_Voucher> voucherList;//1.1优惠券使用记录列表

    @Expose
    public List<M_Voucher> voucherUserList;//1.1商家的代金券使用记录列表




    @Expose
    public M_Voucher  voucherUserInfo;//用户的代金券详情信息

    @Expose
    public M_Voucher voucherInfo;//代金券信息

    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
