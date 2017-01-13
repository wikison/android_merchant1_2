package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_MerchantList extends CommonResult {
    @Expose
    public List<M_Merchant> merchantList;
    @Expose
    public List<M_Merchant> requestList;
    @Expose
    public List<M_Merchant> merchants;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数
}
