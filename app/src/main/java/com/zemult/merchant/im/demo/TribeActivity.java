package com.zemult.merchant.im.demo;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.fundamental.widget.refreshlist.YWPullToRefreshBase;
import com.alibaba.mobileim.fundamental.widget.refreshlist.YWPullToRefreshListView;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.gingko.model.tribe.YWTribeType;
import com.alibaba.mobileim.tribe.IYWTribeService;
import com.alibaba.mobileim.utility.ResourceLoader;
import com.zemult.merchant.R;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.app.BaseActivity;
import com.zemult.merchant.im.imcore.TribeSampleHelper;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.im.sample.TribeAdapterSample;
import com.zemult.merchant.im.tribe.EditTribeInfoActivity;
import com.zemult.merchant.im.tribe.SearchTribeActivity;
import com.zemult.merchant.im.tribe.TribeAndRoomList;
import com.zemult.merchant.im.tribe.TribeConstants;
import com.zemult.merchant.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TribeActivity extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    protected static final long POST_DELAYED_TIME = 300;
    private static final String TAG = "TribeFragment";
    private final Handler handler = new Handler();

    public IYWTribeService getTribeService() {
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        mTribeService = mIMKit.getTribeService();
        return mTribeService;
    }

    private IYWTribeService mTribeService;
    private ListView mMessageListView; // 消息列表视图
    private TribeAdapterSample adapter;
    private int max_visible_item_count = 0; // 当前页面列表最多显示个数
    private TribeAndRoomList mTribeAndRoomList;
    private List<YWTribe> mList;
    private List<YWTribe> mTribeList;
    private List<YWTribe> mRoomsList;

    private PopupWindow mPopupBackground;
    private PopupWindow mPopupWindow;

    private YWIMKit mIMKit;
    private String mUserId;
    private View mProgress;
    private YWPullToRefreshListView mPullToRefreshListView;
    private Runnable cancelRefresh = new Runnable() {
        @Override
        public void run() {
            if (mPullToRefreshListView != null) {
                mPullToRefreshListView.onRefreshComplete(false, false,
                        R.string.aliwx_sync_failed);
            }
        }
    };

    public TribeActivity() {
        // Required empty public constructor
    }


    @Override
    public void setContentView() {
     setContentView(R.layout.demo_fragment_tribe);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = findViewById(R.id.progress);
        mIMKit = LoginSampleHelper.getInstance().getIMKit();
        mUserId = mIMKit.getIMCore().getLoginUserId();
        if (TextUtils.isEmpty(mUserId)) {
            YWLog.i(TAG, "user not login");
        }
        mTribeService = mIMKit.getTribeService();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        initTribeListView();
        Intent intent =getIntent();
        if (intent != null) {
            if (!TextUtils.isEmpty(intent.getStringExtra(TribeConstants.TRIBE_OP))) {
                mList = mTribeService.getAllTribes();
                updateTribeList();
            }
        }
    }


    @Override
    public void init() {
        getWindow().setWindowAnimations(0);
        initTitle();
        mList = new ArrayList<YWTribe>();
        mTribeList = new ArrayList<YWTribe>();
        mRoomsList = new ArrayList<YWTribe>();
        mTribeAndRoomList = new TribeAndRoomList(mTribeList, mRoomsList);
        adapter = new TribeAdapterSample(TribeActivity.this, mTribeAndRoomList);
        mPullToRefreshListView = (YWPullToRefreshListView)
                findViewById(
                        R.id.message_list);
        mMessageListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setMode(YWPullToRefreshBase.Mode.PULL_DOWN_TO_REFRESH);
        mPullToRefreshListView.setShowIndicator(false);
        mPullToRefreshListView.setDisableScrollingWhileRefreshing(false);
        mPullToRefreshListView.setRefreshingLabel("同步群组");
        mPullToRefreshListView.setReleaseLabel("松开同步群组");
//        mPullToRefreshListView.resetHeadLayout();
        mPullToRefreshListView.setDisableRefresh(false);
        mPullToRefreshListView.setOnRefreshListener(new YWPullToRefreshBase.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeCallbacks(cancelRefresh);
                        IYWTribeService tribeService = TribeSampleHelper.getTribeService();
                        if (tribeService != null) {
                            tribeService.getAllTribesFromServer(new IWxCallback() {
                                @Override
                                public void onSuccess(Object... arg0) {
                                    // 返回值为列表
                                    mList.clear();
                                    mList.addAll((ArrayList<YWTribe>) arg0[0]);
                                    if (mList.size() > 0) {
                                        mTribeList.clear();
                                        mRoomsList.clear();
                                        for (YWTribe tribe : mList) {
                                            if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE) {
                                                mTribeList.add(tribe);
                                            } else {
                                                mRoomsList.add(tribe);
                                            }
                                        }
                                    }
                                    mPullToRefreshListView.onRefreshComplete(false, true,
                                            R.string.aliwx_sync_success);
                                    refreshAdapter();
                                }

                                @Override
                                public void onError(int code, String info) {
                                    mPullToRefreshListView.onRefreshComplete(false, false,
                                            R.string.aliwx_sync_failed);
                                }

                                @Override
                                public void onProgress(int progress) {

                                }
                            });
                        }
                    }
                }, POST_DELAYED_TIME);
            }
        });
    }

    private void initTitle() {
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        TextView titleView = (TextView) findViewById(R.id.title_self_title);
        TextView leftButton = (TextView) findViewById(R.id.left_button);
        leftButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.back_btn, 0, 0, 0);
        leftButton.setTextColor(Color.WHITE);
        leftButton.setText("");
//        leftButton.setText("搜索加入群");
//        leftButton.setTextColor(getResources().getColor(R.color.aliwx_white));
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TribeActivity.this, SearchTribeActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        titleBar.setBackgroundColor(Color.WHITE);
        titleView.setTextColor(Color.parseColor("#FFA726"));
        titleView.setText("群组");
        titleBar.setVisibility(View.VISIBLE);

        TextView rightButton = (TextView) findViewById(ResourceLoader.getIdByName(TribeActivity.this, "id", "right_button"));
        rightButton.setText("");
        rightButton.setBackgroundResource(R.mipmap.gengduo_black_icon);
        rightButton.setWidth(DensityUtil.dip2px(this, 50));
        rightButton.setHeight(DensityUtil.dip2px(this, 50));
//        rightButton.setTextColor(getResources().getColor(R.color.aliwx_white));
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    /**
     * 弹出要创建的群类型选项
     *
     * @param v
     */
    private void showPopupMenu(View v) {
        final View bgView = View.inflate(AppApplication.getContext(), R.layout.demo_popup_window_bg, null);
        bgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });
        if (mPopupBackground == null) {
            mPopupBackground = new PopupWindow(bgView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        mPopupBackground.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        View view = View.inflate(AppApplication.getContext(), R.layout.demo_popup_menu, null);
        //创建群组
        TextView tribe = (TextView) view.findViewById(R.id.create_tribe);
        tribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
                Intent intent = new Intent(TribeActivity.this, EditTribeInfoActivity.class);
                intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_CREATE);
                intent.putExtra(TribeConstants.TRIBE_TYPE, YWTribeType.CHATTING_TRIBE.toString());
                startActivityForResult(intent, 0);
            }
        });

        //创建讨论组
        TextView room = (TextView) view.findViewById(R.id.create_room);
        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
//                Intent intent = new Intent(TribeActivity.this, EditTribeInfoActivity.class);
//                intent.putExtra(TribeConstants.TRIBE_OP, TribeConstants.TRIBE_CREATE);
//                intent.putExtra(TribeConstants.TRIBE_TYPE, YWTribeType.CHATTING_GROUP.toString());
//                startActivityForResult(intent, 0);
                Intent intent = new Intent(TribeActivity.this, SearchTribeActivity.class);
                startActivity(intent);

            }
        });

        TextView cancel = (TextView) view.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopupWindow();
            }
        });


        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        mPopupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    private void hidePopupWindow() {
        if (mPopupBackground != null) {
            mPopupBackground.dismiss();
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 初始化群列表行为
     */
    private void initTribeListView() {
        if (mMessageListView != null) {
            mMessageListView.setAdapter(adapter);
            mMessageListView.setOnItemClickListener(this);
            mMessageListView.setOnScrollListener(this);
        }

        getAllTribesFromServer();
    }

    private void getAllTribesFromServer() {
        getTribeService().getAllTribesFromServer(new IWxCallback() {
            @Override
            public void onSuccess(Object... arg0) {
                // 返回值为列表
                mList.clear();
                mList.addAll((ArrayList<YWTribe>) arg0[0]);
                updateTribeList();
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError(int code, String info) {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void updateTribeList() {
        mTribeList.clear();
        mRoomsList.clear();
        if (mList.size() > 0) {
            for (YWTribe tribe : mList) {
                if (tribe.getTribeType() == YWTribeType.CHATTING_TRIBE) {
                    mTribeList.add(tribe);
                } else {
                    mRoomsList.add(tribe);
                }
            }
        }
        refreshAdapter();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position1,
                            long id) {
        final int position = position1 - mMessageListView.getHeaderViewsCount();
        if (position >= 0 && position < mTribeAndRoomList.size()) {

            YWTribe tribe = (YWTribe) mTribeAndRoomList.getItem(position);
            YWIMKit imKit = LoginSampleHelper.getInstance().getIMKit();
            //参数为群ID号
            Intent intent = imKit.getTribeChattingActivityIntent(tribe.getTribeId());
            startActivity(intent);
        }
    }

    /**
     * 刷新当前列表
     */
    private void refreshAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChangedWithAsyncLoad();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE && adapter != null) {
            adapter.loadAsyncTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        max_visible_item_count = visibleItemCount > max_visible_item_count ? visibleItemCount
                : max_visible_item_count;
        if (adapter != null) {
            adapter.setMax_visible_item_count(max_visible_item_count);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mList = mTribeService.getAllTribes();
        updateTribeList();
    }
}
