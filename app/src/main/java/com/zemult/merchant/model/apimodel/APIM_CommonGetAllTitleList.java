package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Title;

import java.util.List;

/**
 * Created by admin on 2017/03/17.
 */

public class APIM_CommonGetAllTitleList extends CommonResult {

    @Expose
    public List<M_Title> titleList; // 预邀单主题列表
}
