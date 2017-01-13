package com.zemult.merchant.util.imagepicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;


import com.zemult.merchant.config.Constants;

import java.util.List;

public class EditImageReciver extends BroadcastReceiver {

    private Handler handler;

    public EditImageReciver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            List<String> paths = intent.getStringArrayListExtra("path");
            Message message = new Message();
            message.what = Constants.EDIT_PHOTOS;
            message.obj = paths;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
