package com.zemult.merchant.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.SplashActivity;
import com.zemult.merchant.app.base.BaseWebViewActivity;
import com.zemult.merchant.util.SlashHelper;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Developer defined push intent service. 
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}. 
 * @author lucas
 *
 */
public class MyPushIntentService extends UmengMessageService {
	private static final String TAG = MyPushIntentService.class.getName();

	@Override
	public void onMessage(Context context, Intent intent) {
		try {
			//可以通过MESSAGE_BODY取得消息体
			String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			UmLog.d(TAG, "message=" + message);    //消息体
			UmLog.d(TAG, "custom=" + msg.custom);    //自定义消息的内容
			UmLog.d(TAG, "title=" + msg.title);    //通知标题
			UmLog.d(TAG, "text=" + msg.text);    //通知内容
			int notifyId=0;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			mBuilder.setContentTitle(msg.title)
					.setContentText(msg.text)
					.setTicker(msg.title)
					.setWhen(System.currentTimeMillis())
					.setPriority(Notification.PRIORITY_DEFAULT)
					.setOngoing(false)
					.setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_VIBRATE)
					.setSmallIcon(R.mipmap.icon_launcher);//设置通知小ICON
			// after_open 必填 值为"go_app", "go_url", "go_activity", "go_custom"
			if (msg.after_open.equals("go_app")){
				notifyId=1;
				Intent  notificationIntent  = new Intent(context, SplashActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent =PendingIntent.getActivity(this, 0,notificationIntent, 0);
				mBuilder.setContentIntent(contentIntent);
			}
			if (msg.after_open.equals("go_url")){
				notifyId=2;
				Intent  notificationIntent  = new Intent(context, BaseWebViewActivity.class);
				notificationIntent.putExtra("url",msg.url);
				notificationIntent.putExtra("titlename",msg.title);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent contentIntent =PendingIntent.getActivity(this, 0,notificationIntent, 0);
				mBuilder.setContentIntent(contentIntent);
			}
			if (msg.after_open.equals("go_activity")){
				notifyId=3;
				if (SlashHelper.userManager().getUserinfo() != null) {
					//1.0版本 会跳转 NewFriendActivity  SystemMessageActivity  都需要用户登录
					Intent notificationIntent = new Intent(context, Class.forName(msg.activity));
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
					mBuilder.setContentIntent(contentIntent);
				}else {
					Intent  notificationIntent  = new Intent(context, LoginActivity.class);
					notificationIntent.putExtra("actfrom","notification");
					notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent contentIntent =PendingIntent.getActivity(this, 0,notificationIntent, 0);
					mBuilder.setContentIntent(contentIntent);
				}
			}

			mNotificationManager.notify(notifyId, mBuilder.build());

			// 对完全自定义消息的处理方式，点击或者忽略
			boolean isClickOrDismissed = true;
			if(isClickOrDismissed) {
				//完全自定义消息的点击统计
				UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
			} else {
				//完全自定义消息的忽略统计
				UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
			}

			// 使用完全自定义消息来开启应用服务进程的示例代码
			// 首先需要设置完全自定义消息处理方式
			// mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
			// code to handle to start/stop service for app process
			JSONObject json = new JSONObject(msg.custom);
			String topic = json.getString("topic");
			if(topic != null && topic.equals("appName:startService")) {
				// 在友盟portal上新建自定义消息，自定义消息文本如下
				//{"topic":"appName:startService"}
				if(Helper.isServiceRunning(context, NotificationService.class.getName()))
					return;
				Intent intent1 = new Intent();
				intent1.setClass(context, NotificationService.class);
				context.startService(intent1);
			} else if(topic != null && topic.equals("appName:stopService")) {
				// 在友盟portal上新建自定义消息，自定义消息文本如下
				//{"topic":"appName:stopService"}
				if(!Helper.isServiceRunning(context, NotificationService.class.getName()))
					return;
				Intent intent1 = new Intent();
				intent1.setClass(context, NotificationService.class);
				context.stopService(intent1);
			}
		} catch (Exception e) {
			UmLog.e(TAG, e.getMessage());
		}
	}
}
