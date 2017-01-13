package com.zemult.merchant.util;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by zhangkai on 2016/6/8.
 */

public class DeviceManager {
    public static DeviceManager instance;
    private Context context;
    private String deviceId;
    private int screenWidth;
    private int screenHeigh;
    private final String DEVICEID_DIR = "Local.System";
    private final String CONFIG = "config";
    private ILogReportor reportor;

    public static DeviceManager instance(Application app) {
        if(instance == null) {
            instance = new DeviceManager(app);
        }

        return instance;
    }

    private DeviceManager(Context context) {
        this.context = context;
    }

    public void setReportor(ILogReportor reportor) {
        this.reportor = reportor;
    }

    public String deviceID() {
        if(!TextUtils.isEmpty(this.deviceId)) {
            return this.deviceId;
        } else {
            this.deviceId = this.getCacheDeviceId();
            if(TextUtils.isEmpty(this.deviceId)) {
                this.deviceId = this.geneDeviceId();
                this.cacheDeviceId(this.deviceId);
            }

            return this.deviceId;
        }
    }

    public String getVersionName() {
        String  versionName="";
        try {
            PackageInfo e = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
            versionName= e.versionName;
        } catch (Exception var2) {
        }

        return versionName;
    }



    private String getCacheDeviceId() {
        File file = null;
        String deviceId = null;
        String state = Environment.getExternalStorageState();
        if("mounted".equals(state)) {
            file = Environment.getExternalStorageDirectory();
        } else {
            file = this.context.getFilesDir();
            if(this.reportor != null) {
                this.reportor.report(this.getClass().getSimpleName() + "无法获取外部缓存");
            }
        }

        file = new File(new File(file, "Local.System"), "config");
        if(file.exists()) {
            try {
                FileInputStream e = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int l = e.read(buf);
                e.close();
                deviceId = new String(buf, 0, l, "UTF-8");
            } catch (Exception var7) {
                Log.v("DeviceManager",var7.toString());
                if(this.reportor != null) {
                    this.reportor.report(this.getClass().getSimpleName() + "从文件中读取deviceId时出错" + var7.toString());
                }
            }
        }

        return deviceId;
    }

    private String geneDeviceId() {
        StringBuilder sb = new StringBuilder();
        sb.append("a_");

        String deviceIdentity;
        try {
            TelephonyManager androidId = (TelephonyManager)this.context.getSystemService(Context.TELEPHONY_SERVICE);
            if(androidId != null) {
                deviceIdentity = androidId.getDeviceId();
                if(TextUtils.isEmpty(deviceIdentity)) {
                    sb.append("NullImei");
                } else {
                    sb.append(deviceIdentity);
                }

                sb.append("_");
            }
        } catch (Exception var4) {
            Log.v("DeviceManager",var4.toString());
            if(this.reportor != null) {
                this.reportor.report(this.getClass().getSimpleName() + "读取imei出错" + var4.toString());
            }
        }

        String androidId1 = Settings.Secure.getString(this.context.getContentResolver(), "android_id");
        if(TextUtils.isEmpty(androidId1)) {
            sb.append("NullAndroidId");
        } else {
            sb.append(androidId1);
        }

        sb.append("_");
        deviceIdentity = Build.VERSION.RELEASE + "_" + Build.MODEL + "_" + Build.BRAND;
        sb.append(deviceIdentity);
        return sb.toString();
    }

    private void cacheDeviceId(String deviceId) {
        File file = null;
        String state = Environment.getExternalStorageState();
        if("mounted".equals(state)) {
            file = Environment.getExternalStorageDirectory();
        } else {
            file = this.context.getFilesDir();
            if(this.reportor != null) {
                this.reportor.report(this.getClass().getSimpleName() + "无法获取外部缓存");
            }
        }

        file = new File(file, "Local.System");
        if(!file.exists() && !file.mkdir()) {
            if(this.reportor != null) {
                this.reportor.report(this.getClass().getSimpleName() + "创建缓存目录出错");
            }
        } else {
            file = new File(file, "config");

            try {
                FileOutputStream e = new FileOutputStream(file);
                e.write(deviceId.getBytes("UTF-8"));
                e.close();
            } catch (Exception var6) {
                Log.v("DeviceManager",var6.toString());
                if(this.reportor != null) {
                    this.reportor.report(this.getClass().getSimpleName() + "deviceId写入文件时出错");
                }
            }
        }

    }

    public int getScreenWidth() {
        if(this.screenWidth <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = (WindowManager)this.context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(dm);
            this.screenWidth = dm.widthPixels;
        }

        return this.screenWidth;
    }

    public int getScreenHeight() {
        if(this.screenHeigh <= 0) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager manager = (WindowManager)this.context.getSystemService(Context.WINDOW_SERVICE);
            manager.getDefaultDisplay().getMetrics(dm);
            this.screenHeigh = dm.heightPixels;
        }

        return this.screenHeigh;
    }

    public boolean isWap() {
        ConnectivityManager connManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if(activeNetInfo == null) {
            return false;
        } else if(activeNetInfo.getType() == 1) {
            return false;
        } else {
            if(activeNetInfo.getType() == 0) {
                String extraInfo = activeNetInfo.getExtraInfo();
                if(extraInfo == null) {
                    return false;
                }

                extraInfo = extraInfo.toLowerCase();
                if(extraInfo.contains("cmnet")) {
                    return false;
                }

                if(extraInfo.contains("cmwap")) {
                    return true;
                }

                if(extraInfo.contains("3gnet")) {
                    return false;
                }

                if(extraInfo.contains("3gwap")) {
                    return true;
                }

                if(extraInfo.contains("uninet")) {
                    return false;
                }

                if(extraInfo.contains("uniwap")) {
                    return true;
                }

                if(extraInfo.contains("ctnet")) {
                    return false;
                }

                if(extraInfo.contains("ctwap")) {
                    return true;
                }

                if(extraInfo.contains("#777")) {
                    Cursor c = null;

                    try {
                        boolean var6;
                        try {
                            c = this.context.getContentResolver().query(Uri.parse("content://telephony/carriers/preferapn"), new String[]{"proxy", "port"}, (String)null, (String[])null, (String)null);
                            if(c.moveToFirst()) {
                                String e = c.getString(0);
                                if(e.length() > 3) {
                                    var6 = true;
                                    return var6;
                                }
                            }

                            boolean e1 = false;
                            return e1;
                        } catch (Exception var10) {
                            var6 = false;
                            return var6;
                        }
                    } finally {
                        if(c != null) {
                            c.close();
                        }

                    }
                }
            }

            return false;
        }
    }

    public boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null) {
            return false;
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    public boolean isWIFIConnection() {
        ConnectivityManager manager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        return activeNetworkInfo == null?true:activeNetworkInfo.getType() == 1;
    }

    public int getHeapSize() {
        return ((ActivityManager)this.context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024;
    }


    public void setUmengDeviceToken(String token) {
        SlashHelper.setSettingString(SlashHelper.APP.Key.UMENGDEVICETOKEN,token);
    }

    public String getUmengDeviceToken() {
        return  SlashHelper.getSettingString(SlashHelper.APP.Key.UMENGDEVICETOKEN,geneDeviceId());
    }
}
