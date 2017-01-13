package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_HotWord;

import java.util.List;


/**
 * Created by Wikison on 2016/6/8.
 */

public class APIM_HotList extends CommonResult {
    @Expose
    public List<M_HotWord> hotList;

    @Expose
    public int maxpage;//当分页获取时，最大的页数

}
