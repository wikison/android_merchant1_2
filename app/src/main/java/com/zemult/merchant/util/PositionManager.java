package com.zemult.merchant.util;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zemult.merchant.app.AppApplication;

/**
 * Created by wikison on 2016/6/20.
 */
public class PositionManager {
    private static PositionManager instance;
    AMapLocationClient mLocationClient;
    AMapLocation aMapLocation;

    public static PositionManager instance() {
        if (instance == null) {
            instance = new PositionManager();
        }
        return instance;
    }

    public AMapLocation getMapLocation() {
        if (aMapLocation == null) {
            initLocation();
        }
        return aMapLocation;
    }

    public void setMapLocation(AMapLocation aMapLocation) {
        this.aMapLocation = aMapLocation;
    }

    public void initLocation() {
        mLocationClient = new AMapLocationClient(AppApplication.getApp());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation mapLocation) {
                if (mapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        aMapLocation = mapLocation;
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
}
