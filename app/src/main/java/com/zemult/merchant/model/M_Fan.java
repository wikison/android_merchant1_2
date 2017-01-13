package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Fan {
    @Expose
    public int userId;    //	粉丝用户id
    @Expose
    public int sex;    //	粉丝用户性别
    @Expose
    public String name;    //	名字
    @Expose
    public String head;    //	头像
    @Expose
    public String managerNames;    //	角色名称(多个,分隔)
    @Expose
    public String note;    //	用户签名(暂无)
    @Expose
    public int state;    //	状态(0:已关注1:未关注)
    //1.1  用户的等级排行榜
    @Expose public int place	;//				排名
    @Expose public  int level		;//				用户等级
    @Expose public  double experience		;//				用户经验值
    @Expose public int isFan		;//				我是否关注该用户(0:否1:是)

    @Expose
    public String  userHead;//客户头像
    @Expose
    public String  userName;//客户头像
    @Expose
    public int  userSex;//客户头像

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }
}
