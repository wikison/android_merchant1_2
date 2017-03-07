package com.zemult.merchant.app;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.wxlib.util.SysUtil;
import com.bugtags.library.Bugtags;
import com.umeng.fb.push.FeedbackPush;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.zemult.merchant.R;
import com.zemult.merchant.database.SQLHelper;
import com.zemult.merchant.im.sample.InitHelper;
import com.zemult.merchant.util.SlashHelper;

import zema.volley.network.VolleyUtil;

/**
 * Created by wikison on 2016/6/2.
 */
public class AppApplication extends MultiDexApplication {

    private static AppApplication mAppApplication;
    private static Context mContext;
    private static Handler handler;
    private static AppApplication _instance;
    private PushAgent mPushAgent;
    private SQLHelper sqlHelper;
    public static Boolean ISDEBUG = false;
    public int iPasswordState = 0;

    public AppApplication() {
        _instance = this;
    }

    private static final String TAG = "AppApplication";
    public static final String NAMESPACE = "openimdemo";
    public static final String UPDATE_STATUS_ACTION = "com.zemult.merchant.action.UPDATE_STATUS";

    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取Application
     */
    public static synchronized AppApplication getApp() {
        return mAppApplication;
    }

    public static Handler getHandler() {
        return handler;
    }

//    public static OkHttpClient getOkHttpClient() {
//        return okHttpClient;
//    }

    public static AppApplication instance() {
        if (_instance == null) {
            throw new IllegalStateException("Application not init!!!");
        }
        return _instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mAppApplication = this;
        //BTGInvocationEventBubble(悬浮小球)、
        //BTGInvocationEventShake(摇一摇)、
        //BTGInvocationEventNone(静默)
        if (SlashHelper.getSettingBoolean(SlashHelper.APP.Key.BUGTAGS, false)) {
            Bugtags.start("6679467463a300e215edf11b22698c14", this, Bugtags.BTGInvocationEventShake);
        } else {
            Bugtags.start("6679467463a300e215edf11b22698c14", this, Bugtags.BTGInvocationEventNone);
        }


        initUmeng();

        handler = new Handler();
        initialize();

        //微信 AppID：wxff28b23674780edc       AppSecret：6381fea488c0c05f2ec9604ef0fadb56
        PlatformConfig.setWeixin("wx0e6067b5bc878112", "9802020708ca2ee7fcfb7502761d4f7c");
        //新浪微博
        PlatformConfig.setSinaWeibo("4271231474", "58e53b10dad7c485e1db09553bd3356f");
        //QQ
        PlatformConfig.setQQZone("1106006502", "Pn8yLHqoyLlMVBtA");

        //友盟IM
        if (mustRunFirstInsideApplicationOnCreate()) {
            //todo 如果在":TCMSSevice"进程中，无需进行openIM和app业务的初始化，以节省内存
            return;
        }

        //初始化云旺SDK
        InitHelper.initYWSDK(this);
        //初始化反馈功能(未使用反馈功能的用户无需调用该初始化)
//        InitHelper.initFeedBack(this);
        //初始化多媒体SDK，小视频和阅后即焚功能需要使用多媒体SDK
//        AlibabaSDK.asyncInit(this, new InitResultCallback() {
//            @Override
//            public void onSuccess() {
//                Log.e(TAG, "-----initTaeSDK----onSuccess()-------" );
//                try {
//                    MediaService mediaService = AlibabaSDK.getService(MediaService.class);
//                    mediaService.enableHttpDNS(); //如果用户为了避免域名劫持，可以启用HttpDNS
////                    mediaService.enableLog(); //在调试时，可以打印日志。正式上线前可以关闭
//                }catch (Exception e){
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//                Log.e(TAG, "-------onFailure----msg:" + msg + "  code:" + code);
//            }
//        });
    }

    private boolean mustRunFirstInsideApplicationOnCreate() {
        //必须的初始化
        SysUtil.setApplication(this);
        mContext = getApplicationContext();
        return SysUtil.isTCMSServiceProcess(mContext);
    }


    private void initialize() {
        initRequestQueue();
    }

    private void initRequestQueue() {
        VolleyUtil.initialize(mContext);
    }


    /**
     * 获取数据库Helper
     */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new SQLHelper(mAppApplication);
        return sqlHelper;
    }


    void initUmeng() {
        FeedbackPush.getInstance(this).init(false);
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);

        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                    }
                });
            }

            /**
             * 参考集成文档的1.6.4
             * http://dev.umeng.com/push/android/integration#1_6_4
             * */
            @Override
            public Notification getNotification(Context context,
                                                UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);

                        return builder.build();

                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };

        mPushAgent.setNotificationClickHandler(notificationClickHandler);


        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                UmLog.i(TAG, "device token: " + deviceToken);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                UmLog.i(TAG, "register failed: " + s + " " + s1);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

    }
}
