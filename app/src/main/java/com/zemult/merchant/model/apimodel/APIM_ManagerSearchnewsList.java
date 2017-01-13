package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_News;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_ManagerSearchnewsList extends CommonResult {
    @Expose
    public List<M_News> newsList;
    @Expose
    public List<M_News> favoriteList;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
