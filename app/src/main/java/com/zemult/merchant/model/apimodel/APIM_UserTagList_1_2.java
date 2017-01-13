package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Label;

import java.util.List;


/**
 * Created by Wikison on 2016/6/8.
 */

public class APIM_UserTagList_1_2 extends CommonResult {
    @Expose
    public List<M_Label> tagList;//我的标签包列表
    @Expose
    public List<M_Label> labelHistoryList;//我给别人贴标签的记录列表       我的被别人贴的标签的记录列表
    @Expose
    public List<M_Label> labelList;//大家眼中的我的标签列表


    @Expose
    public int maxpage;//当分页获取时，最大的页数

}
