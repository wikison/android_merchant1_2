package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Merchant;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_MerchantGetinfo extends CommonResult {
    @Expose
    public M_Merchant merchant;

}
