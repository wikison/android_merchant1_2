package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;

import java.util.ArrayList;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserFriendListOther extends CommonResult {

    //1.1 用户查看和他人的共同好友列表
    @Expose
   public ArrayList<M_Userinfo> friendList;

    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
