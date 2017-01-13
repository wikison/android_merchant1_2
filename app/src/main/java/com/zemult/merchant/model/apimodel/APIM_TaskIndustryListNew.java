package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_TaskIndustryListNew extends CommonResult {
    @Expose
   public List<M_Task> taskList;
    @Expose
    public int   maxpage;//当分页获取时，最大的页数
    @Expose
    public List<M_Task> taskIndustryRecordList;//其它用户完成记录列表
    @Expose
    public List<M_Task> recordList;


}
