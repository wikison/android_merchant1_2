package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_TaskSearchIndustryRecordList extends CommonResult {
    @Expose
    public List<M_Task> recordList;//任务记录列表
    @Expose
    public List<M_Task> taskIndustryList;//任务记录列表
    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
