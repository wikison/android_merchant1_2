
package com.zemult.merchant.activity;
import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.czt.mp3recorder.MP3Recorder;
import com.zemult.merchant.R;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.oss.OssFileService;
import com.zemult.merchant.util.oss.OssHelper;
import com.zemult.merchant.util.sound.HttpOperateUtil;

import cn.trinea.android.common.util.ToastUtils;

import static com.zemult.merchant.config.Constants.OSSENDPOINT;

public class SoundTaskRecordActivity extends MAppCompatActivity {

	private boolean isStartRecord;
	private ImageButton imageButton = null;
	private Button mStartPlayBtn;
	private Button mStopPlayBtn;

	private MediaPlayer mMediaPlayer;
	private OssFileService ossFileService;
	String ossFilename="",filename="";
	String URL_UPLOAD_FILEPATH="";

	private MP3Recorder mRecorder =null;
	AnimationDrawable voiceAnimation;
	ImageView voiceImageBtn;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soundtaskrecord);
		ossFileService = OssHelper.initFileOSS(SoundTaskRecordActivity.this);
		// initView
		imageButton = (ImageButton) findViewById(R.id.imageButtonDial);
		imageButton.setBackgroundResource(R.mipmap.btn_speak_normal);

		imageButton.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Log.i("keanbin", "onLongClick()");
				setTalkBtnBackground(true);
				// 停止播放声音
				stopPlay();
				// 开始录音
				startRecord();
				return true;
			}
		});
		imageButton.setOnTouchListener(new MyClickListener());

		mStartPlayBtn = (Button) findViewById(R.id.btn_mian_startPlay);
		mStartPlayBtn.setOnClickListener(mOnClickListener);
		mStopPlayBtn = (Button) findViewById(R.id.btn_mian_stopPlay);
		mStopPlayBtn.setOnClickListener(mOnClickListener);

		registerReceiver(new String[]{Constants.BROCAST_OSS_UPLOADSOUND});

		voiceImageBtn = ((ImageView) findViewById(R.id.iv_voice));
		ImageView head_iv = (ImageView) findViewById(R.id.iv_userhead);
		TextView tv = (TextView) findViewById(R.id.tv_length);
		ProgressBar pb = (ProgressBar) findViewById(R.id.pb_sending);
		TextView tv_userId = (TextView) findViewById(R.id.tv_userid);
		ImageView iv_read_status = (ImageView) findViewById(R.id.iv_unread_voice);

	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btn_mian_startPlay:
					if (isStartRecord) {
						stopRecord();
					}
					startPlay();
					break;

				case R.id.btn_mian_stopPlay:
					stopPlay();
					break;

				default:
					break;
			}
		}
	};

	public void startRecord() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "请插入SD卡！", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		isStartRecord = true;
		filename=SlashHelper.userManager().getUserId()+System.currentTimeMillis()+".mp3";
		File downloadFile = new File(Constants.SOUND_CACHE_DIR);
		AppUtils.deleteAllFiles(downloadFile);
		if (!downloadFile.exists()) {
			downloadFile.mkdirs();
		}
		URL_UPLOAD_FILEPATH= Constants.SOUND_CACHE_DIR+filename;
		mRecorder=new MP3Recorder(new File(URL_UPLOAD_FILEPATH));
		try {
			mRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopRecord() {
		if(null!=mRecorder){
			mRecorder.stop();
		}
		isStartRecord = false;
	}


	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					voiceImageBtn.setImageResource(R.mipmap.chatfrom_voice_playing_f3);
					voiceAnimation.stop();
					break;
			}
			super.handleMessage(msg);
		}

	};


	public void startPlay() {
		voiceImageBtn.setImageResource(R.drawable.voice_from_icon);
		voiceAnimation = (AnimationDrawable)voiceImageBtn.getDrawable();
		voiceAnimation.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				// TODO Auto-generated method stub
				String fileUrl = OSSENDPOINT+ossFilename;

				String fileName = HttpOperateUtil.downLoadFile(fileUrl,
						fileUrl.substring(fileUrl.lastIndexOf("/") + 1));

				Log.i("keanbin", "fileName = " + fileName);
				File file = new File(fileName);

				if (!file.exists()) {
					Toast.makeText(SoundTaskRecordActivity.this, "没有语音文件！", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				mMediaPlayer = MediaPlayer.create(SoundTaskRecordActivity.this,
						Uri.parse(fileName));
				mMediaPlayer.setLooping(false);
				mMediaPlayer.start();
				mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
					}
				});
				Looper.loop();
			}
		}).start();

	}

	public void stopPlay() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
		}
		if(voiceAnimation!=null){
			voiceImageBtn.setImageResource(R.mipmap.chatfrom_voice_playing_f3);
			voiceAnimation.stop();
		}

	}



	@Override
	protected void onDestroy() {
		stopPlay();
		stopRecord();
		super.onDestroy();
	}

	class MyClickListener implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			Log.i("keanbin", "event.getAction() = " + event.getAction());
			switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//
//					break;
				case MotionEvent.ACTION_UP:
					setTalkBtnBackground(false);
					if (isStartRecord) {
						// 停止录音
						showPd();
						stopRecord();
						File file = new File(URL_UPLOAD_FILEPATH);
						if (file.exists()&&file.length()>1204*3) {
							if(SlashHelper.userManager().getUserinfo()!=null){
										ossFilename="aduio/android_"+filename;
										ossFileService.asyncPutFile(ossFilename, URL_UPLOAD_FILEPATH);
										Log.d(getClass().getName(),ossFilename);
						}
						}
						else{
							dismissPd();
							Toast.makeText(SoundTaskRecordActivity.this, "您的语音时间太短！",
									Toast.LENGTH_SHORT).show();
						}
					}

					break;

				case MotionEvent.ACTION_CANCEL:
					setTalkBtnBackground(false);
					// TODO 异常放开，接下来一般做以下事情：删除录音文件

					// 停止录音
//					stopRecord();
					break;

				default:
					break;
			}
			return false;
		}

	}

	//接收广播回调
	@Override
	protected void handleReceiver(Context context, Intent intent) {

		if (intent == null || TextUtils.isEmpty(intent.getAction())) {
			return;
		}
		Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
		if (Constants.BROCAST_OSS_UPLOADSOUND.equals(intent.getAction())) {
			if (intent.getStringExtra("status").equals("ok")) {
				Log.d(getClass().getName(),ossFilename);
				ToastUtils.show(this, intent.getStringExtra("info"));
                dismissPd();
			} else {
				ToastUtils.show(this, intent.getStringExtra("info"));
			}
		}
	}

	public void setTalkBtnBackground(boolean isTalk) {
		if (isTalk) {
			imageButton.setBackgroundResource(R.mipmap.btn_speak_pressed);
		} else {
			imageButton.setBackgroundResource(R.mipmap.btn_speak_normal);
		}

	}

}
