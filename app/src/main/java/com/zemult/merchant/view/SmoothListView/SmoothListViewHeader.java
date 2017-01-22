package com.zemult.merchant.view.SmoothListView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;
import com.wang.avi.AVLoadingIndicatorView;


public class SmoothListViewHeader extends LinearLayout {
	private LinearLayout mContainer;
	private ImageView ivBeePull, ivBeeDown;
	private int mState = STATE_NORMAL;


	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_REFRESHING = 2;

	private AnimationDrawable pullAnimationDrawable, downAnimationDrawable;

	public SmoothListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public SmoothListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(
				LayoutParams.FILL_PARENT, 0);
		mContainer = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.smoothlistview_header, null);
		addView(mContainer, lp);
		setGravity(Gravity.BOTTOM);

		ivBeePull = (ImageView)findViewById(R.id.iv_bee_pull);
		ivBeeDown = (ImageView)findViewById(R.id.iv_bee_down);


		ivBeePull.setImageResource(R.drawable.pull_anim);
		ivBeeDown.setImageResource(R.drawable.down_anim);
		pullAnimationDrawable= (AnimationDrawable) ivBeePull.getDrawable();
		downAnimationDrawable= (AnimationDrawable) ivBeeDown.getDrawable();
	}

	public void setState(int state) {
		if (state == mState) return ;
		
		if (state == STATE_REFRESHING) {	// 显示进度
			ivBeePull.setVisibility(View.GONE);
			pullAnimationDrawable.stop();
			ivBeeDown.setVisibility(View.VISIBLE);
			downAnimationDrawable.start();
		} else {
			ivBeePull.setVisibility(View.VISIBLE);
			pullAnimationDrawable.start();
			ivBeeDown.setVisibility(View.GONE);
			downAnimationDrawable.stop();
		}
		
//		switch(state){
//		case STATE_NORMAL:
//			if (mState == STATE_READY) {
//				mArrowImageView.startAnimation(mRotateDownAnim);
//			}
//			if (mState == STATE_REFRESHING) {
//				mArrowImageView.clearAnimation();
//			}
//			mHintTextView.setText(R.string.smoothlistview_header_hint_normal);
//			break;
//		case STATE_READY:
//			if (mState != STATE_READY) {
//				mArrowImageView.clearAnimation();
//				mArrowImageView.startAnimation(mRotateUpAnim);
//				mHintTextView.setText(R.string.smoothlistview_header_hint_ready);
//			}
//			break;
//		case STATE_REFRESHING:
//			mHintTextView.setText(R.string.smoothlistview_header_hint_loading);
//			break;
//			default:
//		}
//
		mState = state;
	}
	
	public void setVisiableHeight(int height) {
		if (height < 0)
			height = 0;
		LayoutParams lp = (LayoutParams) mContainer
				.getLayoutParams();
		lp.height = height;
		mContainer.setLayoutParams(lp);
	}

	public int getVisiableHeight() {
		return mContainer.getHeight();
	}

}
