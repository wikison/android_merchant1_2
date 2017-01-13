package com.zemult.merchant.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.zemult.merchant.app.AppApplication;

import java.util.List;

/**
 * Created by Wikison on 2015/8/26.
 */
public class UIUtil {

    private static final String TAG = "UIUtil";
    private static int screenWidth = 0;
    private static int screenHeight = 0;
    private static float screenDensity = 0;
    private static int densityDpi = 0;
    private static int statusBarHeight = 0;


    public static void initialize(Context context) {
        if (context == null)
            return;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;     // 屏幕宽度
        screenHeight = metrics.heightPixels;   // 屏幕高度
        screenDensity = metrics.density;      // 0.75 / 1.0 / 1.5 / 2.0 / 3.0
        densityDpi = metrics.densityDpi;  //120 160 240 320 480
        Log.i(TAG, "screenDensity = " + screenDensity + " densityDpi = " + densityDpi);
    }

    public static int dip2px(float dipValue) {
        return (int) (dipValue * screenDensity + 0.5f);
    }

    public static int px2dip(float pxValue) {

        return (int) (pxValue / screenDensity + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * dip转为 px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px 转为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }





    public static int getScreenWidth() {
        return screenWidth;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    //获取屏幕高度
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static ColorStateList getColorStateList(int color) {
        return getContext().getResources().getColorStateList(color);

    }

    public static Drawable getDrawable(int drawable) {
        return getContext().getResources().getDrawable(drawable);
    }

    public static Context getContext() {
        return AppApplication.getContext();
    }

    public static String[] getStringArray(int array) {
        return getContext().getResources().getStringArray(array);
    }

    public static View inflate(int layout) {
        return View.inflate(getContext(), layout, null);
    }

    public static void runOnUIThread(Runnable runnable) {
        if (isRunOnUIThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    public static boolean isRunOnUIThread() {
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    public static int getMainThreadId() {
        return 0;
    }

    public static Handler getHandler() {
        return AppApplication.getHandler();
    }

    /**
     * 判断某个界面是否在前台
     * note: need permisson <uses-permission android:name="android.permission.GET_TASKS"/>
     *
     * @param context
     * @param className className
     */
    private boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }
}
