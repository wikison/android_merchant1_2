package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Fan;
import com.zemult.merchant.model.M_Userinfo;

import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserFansList extends CommonResult {
    @Expose
    public List<M_Fan> fansList;
    @Expose
    public List<M_Fan> userList;
    @Expose
    public int maxpage;//当分页获取时，最大的页数


    @Expose public String head;//						我的头像
    @Expose public int level	;//						我的等级
   // @Expose public int experience		;//					我的经验值
    @Expose public  int place	;//						我的排名




}
