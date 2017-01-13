package com.zemult.merchant.model.apimodel;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Friend;
import com.zemult.merchant.model.M_Userinfo;

import java.util.ArrayList;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class APIM_UserFriendList extends CommonResult {
    @Expose
   public ArrayList<M_Friend> friendList;

    //1.1 获取通讯录中不是用户好友的手机号(是平台账号,发过好友申请的也不显示)
    @Expose
    public ArrayList<M_Userinfo> userList;

    @Expose
    public int   maxpage;//当分页获取时，最大的页数

}
