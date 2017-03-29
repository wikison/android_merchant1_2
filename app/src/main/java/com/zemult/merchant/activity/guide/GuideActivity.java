package com.zemult.merchant.activity.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginActivity;
import com.zemult.merchant.activity.MainActivity;
import com.zemult.merchant.activity.RegisterActivity;
import com.zemult.merchant.activity.SplashActivity;
import com.zemult.merchant.activity.mine.ThirdBandPhoneActivity;
import com.zemult.merchant.aip.common.UserWxBandUserRequest;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.model.M_Userinfo;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.view.PreviewIndicator;
import com.zemult.merchant.view.PreviewVideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import zema.volley.network.ResponseListener;


public class GuideActivity extends BaseActivity {

    private ViewPager viewPage;
    private Fragment0 mFragment0;
    private Fragment1 mFragment1;
    private Fragment2 mFragment2;
    private Fragment3 mFragment3;
    private boolean misScrolled;

    private PagerAdapter mPgAdapter;
    private List<Fragment> mListFragment = new ArrayList<Fragment>();


    private PreviewVideoView mVideoView;
    private ViewPager mVpImage;
    private PreviewIndicator mIndicator;

    private List<View> mViewListone = new ArrayList<>();

    private String[] mTextoneResIds = new String[]{"消费预约 个性定制", "服务管家 直观筛选", "邀请嘉宾 与众不同", "沟通聊天 放心安全", "结账开票 赞赏激励"};
    private String[] mTexttwoResIds = new String[]{"更全面、更贴心", "更优质、更可靠", "更方便、更大气", "更安全、更隐私", "更快捷、更亲密"};
    private CustomPagerAdapter mAdapter;

    private int mCurrentPage = 0;
    private Subscription mLoop;
    Intent it;
    private UMShareAPI umShareAPI;
    private static final int REQ_THIRD_LOGIN = 0x120;


    @Override
    public void setContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
    }

    @Override
    public void init() {
        mVideoView = (PreviewVideoView) findViewById(R.id.vv_preview);
        mVpImage = (ViewPager) findViewById(R.id.vp_image);
        mIndicator = (PreviewIndicator) findViewById(R.id.indicator);

        SharedPreferences sharedPreferences = GuideActivity.this.getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {       //第一次
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        } else {
            startActivity(new Intent(GuideActivity.this, SplashActivity.class));
            GuideActivity.this.finish();
        }
        umShareAPI = UMShareAPI.get(this);


        //initView();

        mVideoView.setVideoURI(Uri.parse(getVideoPath()));

        doplayvideo();

        for (int i = 0; i < mTextoneResIds.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.preview_item, null, false);
            ((TextView) view.findViewById(R.id.iv_intro_text)).setText(mTextoneResIds[i]);
            ((TextView) view.findViewById(R.id.iv_second_text)).setText(mTexttwoResIds[i]);
            mViewListone.add(view);
        }

        mAdapter = new CustomPagerAdapter(mViewListone);
        mVpImage.setAdapter(mAdapter);
        mVpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPage = position;
                mIndicator.setSelected(mCurrentPage);
                //   startLoop();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                //最后一页左滑进入
//                switch (state) {
//                    case ViewPager.SCROLL_STATE_DRAGGING:
//                        misScrolled = false;
//                        break;
//                    case ViewPager.SCROLL_STATE_SETTLING:
//                        misScrolled = true;
//                        break;
//                    case ViewPager.SCROLL_STATE_IDLE:
//                        if (viewPage.getCurrentItem() == viewPage.getAdapter().getCount() - 1 && !misScrolled) {
//                            startActivity(new Intent(GuideActivity.this, SplashActivity.class));
//                            GuideActivity.this.finish();
//                        }
//                        misScrolled = true;
//                        break;
//                }

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        doplayvideo();
    }

    /**
     * 获取video文件的路径
     *
     * @return 路径
     */
    private String getVideoPath() {
        return "android.resource://" + this.getPackageName() + "/" + R.raw.intro_video;
    }

    private void doplayvideo() {
        //播放
        mVideoView.start();
        //循环播放
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
    }


//    /**
//     * 开启轮询
//     */
//    private void startLoop() {
//        if (null != mLoop) {
//            mLoop.unsubscribe();
//        }
//        mLoop = Observable.interval(0, 6 * 1000, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Long>() {
//                    @Override
//                    public void call(Long aLong) {
//                        mVideoView.seekTo(mCurrentPage * 6 * 1000);
//                        if (!mVideoView.isPlaying()) {
//                            mVideoView.start();
//                        }
//                    }
//                });
//    }


//	private void initView() {
//		viewPage = (ViewPager) findViewById(R.id.viewpager);
//		mFragment0 = new Fragment0();
//		mFragment1 = new Fragment1();
//		mFragment2 = new Fragment2();
//		mFragment3 = new Fragment3();
//
//		mListFragment.add(mFragment0);
//		mListFragment.add(mFragment1);
//		mListFragment.add(mFragment2);
//		mListFragment.add(mFragment3);
//
//		mPgAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
//				mListFragment);
//		viewPage.setAdapter(mPgAdapter);
//
//	}

//	public class MyPagerChangeListener implements OnPageChangeListener {
//
//		public void onPageSelected(int position) {
//
//		}
//
//		public void onPageScrollStateChanged(int state) {
//
//			//最后一页左滑进入
//			switch (state) {
//				case ViewPager.SCROLL_STATE_DRAGGING:
//					misScrolled = false;
//					break;
//				case ViewPager.SCROLL_STATE_SETTLING:
//					misScrolled = true;
//					break;
//				case ViewPager.SCROLL_STATE_IDLE:
//					if (viewPage.getCurrentItem() == viewPage.getAdapter().getCount() - 1 && !misScrolled) {
//						startActivity(new Intent(GuideActivity.this, SplashActivity.class));
//						GuideActivity.this.finish();
//					}
//					misScrolled = true;
//					break;
//			}
//
//		}
//
//		public void onPageScrolled(int position, float positionOffset,
//				int positionOffsetPixels) {
//		}
//
//	}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 这个Activity中有Fragment，限的通知。这句话不能注释，否则Fragment将接收不到获取权
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    private void thirdLogin() {
        umShareAPI.doOauthVerify(GuideActivity.this, SHARE_MEDIA.WEIXIN, doOauthVerifyListener);
    }

    private UMAuthListener doOauthVerifyListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            UMShareAPI.get(GuideActivity.this).getPlatformInfo(GuideActivity.this, SHARE_MEDIA.WEIXIN, getPlatformInfoListener);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "授权取消", Toast.LENGTH_SHORT).show();
        }
    };


    private UMAuthListener getPlatformInfoListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            user_wx_band_user(data.get("openid"), data.get("nickname"), data.get("headimgurl"));
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(getApplicationContext(), "获取信息取消", Toast.LENGTH_SHORT).show();
        }
    };
    private UserWxBandUserRequest userWxBandUserRequest;

    //根据微信号获取绑定的用户信息
    private void user_wx_band_user(final String openid, final String nickname, final String head) {
        loadingDialog.show();
        if (userWxBandUserRequest != null) {
            userWxBandUserRequest.cancel();
        }
        UserWxBandUserRequest.Input input = new UserWxBandUserRequest.Input();
        input.openid = openid;
        input.convertJosn();

        userWxBandUserRequest = new UserWxBandUserRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(final Object response) {
                if (((CommonResult) response).status == 1) {
                    if (((CommonResult) response).isBand == 0) { // 是否已经绑定了用户账号(0:否,1:是)
                        Intent intent = new Intent(GuideActivity.this, ThirdBandPhoneActivity.class);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("head", head);
                        intent.putExtra("openid", openid);
                        startActivityForResult(intent, REQ_THIRD_LOGIN);
                    } else {
                        // 直接获取信息
                        AppUtils.initIm(((CommonResult) response).userId + "", Urls.APP_KEY);
                        M_Userinfo userInfo = new M_Userinfo();
                        userInfo.setUserId(((CommonResult) response).userId);
                        UserManager.instance().saveUserinfo(userInfo);
                        Intent updateintent = new Intent(Constants.BROCAST_UPDATEMYINFO);
                        sendBroadcast(updateintent);
                        finish();
                    }
                } else {
                    ToastUtil.showMessage(((CommonResult) response).info);
                }
                loadingDialog.dismiss();
            }
        });
        sendJsonRequest(userWxBandUserRequest);
    }



    @OnClick({R.id.login_btn, R.id.register_btn, R.id.weixinlog_iv})
    public void onClick(View view) {
            switch (view.getId()) {
                case R.id.login_btn:
                    it = new Intent(this, LoginActivity.class);
                    startActivityForResult(it, 1);

                    break;
                case R.id.register_btn:
                    it = new Intent(this, RegisterActivity.class);
                    startActivityForResult(it, 1);
                    break;
                case R.id.weixinlog_iv:
                    thirdLogin();
                    break;
        }
    }


    public static class CustomPagerAdapter extends PagerAdapter {

        private List<View> mViewList;

        public CustomPagerAdapter(List<View> viewList) {
            mViewList = viewList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                it = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(it);
            } else if (requestCode == REQ_THIRD_LOGIN) {
                finish();
            }
        }
        umShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
