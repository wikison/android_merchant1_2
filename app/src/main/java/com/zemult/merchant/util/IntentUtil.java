package com.zemult.merchant.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Pair;

import com.zemult.merchant.R;

public class IntentUtil {
    private static final String strTag = "IntentUtil";

    public static void start_activity(Activity activity, Class<?> cls, Pair<String, String>... pairs) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < pairs.length; i++) {
            intent.putExtra(pairs[i].first, pairs[i].second);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void intStart_activity(Activity activity, Class<?> cls, Pair<String, Integer>... pairs) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        for (int i = 0; i < pairs.length; i++) {
            intent.putExtra(pairs[i].first, pairs[i].second);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void start_activity(Activity activity, String className, Pair<String, String>... pairs) {
        String packageName = activity.getPackageName();
        ComponentName componentName = new ComponentName(packageName, className);
        Intent intent = new Intent();
        intent.setComponent(componentName);
        for (int i = 0; i < pairs.length; i++) {
            intent.putExtra(pairs[i].first, pairs[i].second);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
