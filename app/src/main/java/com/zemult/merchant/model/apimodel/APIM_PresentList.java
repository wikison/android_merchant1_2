package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_HotWord;
import com.zemult.merchant.model.M_Present;

import java.util.List;


/**
 *
 * @author djy
 * @time 2017/1/20 13:25
 */

public class APIM_PresentList extends CommonResult {
    @Expose
    public List<M_Present> userPresentList;

}
