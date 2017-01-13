package com.zemult.merchant.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zemult.merchant.app.AppApplication;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangk  on 2016/6/8.
 */

public class SlashHelper {


    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        if (!"".equals(dateStr)) {
                            return format.parse(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            })
            .setDateFormat(pattern)
            .registerTypeAdapter(Object.class, new JsonDeserializer<Double>() {
                public Double deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        return json.getAsDouble();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0d;
                }
            })
            .create();

    static {
        preferences = AppApplication.instance().getSharedPreferences(
                "yogous_preferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public static final int getSettingInt(String key) {
        return preferences.getInt(key, 0);
    }

    public static final int getStttingInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static final void setSettingInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public static final boolean getSettingBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static final boolean getSettingBoolean(String key,
                                                  boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static final void setSettingBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public static final void setSettingString(String key, String status) {
        editor.putString(key, status).commit();
    }

    public static final String getSettingString(String key, String defaulStatus) {
        return preferences.getString(key, defaulStatus);
    }

    public static final Long getSettingLong(String key, Long defaulStatus) {
        return preferences.getLong(key, defaulStatus);
    }

    public static final void setSettingLong(String key, Long value) {
        editor.putLong(key, value).commit();
    }


    public static void clearData() {
        preferences.edit().clear().commit();
    }

    /**
     * 获取 DeviceManager，管理硬件相关的信息
     *
     * @return DeviceManager
     */
    public static DeviceManager deviceManager() {
        return DeviceManager.instance(AppApplication.instance());
    }

    public static UserManager userManager() {
        return UserManager.instance();
    }

    public static PositionManager positionManager() {
        return PositionManager.instance();
    }

    /**
     * 个人
     */
    public static final class User {
        public static class Key {
            public static final String USER_PWD = "USER_PWD";
            public static final String USER_INFO = "USER_INFO";//个人用户信息
            public static final String CONTACTSLIST = "CONTACTSLIST";//联系人列表
            public static final String UNREADMESSAGE = "UNREADMESSAGE";//未读消息
        }

    }

    /**
     * 系统
     */
    public static final class APP {
        public static class Key {
            public static final String AREA = "AREA";//省市
            public static final String COUNTRY = "COUNTRY";//区
            public static final String UMENMGPUSH = "UMENMGPUSH";//友盟推送
            public static final String IMAGESIZE = "IMAGESIZE";//大图小图切换
            public static final String URL_SHARE_NEWS = "URL_SHARE_NEWS";//分享方案的链接地址
            public static final String URL_SHARE_TASK = "URL_SHARE_TASK";//分享任务记录
            public static final String URL_SHARE_COMMISSION = "URL_SHARE_COMMISSION";//营销经理分享
            public static final String URL_SHARE_APP = "URL_SHARE_APP";//分享app/角色的链接地址
            public static final String UMENGDEVICETOKEN = "UMENGDEVICETOKEN";//友盟deviceToken


        }

    }
}
