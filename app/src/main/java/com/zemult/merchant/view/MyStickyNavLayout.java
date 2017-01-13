package com.zemult.merchant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;
import com.zemult.merchant.R;

import in.srain.cube.views.GridViewWithHeaderAndFooter;

/**
 * 吸顶效果
 * @author djy
 * @time 2016/7/25 8:59
 */
public class MyStickyNavLayout extends LinearLayout {
    private static final String TAG = MyStickyNavLayout.class.getSimpleName();
    private View mTop;
    private View mNav;
    private ViewPager mViewPager;
    private int mTopViewHeight;
    private ViewGroup mInnerScrollView;
    private boolean isTopHidden;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private float mLastY;
    private boolean mDragging;
    private boolean isStickNav;
    private boolean isInControl;
    private int stickOffset;
    private int mViewPagerMaxHeight;
    private int mTopViewMaxHeight;
    private boolean isSticky;
    private onStickStateChangeListener listener;

    public MyStickyNavLayout(Context context) {
        this(context, (AttributeSet)null);
    }

    public MyStickyNavLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyStickyNavLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isTopHidden = false;
        this.isInControl = false;
        this.setOrientation(VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickNavLayout);
        this.isStickNav = a.getBoolean(R.styleable.StickNavLayout_isStickNav, false);
        this.stickOffset = a.getDimensionPixelSize(R.styleable.StickNavLayout_stickOffset, 0);
        a.recycle();
        this.mScroller = new OverScroller(context);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
    }

    public void setIsStickNav(boolean isStickNav) {
        this.isStickNav = isStickNav;
    }

    public void setStickNavAndScrollToNav() {
        this.isStickNav = true;
        this.scrollTo(0, this.mTopViewHeight);
    }

    public void setTopViewHeight(int height) {
        this.mTopViewHeight = height;
        if(this.isStickNav) {
            this.scrollTo(0, this.mTopViewHeight);
        }

    }

    public void setTopViewHeight(int height, int offset) {
        this.mTopViewHeight = height;
        if(this.isStickNav) {
            this.scrollTo(0, this.mTopViewHeight - offset);
        }
    }

    // 将滚动区域滑出，直接吸顶
    public void scrollToTop(){
        this.scrollTo(0, this.mTopViewHeight);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mTop = this.findViewById(R.id.id_stickynavlayout_topview);
        this.mNav = this.findViewById(R.id.id_stickynavlayout_indicator);
        View view = this.findViewById(R.id.id_stickynavlayout_viewpager);
        if(!(view instanceof ViewPager)) {
            throw new RuntimeException("id_MyStickyNavLayout_viewpager show used by ViewPager !");
        } else {
            if(this.mTop instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)this.mTop;
                if(viewGroup.getChildCount() >= 2) {
                    throw new RuntimeException("if the TopView(android:id=\"R.id.id_MyStickyNavLayout_topview\") is a ViewGroup(ScrollView,LinearLayout,FrameLayout, ....) ,the children count should be one  !");
                }
            }

            this.mViewPager = (ViewPager)view;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LayoutParams params = (LayoutParams) this.mViewPager.getLayoutParams();
        int height = this.getMeasuredHeight() - this.mNav.getMeasuredHeight();
        this.mViewPagerMaxHeight = height >= this.mViewPagerMaxHeight?height:this.mViewPagerMaxHeight;
        params.height = height;
        this.mViewPager.setLayoutParams(params);
        int topHeight = this.mTop.getMeasuredHeight();
        LayoutParams topParams = (LayoutParams) this.mTop.getLayoutParams();
        this.mTopViewMaxHeight = topHeight >= this.mTopViewMaxHeight?topHeight:this.mTopViewMaxHeight;
        topParams.height = topHeight;
        this.mTop.setLayoutParams(topParams);
        this.mTopViewHeight = topParams.height - stickOffset;
        Log.d("MyStickyNavLayout", "onMeasure--mTopViewHeight:" + this.mTopViewHeight);
    }

    public void updateTopViews() {
        if(!this.isTopHidden) {
            final LayoutParams params = (LayoutParams) this.mTop.getLayoutParams();
            this.mTop.post(new Runnable() {
                public void run() {
                    if(MyStickyNavLayout.this.mTop instanceof ViewGroup) {
                        ViewGroup viewGroup = (ViewGroup)MyStickyNavLayout.this.mTop;
                        int height = viewGroup.getChildAt(0).getHeight();
                        MyStickyNavLayout.this.mTopViewHeight = height - MyStickyNavLayout.this.stickOffset;
                        params.height = height;
                        MyStickyNavLayout.this.mTop.setLayoutParams(params);
                        params.height = -2;
                    } else {
                        MyStickyNavLayout.this.mTopViewHeight = MyStickyNavLayout.this.mTop.getMeasuredHeight() - MyStickyNavLayout.this.stickOffset;
                    }

                }
            });
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        final LayoutParams params = (LayoutParams) this.mTop.getLayoutParams();
        Log.d("MyStickyNavLayout", "onSizeChanged-mTopViewHeight:" + this.mTopViewHeight);
        this.mTop.post(new Runnable() {
            public void run() {
                if(MyStickyNavLayout.this.mTop instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup)MyStickyNavLayout.this.mTop;
                    int height = viewGroup.getChildAt(0).getHeight();
                    MyStickyNavLayout.this.mTopViewHeight = height - MyStickyNavLayout.this.stickOffset;
                    params.height = height;
                    MyStickyNavLayout.this.mTop.setLayoutParams(params);
                    MyStickyNavLayout.this.mTop.requestLayout();
                } else {
                    MyStickyNavLayout.this.mTopViewHeight = MyStickyNavLayout.this.mTop.getMeasuredHeight() - MyStickyNavLayout.this.stickOffset;
                }

                Log.d("MyStickyNavLayout", "mTopViewHeight:" + MyStickyNavLayout.this.mTopViewHeight);
                if(null != MyStickyNavLayout.this.mInnerScrollView) {
                    Log.d("MyStickyNavLayout", "mInnerScrollViewHeight:" + MyStickyNavLayout.this.mInnerScrollView.getMeasuredHeight());
                }

                if(MyStickyNavLayout.this.isStickNav) {
                    MyStickyNavLayout.this.scrollTo(0, MyStickyNavLayout.this.mTopViewHeight);
                }

            }
        });
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        switch(action) {
            case 0:
                this.mLastY = y;
                break;
            case 1:
            case 3:
                float distance4 = y - this.mLastY;
                if(this.isSticky && Math.abs(distance4) <= (float)this.mTouchSlop) {
                    this.isSticky = false;
                    return true;
                }

                this.isSticky = false;
                return super.dispatchTouchEvent(ev);
            case 2:
                float dy = y - this.mLastY;
                this.getCurrentScrollView();
                if(this.mInnerScrollView instanceof ScrollView) {
                    if(this.mInnerScrollView.getScrollY() == 0 && this.isTopHidden && dy > 0.0F && !this.isInControl) {
                        this.isInControl = true;
                        ev.setAction(3);
                        MotionEvent distance = MotionEvent.obtain(ev);
                        this.dispatchTouchEvent(ev);
                        distance.setAction(0);
                        this.isSticky = true;
                        return this.dispatchTouchEvent(distance);
                    }
                } else {
                    View ev2;
                    MotionEvent ev21;
                    if(this.mInnerScrollView instanceof ListView) {
                        ListView distance1 = (ListView)this.mInnerScrollView;
                        ev2 = distance1.getChildAt(distance1.getFirstVisiblePosition());
                        if(!this.isInControl && ev2 != null && ev2.getTop() == 0 && this.isTopHidden && dy > 0.0F) {
                            this.isInControl = true;
                            ev.setAction(3);
                            ev21 = MotionEvent.obtain(ev);
                            this.dispatchTouchEvent(ev);
                            ev21.setAction(0);
                            this.isSticky = true;
                            return this.dispatchTouchEvent(ev21);
                        }
                    }
                    else if(this.mInnerScrollView instanceof GridViewWithHeaderAndFooter) {
                        GridView distance2 = (GridView)this.mInnerScrollView;
                        ev2 = distance2.getChildAt(distance2.getFirstVisiblePosition());
                        if(!this.isInControl && ev2 != null && ev2.getTop() == 0 && this.isTopHidden && dy > 0.0F) {
                            this.isInControl = true;
                            ev.setAction(3);
                            ev21 = MotionEvent.obtain(ev);
                            this.dispatchTouchEvent(ev);
                            ev21.setAction(0);
                            this.isSticky = true;
                            return this.dispatchTouchEvent(ev21);
                        }
                    }
                    else if(this.mInnerScrollView instanceof RecyclerView) {
                        RecyclerView distance3 = (RecyclerView)this.mInnerScrollView;
                        if(!this.isInControl && ViewCompat.canScrollVertically(distance3, -1) && this.isTopHidden && dy > 0.0F) {
                            this.isInControl = true;
                            ev.setAction(3);
                            MotionEvent ev22 = MotionEvent.obtain(ev);
                            this.dispatchTouchEvent(ev);
                            ev22.setAction(0);
                            this.isSticky = true;
                            return this.dispatchTouchEvent(ev22);
                        }
                    }
                }
        }

        return super.dispatchTouchEvent(ev);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        float y = ev.getY();
        switch(action) {
            case 0:
                this.mLastY = y;
                break;
            case 1:
            case 3:
                this.mDragging = false;
                this.recycleVelocityTracker();
                break;
            case 2:
                float dy = y - this.mLastY;
                this.getCurrentScrollView();
                if(Math.abs(dy) > (float)this.mTouchSlop) {
                    this.mDragging = true;
                    if(this.mInnerScrollView instanceof ScrollView) {
                        if(!this.isTopHidden || this.mInnerScrollView.getScrollY() == 0 && this.isTopHidden && dy > 0.0F) {
                            this.initVelocityTrackerIfNotExists();
                            this.mVelocityTracker.addMovement(ev);
                            this.mLastY = y;
                            return true;
                        }
                    } else {
                        View c;
                        if(this.mInnerScrollView instanceof ListView) {
                            ListView rv2 = (ListView)this.mInnerScrollView;
                            c = rv2.getChildAt(rv2.getFirstVisiblePosition());
                            if(!this.isTopHidden || c != null && c.getTop() == 0 && this.isTopHidden && dy > 0.0F) {
                                this.initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(ev);
                                this.mLastY = y;
                                return true;
                            }

                            if(rv2.getAdapter() != null && rv2.getAdapter().getCount() == 0) {
                                this.initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(ev);
                                this.mLastY = y;
                                return true;
                            }
                        }
                        else if(!(this.mInnerScrollView instanceof GridViewWithHeaderAndFooter)) {
                            if(this.mInnerScrollView instanceof RecyclerView) {
                                RecyclerView rv1 = (RecyclerView)this.mInnerScrollView;
                                if(!this.isTopHidden || !ViewCompat.canScrollVertically(rv1, -1) && this.isTopHidden && dy > 0.0F) {
                                    this.initVelocityTrackerIfNotExists();
                                    this.mVelocityTracker.addMovement(ev);
                                    this.mLastY = y;
                                    return true;
                                }
                            }
                        }
                        else {
                            GridViewWithHeaderAndFooter rv = (GridViewWithHeaderAndFooter)this.mInnerScrollView;
                            c = rv.getChildAt(rv.getFirstVisiblePosition());
                            if(!this.isTopHidden || c != null && c.getTop() == 0 && this.isTopHidden && dy > 0.0F) {
                                this.initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(ev);
                                this.mLastY = y;
                                return true;
                            }

                            if(rv.getAdapter() != null && rv.getAdapter().getCount() == rv.getHeaderViewCount() + rv.getFooterViewCount()) {
                                this.initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(ev);
                                this.mLastY = y;
                                return true;
                            }
                        }
                    }
                }
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void getCurrentScrollView() {
        int currentItem = this.mViewPager.getCurrentItem();
        PagerAdapter a = this.mViewPager.getAdapter();
        Fragment item;
        View v;
        if(a instanceof FragmentPagerAdapter) {
            FragmentPagerAdapter fsAdapter = (FragmentPagerAdapter)a;
            item = fsAdapter.getItem(currentItem);
            v = item.getView();
            if(v != null) {
                this.mInnerScrollView = (ViewGroup)((ViewGroup)v.findViewById(R.id.id_stickynavlayout_innerscrollview));
            }
        } else {
            if(!(a instanceof FragmentStatePagerAdapter)) {
                throw new RuntimeException("mViewPager  should be  used  FragmentPagerAdapter or  FragmentStatePagerAdapter  !");
            }

            FragmentStatePagerAdapter fsAdapter1 = (FragmentStatePagerAdapter)a;
            item = fsAdapter1.getItem(currentItem);
            v = item.getView();
            if(v != null) {
                this.mInnerScrollView = (ViewGroup)((ViewGroup)v.findViewById(R.id.id_stickynavlayout_innerscrollview));
            }
        }

    }

    public boolean onTouchEvent(MotionEvent event) {
        this.initVelocityTrackerIfNotExists();
        this.mVelocityTracker.addMovement(event);
        int action = event.getAction();
        float y = event.getY();
        switch(action) {
            case 0: // ACTION_DOWN
                if(!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }

                this.mLastY = y;
                return true;
            case 1: // ACTION_UP
                this.mDragging = false;
                this.mVelocityTracker.computeCurrentVelocity(1000, (float)this.mMaximumVelocity);
                int velocityY = (int)this.mVelocityTracker.getYVelocity();
                if(Math.abs(velocityY) > this.mMinimumVelocity) {
                    this.fling(-velocityY);
                }

                this.recycleVelocityTracker();
                break;
            case 2: // ACTION_MOVE
                float dy = y - this.mLastY;
                if(!this.mDragging && Math.abs(dy) > (float)this.mTouchSlop) {
                    this.mDragging = true;
                }

                if(this.mDragging) {
                    this.scrollBy(0, (int)(-dy));
                    if(this.getScrollY() == this.mTopViewHeight && dy < 0.0F) {
                        event.setAction(0);
                        this.dispatchTouchEvent(event);
                        this.isInControl = false;
                        this.isSticky = true;
                    } else {
                        this.isSticky = false;
                    }
                }

                this.mLastY = y;
                break;
            case 3: // ACTION_CANCEL
                this.mDragging = false;
                this.recycleVelocityTracker();
                if(!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
        }

        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        this.mScroller.fling(0, this.getScrollY(), 0, velocityY, 0, 0, 0, this.mTopViewHeight);
        this.invalidate();
    }

    public void scrollTo(int x, int y) {
        if(y < 0) {
            y = 0;
        }

        if(y > this.mTopViewHeight) {
            y = this.mTopViewHeight;
        }

        if(y != this.getScrollY()) {
            super.scrollTo(x, y);
        }

        this.isTopHidden = this.getScrollY() == this.mTopViewHeight;
        if(this.listener != null) {
            this.listener.isStick(this.isTopHidden);
            this.listener.scrollPercent((float)this.getScrollY() / (float)this.mTopViewHeight);
        }

    }

    public void computeScroll() {
        if(this.mScroller.computeScrollOffset()) {
            this.scrollTo(0, this.mScroller.getCurrY());
            this.postInvalidate();
        }

    }

    private void initVelocityTrackerIfNotExists() {
        if(this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }

    }

    private void recycleVelocityTracker() {
        if(this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }

    }

    public void setOnStickStateChangeListener(onStickStateChangeListener listener) {
        this.listener = listener;
    }

    public interface onStickStateChangeListener {
        void isStick(boolean var1);

        void scrollPercent(float var1);
    }
}

