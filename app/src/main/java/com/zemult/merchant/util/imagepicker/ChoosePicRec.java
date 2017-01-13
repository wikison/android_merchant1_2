package com.zemult.merchant.util.imagepicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;


import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.ImageHelper;

import java.util.List;

public class ChoosePicRec extends BroadcastReceiver {
    private Handler handler;

    public ChoosePicRec(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            List<String> paths = intent.getStringArrayListExtra("path");
            for(int i=0;i<paths.size();i++){
            	paths.set(i, ImageHelper.saveCompressBitmap4Upload(paths.get(i)));
            }
            Message message = new Message();
            message.what = Constants.CHOOSE_PHOTOS;
            message.obj = paths;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
