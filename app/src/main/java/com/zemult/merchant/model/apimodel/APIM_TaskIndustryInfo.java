package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Task;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_TaskIndustryInfo extends CommonResult {
    @Expose
    public M_Task taskIndustryInfo;//角色任务详情
    @Expose
    public int  isRight;//当前操作用户是否可以领取/完成该任务(0:否,1:是)--（游客默认0）
    @Expose
    public int taskState;//当前操作用户对这个任务的状态(0:未领取,1:已领取未完成,2:已领取且已完成)（游客默认0）
    @Expose
    public M_Task taskIndustryRecordInfo;//角色任务详情
}
