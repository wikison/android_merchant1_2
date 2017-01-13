package com.android.volley.base;

import android.content.Context;


/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class VolleyErrorHelper {

    public static String getMessage (Object arg0, Context arg1){
        if(arg0!=null&&arg0.toString().indexOf("No address associated with hostname")!=-1){
            return  "您的网络不可用";
        }
        return  arg0.toString();

    }
    public static String getMessage(Object arg0) {
        return  arg0.toString();
    }

}
