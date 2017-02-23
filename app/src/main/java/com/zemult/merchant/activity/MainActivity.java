package com.zemult.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.IYWPushListener;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.IYWConversationUnreadChangeListener;
import com.alibaba.mobileim.conversation.IYWMessageLifeCycleListener;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.alibaba.mobileim.conversation.YWMessageType;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.login.IYWConnectionListener;
import com.alibaba.mobileim.login.YWLoginCode;
import com.alibaba.mobileim.login.YWLoginState;
import com.android.volley.VolleyError;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.common.UmLog;
import com.umeng.message.common.UmengMessageDeviceConfig;
import com.zemult.merchant.R;
import com.zemult.merchant.aip.mine.UserInfoOwnerRequest;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.MAppCompatActivity;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.fragment.HomeFragment;
import com.zemult.merchant.fragment.MineFragment;
import com.zemult.merchant.fragment.MyFollowFragment;
import com.zemult.merchant.im.common.Notification;
import com.zemult.merchant.im.sample.CustomConversationHelper;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.model.apimodel.APIM_UserLogin;
import com.zemult.merchant.push.MyPushIntentService;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.util.ToastUtil;
import com.zemult.merchant.util.UserManager;
import com.zemult.merchant.view.SlashMenuWindow;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;


public class MainActivity extends MAppCompatActivity implements View.OnClickListener {
    public static final String SYSTEM_INTERACTION = "interaction";
    public static final String SYSTEM_EXPRESSAGE = "expressage";
    public static final int ACTION_OVERLAY_PERMISSION = 100;
    public Handler handler = new Handler();
    String TAG = "MainActivity";
    SlashMenuWindow mSlashMenuWindow;
    private HomeFragment slashFragment;       //斜杠
    //    private MainFriendFragment mainFriendFragment;    //杠友
    private Fragment conversationFragment;    //杠友
    //private ContactsFragment contactsFragment;
    //  private InteractionFragment interactionFragment;
//    private DiscoverFragment discoverFragment;      //发现
//    private MyRoleFragment myRoleFragment;      //角色
    private MineFragment mineFragment;             //我的
    private MyFollowFragment myFollowFragment;

    private View slashLayout;
    private View sfriendLayout;
    private View discoverLayout;
    private View mineLayout;
    private View record_layout;
    private ImageView ivRecordSlash;
    private ImageView slashImage;
    private ImageView sfriendImage;
    private ImageView discoverImage;
    private ImageView mineImage;
//    private Fragment fragment;
    /**
     * 在Tab布局上显示消息标题的控件
     */
    private TextView slashText;
    private TextView sfriendText;
    private TextView discoverText;
    private TextView mineText;
    private TextView mUnread;

    boolean hasOverlay = false;

    //  private YWIMKit imkit;
    private FragmentTransaction transaction;
    public static YWIMKit mIMKit;
    public IYWConversationService mConversationService;
    public IYWConnectionListener mConnectionListener;
    public IYWConversationUnreadChangeListener mConversationUnreadChangeListener;
    public IYWMessageLifeCycleListener mMessageLifeCycleListener;
    public Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;
    private PushAgent mPushAgent;
    private LoginSampleHelper loginHelper;

    public IUmengRegisterCallback mRegisterCallback = new IUmengRegisterCallback() {

        @Override
        public void onSuccess(String s) {
            updateStatus();
            mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);
        }

        @Override
        public void onFailure(String s, String s1) {
            UmLog.i(TAG, "register failed: " + s + " " + s1);

        }
    };

    private long firstTime = 0; //记录第一次点击的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化布局元素
        initViews();
        fragmentManager = getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection(0);
        initUmeng();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        EventBus.getDefault().register(this);
        initIM();
        registerReceiver(new String[]{Constants.BROCAST_LOGIN, Constants.BROCAST_CLOSE_ACTIVITY_FORLABEL});
        registerReceiver(new String[]{Constants.BROCAST_UPDATEMYINFO});
        get_user_info_owner_request();
    }

    /**
     * 初始化聊天
     */
    private void initIM() {
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        if (mIMKit == null) {
            return;
        }
        mConversationService = mIMKit.getConversationService();
        initListeners();

//登录IM
        if (null != SlashHelper.userManager().getUserinfo()) {
            loginHelper = LoginSampleHelper.getInstance();
            AppUtils.initIm(SlashHelper.userManager().getUserId() + "", Urls.APP_KEY);
            loginHelper.login_Sample(SlashHelper.userManager().getUserId() + "", SlashHelper.userManager().getUserinfo().getPassword(), Urls.APP_KEY, new IWxCallback() {
                @Override
                public void onSuccess(Object... arg0) {
//                    Log.d("MainActivity",arg0.toString());
                }

                @Override
                public void onProgress(int arg0) {
                }

                @Override
                public void onError(int errorCode, String errorMessage) {
                }
            });
        }

    }


    @Override
    protected void handleReceiver(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        Log.d(getClass().getName(), "[onReceive] action:" + intent.getAction());
        if (Constants.BROCAST_LOGIN.equals(intent.getAction())) {
            initIM();
        }
        if (Constants.BROCAST_CLOSE_ACTIVITY_FORLABEL.equals(intent.getAction())) {
            mSlashMenuWindow.dismiss();
        }
        if (Constants.BROCAST_UPDATEMYINFO.equals(intent.getAction())) {
            if (SlashHelper.userManager().getUserinfo() != null) {
                get_user_info_owner_request();
            }

        }

    }

    /**
     * 初始化相关监听
     */
    private void initListeners() {
        //初始化并添加会话变更监听
        initConversationServiceAndListener();
        //初始化并添加群变更监听
//        addTribeChangeListener();
        //设置发送消息生命周期监听
        setMessageLifeCycleListener();
        //设置发送消息给黑名单中的联系人监听
//        setSendMessageToContactInBlackListListener();
        //添加IM连接状态监听
        addConnectionListener();


        initCustomConversation("", 0);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void exitRefresh(String s) {
        if ("exit".equals(s)) {
            clearSelection();
            transaction = fragmentManager.beginTransaction();

            slashImage.setImageResource(R.mipmap.shouye_icon);
            slashText.setTextColor(getResources().getColor(R.color.sel_color));
            transaction.hide(mineFragment).show(slashFragment).commitAllowingStateLoss();

            IntentUtil.start_activity(MainActivity.this, LoginActivity.class);
        }
    }

    //友盟注册
    void initUmeng() {
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(mRegisterCallback);
        if (SlashHelper.getSettingBoolean(SlashHelper.APP.Key.UMENMGPUSH, true)) {
            Log.i(TAG, "============initUmeng=================");
            mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
            mPushAgent.onAppStart();
        } else {
            if (!TextUtils.isEmpty(mPushAgent.getRegistrationId())) {
                SlashHelper.deviceManager().setUmengDeviceToken(mPushAgent.getRegistrationId());
            }
        }

    }

    private void updateStatus() {
        String pkgName = getApplicationContext().getPackageName();
        String info = String.format("DeviceToken:%s\n" + "SdkVersion:%s\nAppVersionCode:%s\nAppVersionName:%s",
                mPushAgent.getRegistrationId(), MsgConstant.SDK_VERSION,
                UmengMessageDeviceConfig.getAppVersionCode(this), UmengMessageDeviceConfig.getAppVersionName(this));
        Log.i("DeviceToken", info);
        if (!TextUtils.isEmpty(mPushAgent.getRegistrationId())) {
            SlashHelper.deviceManager().setUmengDeviceToken(mPushAgent.getRegistrationId());
        }

    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        slashLayout = findViewById(R.id.slash_layout);
        sfriendLayout = findViewById(R.id.sfriend_layout);
        discoverLayout = findViewById(R.id.discover_layout);
        mineLayout = findViewById(R.id.mine_layout);
        record_layout = findViewById(R.id.record_layout);

        ivRecordSlash = (ImageView) findViewById(R.id.record_image);
        slashImage = (ImageView) findViewById(R.id.slash_image);
        sfriendImage = (ImageView) findViewById(R.id.sfriend_image);
        discoverImage = (ImageView) findViewById(R.id.discover_image);
        mineImage = (ImageView) findViewById(R.id.mine_image);

        slashText = (TextView) findViewById(R.id.slash_text);
        sfriendText = (TextView) findViewById(R.id.sfriend_text);
        discoverText = (TextView) findViewById(R.id.discover_text);
        mineText = (TextView) findViewById(R.id.mine_text);
        mUnread = (TextView) findViewById(R.id.unread);
        ivRecordSlash.setOnClickListener(this);
        slashLayout.setOnClickListener(this);
        sfriendLayout.setOnClickListener(this);
        record_layout.setOnClickListener(this);
        discoverLayout.setOnClickListener(this);
        mineLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slash_layout:
                // 当点击了消息tab时，选中第1个tab
                setTabSelection(0);
                break;
            case R.id.sfriend_layout:
                // 当点击了联系人tab时，选中第2个tab
                if (SlashHelper.userManager().getUserinfo() != null) {
                    setTabSelection(1);
                } else {
                    IntentUtil.start_activity(MainActivity.this, LoginActivity.class);
                }

                break;
            case R.id.record_image:
            case R.id.record_layout:
                if (SlashHelper.userManager().getUserinfo() != null) {
                    if (null == mSlashMenuWindow) {
                        mSlashMenuWindow = new SlashMenuWindow(this);
                        mSlashMenuWindow.init();
                    }
                    mSlashMenuWindow.showMenuWindow(v);
                } else {
                    IntentUtil.start_activity(MainActivity.this, LoginActivity.class);
                }

                break;
            case R.id.discover_layout:
                // 当点击了设置tab时，选中第4个tab

                if (SlashHelper.userManager().getUserinfo() != null) {
                    setTabSelection(3);
                } else {
                    IntentUtil.start_activity(MainActivity.this, LoginActivity.class);
                }

                break;
            case R.id.mine_layout:
                // 当点击了设置tab时，选中第5个tab
                if (SlashHelper.userManager().getUserinfo() != null) {
                    setTabSelection(4);
                } else {
                    IntentUtil.start_activity(MainActivity.this, LoginActivity.class);
                }


                break;

            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     * @param index 每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
     */
    private void setTabSelection(int index) {
        // 每次选中之前先清除掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
//        login();    //登录IM聊 进入界面登录防止接受消息延迟
        switch (index) {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                slashImage.setImageResource(R.mipmap.shouye_icon);
                slashText.setTextColor(getResources().getColor(R.color.sel_color));
                if (slashFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    slashFragment = new HomeFragment();
                    transaction.add(R.id.content, slashFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(slashFragment);
                }
                transaction.commitAllowingStateLoss();
                break;
            case 1:
                checkPermission();

                sfriendImage.setImageResource(R.mipmap.xiaoxi2_icon);
                sfriendText.setTextColor(getResources().getColor(R.color.sel_color));

                break;
//            case 2:
//                interactionImage.setImageResource(R.mipmap.interaction_btn_sel);
//                interactionText.setTextColor(getResources().getColor(R.color.bg_head));
//                transaction.add(R.id.content, fragment);
//                break;
            case 3:
                discoverImage.setImageResource(R.mipmap.gangyou_btn_sel);
                discoverText.setTextColor(getResources().getColor(R.color.sel_color));
                if (myFollowFragment == null) {
                    myFollowFragment = new MyFollowFragment();
                    transaction.add(R.id.content, myFollowFragment);
                } else {
                    transaction.show(myFollowFragment);
                }
                transaction.commitAllowingStateLoss();
                break;

            default:
                mineImage.setImageResource(R.mipmap.wode2_icon);
                mineText.setTextColor(getResources().getColor(R.color.sel_color));
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                transaction.commitAllowingStateLoss();

                break;

        }
        //transaction.commitAllowingStateLoss();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        slashImage.setImageResource(R.mipmap.shouye2_icon);
        slashText.setTextColor(getResources().getColor(R.color.normal_tab));
        sfriendImage.setImageResource(R.mipmap.xiaoxi_icon);
        sfriendText.setTextColor(getResources().getColor(R.color.normal_tab));
//        interactionImage.setImageResource(R.mipmap.interaction_btn_nor);
//        interactionText.setTextColor(Color.parseColor("#98999b"));
        discoverImage.setImageResource(R.mipmap.gangyou_btn_nor);
        discoverText.setTextColor(getResources().getColor(R.color.normal_tab));
        mineImage.setImageResource(R.mipmap.wode_icon);
        mineText.setTextColor(getResources().getColor(R.color.normal_tab));
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (slashFragment != null) {
            transaction.hide(slashFragment);
        }
        if (conversationFragment != null) {
            transaction.hide(conversationFragment);
        }
        if (myFollowFragment != null) {
            transaction.hide(myFollowFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }

    }

    /**
     * 自定义会话示例展示系统通知的示例
     */
    public static final String SYSTEM_SYSMESSAGE = "sysMessage";

    private void initCustomConversation(String message, int unReadCounr) {
        CustomConversationHelper.addCustomConversation(SYSTEM_SYSMESSAGE, message, unReadCounr);
    }


    //设置连接状态的监听
    private void addConnectionListener() {
        mConnectionListener = new IYWConnectionListener() {
            @Override
            public void onDisconnect(int code, String info) {
                if (code == YWLoginCode.LOGON_FAIL_KICKOFF) {
                    ToastUtil.showMessage("请重新登录");
                }
                setTabSelection(0);
                SlashHelper.userManager().saveUserinfo(null);
                LoginSampleHelper.getInstance().setAutoLoginState(YWLoginState.idle);
                Intent intent = new Intent(AppApplication.getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AppApplication.getContext().startActivity(intent);
            }

            @Override
            public void onReConnecting() {

            }

            @Override
            public void onReConnected() {

            }
        };
        mIMKit.getIMCore().addConnectionListener(mConnectionListener);
    }


    private void setMessageLifeCycleListener() {
        mMessageLifeCycleListener = new IYWMessageLifeCycleListener() {
            /**
             * 发送消息前回调
             * @param conversation 当前消息所在会话
             * @param message      当前将要发送的消息
             * @return 需要发送的消息，若为null，则表示不发送消息
             */
            @Override
            public YWMessage onMessageLifeBeforeSend(YWConversation conversation, YWMessage message) {
                //todo 以下代码仅仅是示例，开发者无需按照以下方式设置，应该根据自己的需求对消息进行修改
                String cvsType = "单聊";
                if (conversation.getConversationType() == YWConversationType.Tribe) {
                    cvsType = "群聊：";
                }
                String msgType = "文本消息";
                if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_IMAGE) {
                    msgType = "图片消息";
                } else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_GEO) {
                    msgType = "地理位置消息";
                } else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_AUDIO) {
                    msgType = "语音消息";
                } else if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_P2P_CUS || message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TRIBE_CUS) {
                    msgType = "自定义消息";
                }

                //TODO 设置APNS Push，如果开发者需要对APNS Push进行定制可以调用message.setPushInfo(YWPushInfo)方法进行设置，如果不需要APNS Push定制则不需要调用message.setPushInfo(YWPushInfo)方法
                //TODO Demo默认发送消息不需要APNS Push定制,所以注释掉以下两行代码
//                YWPushInfo pushInfo = new YWPushInfo(1, cvsType + msgType, "dingdong", "我是自定义数据");
//                message.setPushInfo(pushInfo);

                //根据消息类型对消息进行修改，切记这里只是示例，具体怎样对消息进行修改开发者可以根据自己的需求进行处理
                if (message.getSubType() == YWMessage.SUB_MSG_TYPE.IM_TEXT) {
                    String content = message.getContent();
                    if (content.equals("55")) {
                        message.setContent("我修改了消息内容, 原始内容：55");
                        return message;
                    } else if (content.equals("66")) {
                        YWMessage newMsg = YWMessageChannel.createTextMessage("我创建了一条新消息, 原始消息内容：66");
                        return newMsg;
                    } else if (content.equals("77")) {
                        Notification.showToastMsg(MainActivity.this, "不发送该消息，消息内容为：77");
                        return null;
                    }
                }
                return message;
            }

            /**
             * 发送消息结束后回调
             * @param message   当前发送的消息
             * @param sendState 消息发送状态，具体状态请参考{@link com.alibaba.mobileim.conversation.YWMessageType.SendState}
             */
            @Override
            public void onMessageLifeFinishSend(YWMessage message, YWMessageType.SendState sendState) {
//                Notification.showToastMsg(FragmentTabs.this, sendState.toString());
            }
        };
        mConversationService.setMessageLifeCycleListener(mMessageLifeCycleListener);
    }

    private void initConversationServiceAndListener() {
        mConversationUnreadChangeListener = new IYWConversationUnreadChangeListener() {

            //当未读数发生变化时会回调该方法，开发者可以在该方法中更新未读数
            @Override
            public void onUnreadChange() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LoginSampleHelper loginHelper = LoginSampleHelper.getInstance();
                        final YWIMKit imKit = loginHelper.getIMKit();
                        mConversationService = imKit.getConversationService();
                        //获取当前登录用户的所有未读数
                        int unReadCount = mConversationService.getAllUnreadCount();
                        //设置桌面角标的未读数
                        mIMKit.setShortcutBadger(unReadCount);
                        if (unReadCount > 0) {
                            mUnread.setVisibility(View.VISIBLE);
                            if (unReadCount < 100) {
                                mUnread.setText(unReadCount + "");
                            } else {
                                mUnread.setText("99+");
                            }
                        } else {
                            mUnread.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        };
        mConversationService.addTotalUnreadChangeListener(mConversationUnreadChangeListener);

//            //新消息通知的回调
//        IYWPushListener msgPushListener = new IYWPushListener() {
//            @Override
//            public void onPushMessage(IYWContact iywContact, YWMessage ywMessage) {
//                ywMessage.getContent();
//
//            }
//
//            @Override
//            public void onPushMessage(YWTribe ywTribe, YWMessage ywMessage) {
//
//            }
//        };
//            IYWConversationService conversationService = mIMKit.getConversationService();
////如果之前add过，请清除
//            conversationService.removePushListener(msgPushListener);
////增加新消息到达的通知
//            conversationService.addPushListener(msgPushListener);
//
    }

    UserInfoOwnerRequest userInfoOwnerRequest;

    //获取用户自身的资料（包含关注数/粉丝数）
    private void get_user_info_owner_request() {
        if (userInfoOwnerRequest != null) {
            userInfoOwnerRequest.cancel();
        }

        UserInfoOwnerRequest.Input input = new UserInfoOwnerRequest.Input();
        if (SlashHelper.userManager().getUserinfo() != null) {
            input.userId = SlashHelper.userManager().getUserId() + "";
            input.convertJosn();
        }

        userInfoOwnerRequest = new UserInfoOwnerRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(getClass().getName(), error.getMessage());
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserLogin) response).status == 1) {

                    if (((APIM_UserLogin) response).userInfo != null) {
                        ((APIM_UserLogin) response).userInfo.setUserId(SlashHelper.userManager().getUserId());
                        ((APIM_UserLogin) response).userInfo.setPassword(SlashHelper.userManager().getUserinfo().getPassword());
                        UserManager.instance().saveUserinfo(((APIM_UserLogin) response).userInfo);
                        SlashHelper.setSettingString(((APIM_UserLogin) response).userInfo.getPhoneNum(), ((APIM_UserLogin) response).userInfo.getHead());
                    }
                } else {
                }
            }
        });
        sendJsonRequest(userInfoOwnerRequest);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 800) {//如果两次按键时间间隔大于800毫秒，则不退出
                Toast.makeText(MainActivity.this, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = secondTime;//更新firstTime
                return true;
            } else {
                System.exit(0);//否则退出程序
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_OVERLAY_PERMISSION) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission();

            } else {
                if (conversationFragment == null) {
                    conversationFragment = mIMKit.getConversationFragment();//= new MainFriendFragment() new SfriendFragment();mIMKit.getConversationFragment()   conversationFragment
                    transaction.add(R.id.content, conversationFragment);
                } else {
                    transaction.show(conversationFragment);
                }

                transaction.commitAllowingStateLoss();
            }

        }


    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_OVERLAY_PERMISSION);
            } else {
                if (conversationFragment == null) {
                    conversationFragment = mIMKit.getConversationFragment();//= new MainFriendFragment() new SfriendFragment();mIMKit.getConversationFragment()   conversationFragment
                    transaction.add(R.id.content, conversationFragment);
                } else {
                    transaction.show(conversationFragment);
                }

                transaction.commitAllowingStateLoss();
            }
        } else {
            if (conversationFragment == null) {
                conversationFragment = mIMKit.getConversationFragment();//= new MainFriendFragment() new SfriendFragment();mIMKit.getConversationFragment()   conversationFragment
                transaction.add(R.id.content, conversationFragment);
            } else {
                transaction.show(conversationFragment);
            }

            transaction.commitAllowingStateLoss();
        }

    }

}
