package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;

import java.util.List;

/**
 * Created by admin on 2016/8/6.
 */
public class APIM_UserTaskList extends CommonResult {
    @Expose
    public List<M_Task>  taskList; //任务列表


    @Expose
    public int maxpage;//当分页获取时，最大的页数


}
