package com.zemult.merchant.util;

import com.zemult.merchant.model.M_Userinfo;

public class UserManager {

    private static UserManager instance;
    M_Userinfo m_Userinfo;

    public static UserManager instance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public M_Userinfo getUserinfo() {
        if (m_Userinfo == null) {
            m_Userinfo  = SlashHelper.gson.fromJson(SlashHelper.getSettingString(SlashHelper.User.Key.USER_INFO,""),M_Userinfo.class);
        }
       return m_Userinfo ;
    }

    public int getUserId() {
        if (m_Userinfo == null) {
            getUserinfo();
        }
        return m_Userinfo==null?0:m_Userinfo.getUserId();
    }
    public void saveUserinfo(M_Userinfo userinfo) {
        SlashHelper.setSettingString(SlashHelper.User.Key.USER_INFO,SlashHelper.gson.toJson(userinfo));
        m_Userinfo=userinfo;
    }
}
