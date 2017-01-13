package com.zemult.merchant.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zemult.merchant.util.SlashHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wikison on 2016/6/20.
 */
public class MapService extends Service {

    AMapLocation mapLocation;
    int iTime = 0;
    private AMapLocationClient mLocationClient;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            iTime++;
            if (iTime >= 60) {
                iTime = 0;
                initLocation();
            } else {

            }
            super.handleMessage(msg);
        }
    };
    private MyBinder mBinder = new MyBinder();
    private Timer timer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    public AMapLocation getMapLocation() {
        return mapLocation;
    }

    public void initLocation() {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        mapLocation = aMapLocation;
                        Log.i("Location", aMapLocation.getCity());
                        SlashHelper.positionManager().setMapLocation(aMapLocation);
                    } else {
                        //定位失败
                        //aMapLocation==null
                    }
                }
            }
        });
        mLocationClient.startLocation();
    }

    public class MyBinder extends Binder {
        MapService getService() {
            return MapService.this;
        }
    }
}
