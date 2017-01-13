package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Manager;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_ManagerGetmanagerinfo extends CommonResult{
    @Expose
    M_Manager manager;

    @Expose
    public String icon; // 角色图标 地址
    @Expose
    public String levelName; // 用户的当前角色等级(例；6)
    @Expose
    public int level; // 用户等级的经验值(例；lv6：600)
    @Expose
    public int levelEXP; // 下一级等级所需的经验值(例；lv7：700)
    @Expose
    public int nextEXP; // 用户当前的经验值(例：638)
    @Expose
    public int place; // 等级排名


}
