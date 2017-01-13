package com.zemult.merchant.view;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.MainActivity;
import com.zemult.merchant.activity.mine.RoleDetailActivity;
import com.zemult.merchant.activity.mine.RoleSetActivity;
import com.zemult.merchant.activity.search.LabelHomeActivity;
import com.zemult.merchant.activity.slash.DiscoverRecommendActivity;
import com.zemult.merchant.adapter.slash.MySlashAdapter;
import com.zemult.merchant.aip.mine.UserIndustryListRequest;
import com.zemult.merchant.model.M_UserRole;
import com.zemult.merchant.model.apimodel.APIM_UserIndustryList;
import com.zemult.merchant.util.DateTimeUtil;
import com.zemult.merchant.util.DensityUtil;
import com.zemult.merchant.util.FastBlur;
import com.zemult.merchant.util.IntentUtil;
import com.zemult.merchant.util.KickBackAnimator;
import com.zemult.merchant.util.SlashHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.trinea.android.common.util.ToastUtils;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import zema.volley.network.ResponseListener;

public class SlashMenuWindow extends PopupWindow {

    @Bind(R.id.iv_close)
    ImageView ivClose;
    @Bind(R.id.rl_info)
    RelativeLayout rlInfo;
    @Bind(R.id.ll_close)
    LinearLayout llClose;
    @Bind(R.id.tv_new_play)
    TextView tvNewPlay;
    @Bind(R.id.tv_new_task)
    TextView tvNewTask;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_week)
    TextView tvWeek;
    @Bind(R.id.tv_year)
    TextView tvYearMonth;
    @Bind(R.id.tv_motto)
    TextView tvMotto;
    @Bind(R.id.rv_roles)
    RecyclerView rvRoles;
    int[] animViews = {R.id.tv_new_play, R.id.tv_new_recommend, R.id.tv_new_task};
    private String TAG = SlashMenuWindow.class.getSimpleName();
    private int mWidth;
    private int mHeight;
    private int statusBarHeight;
    private Bitmap mBitmap = null;
    private Bitmap overlay = null;
    private Handler mHandler = new Handler();
    private MainActivity mContext;
    MySlashAdapter.OnItemClickListener onItemClick = new MySlashAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int position, M_UserRole entity) {
            if (entity != null) {
                IntentUtil.intStart_activity(mContext, RoleDetailActivity.class, new Pair<String, Integer>(RoleDetailActivity.INTENT_INDUSTRYID, entity.industryId));
            }
        }
    };
    private List<M_UserRole> slashList = new ArrayList<>();
    private MySlashAdapter mMySlashAdapter;
    private UserIndustryListRequest userIndustryListRequest;

    public SlashMenuWindow(MainActivity context) {
        mContext = context;
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = mContext.getWindowManager().getDefaultDisplay().getHeight();

        setWidth(mWidth);
        setHeight(mHeight);
        EventBus.getDefault().register(this);
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 8;
        float radius = 30;//模糊度
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor), (int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        Log.i(TAG, "blur time is:" + (System.currentTimeMillis() - startMs));
        return overlay;
    }

    public void showMenuWindow(View anchor) {
        final RelativeLayout view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.activity_new_slash_menu, null);
        ButterKnife.bind(this, view);
        setContentView(view);

        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
        initAnimation(view);
    }

    private void initData() {
        getMerchantData();
    }

    private void initView() {

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRoles.setLayoutManager(linearLayoutManager);
        mMySlashAdapter = new MySlashAdapter(mContext, slashList);
        rvRoles.setAdapter(mMySlashAdapter);

        tvDate.setText(DateTimeUtil.getCurrentDay());
        tvWeek.setText(DateTimeUtil.getChinaDayOfWeek(new Date()));
        tvYearMonth.setText(String.format("%s / %s", DateTimeUtil.getCurrentYear(), DateTimeUtil.getCurrentMonth()));
        tvMotto.setText("体验多重角色|实现多重职业|成就精彩人生");
    }

    private void initListener(final View view) {
        mMySlashAdapter.setOnItemClickListener(onItemClick);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SlashMenuWindow.this.isShowing()) {
                    dismiss();
                }
            }
        });
    }

    private void initAnimation(final View v) {
        for (int i = 0; i < animViews.length; i++) {
            final View child = v.findViewById(animViews[i]);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", DensityUtil.getWindowHeight(mContext), 0);
                    fadeAnim.setDuration(800);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(250);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            initData();
                            initView();
                            initListener(v);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            }, i * 50);
        }

    }

    /**
     * 获取场景数据
     */
    private void getMerchantData() {
        getUserMerchantList();
    }

    /**
     * 获取我的场景列表
     */
    private void getUserMerchantList() {
        if (userIndustryListRequest != null) {
            userIndustryListRequest.cancel();
        }
        UserIndustryListRequest.Input input = new UserIndustryListRequest.Input();
        input.userId = SlashHelper.userManager().getUserId();
        input.page = 1;
        input.rows = 1000;

        input.convertJosn();

        userIndustryListRequest = new UserIndustryListRequest(input, new ResponseListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }

            @Override
            public void onResponse(Object response) {
                if (((APIM_UserIndustryList) response).status == 1) {
                    setSlashAdapter(((APIM_UserIndustryList) response).industryList);
                } else {
                    ToastUtils.show(mContext, ((APIM_UserIndustryList) response).info);
                }
            }
        });
        mContext.sendJsonRequest(userIndustryListRequest);
    }

    private void setSlashAdapter(List<M_UserRole> list) {
        if (list != null && !list.isEmpty()) {
            mMySlashAdapter.setData(list);
        }
    }

    @OnClick({R.id.tv_new_play, R.id.tv_new_recommend, R.id.tv_new_task})
    public void viewClick(View v) {
        switch (v.getId()) {
            case R.id.tv_new_play:
                if (SlashHelper.userManager().getUserinfo() != null) {
                }
                break;
            case R.id.tv_new_recommend:
                if (SlashHelper.userManager().getUserinfo() != null) {
                    IntentUtil.start_activity(mContext, DiscoverRecommendActivity.class);
                }
                break;
            case R.id.tv_new_task:
                if (SlashHelper.userManager().getUserinfo() != null) {
                    IntentUtil.start_activity(mContext, LabelHomeActivity.class);
                }
                break;
        }
    }


    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }

        EventBus.getDefault().unregister(this);
    }


    /**
     * =================================================处理刷新请求===========================================================================
     */
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void refreshEvent(String s) {
        if (RoleSetActivity.Call_SLASHMENUWINDOW_REFRESH.equals(s))
            getMerchantData();
    }

}
