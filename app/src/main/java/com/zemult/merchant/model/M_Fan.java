package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Fan extends M_Userinfo{
    @Expose
    public String managerNames;    //	角色名称(多个,分隔)
    //	状态(0:已关注1:未关注)
//    @Expose
//    public int state;

    //1.1  用户的等级排行榜
    @Expose public int place	;//				排名


}
