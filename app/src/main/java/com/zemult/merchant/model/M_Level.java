package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * 等级
 * @author djy
 * @time 2016/8/3 9:03
 */

public class M_Level {
    @Expose
    public String userName;    //	名字
    @Expose
    public String userHead;    //	头像
    @Expose
    public int level		;  //	用户等级
    @Expose
    public int taskNum		;  //	完成角色任务数
    @Expose
    public String taskDiscount		;  // 任务达成率(例:90%)
    @Expose
    public int industryLevel		;  //	最高的角色等级
    @Expose
    public int industryNum		;  //	角色总数
    @Expose
    public int goodNum		;  //	用户累计被赞的数目




}
