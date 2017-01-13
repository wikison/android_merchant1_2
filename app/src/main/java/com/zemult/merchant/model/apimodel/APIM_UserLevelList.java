package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Level;


/**
 * 用户等级
 *
 * @author djy
 * @time 2016/8/3 9:07
 */

public class APIM_UserLevelList extends CommonResult {
    @Expose
    public M_Level levelInfo;

    @Expose
    public int level;//用户的当前角色等级(例；6)
    @Expose
    public int nextEXP;//下一级等级所需的经验值(例；700)

    @Expose
    public int experience;   //用户当前的经验值(例：638)


}
