package com.zemult.merchant.view.SmoothListView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;


public class SmoothListViewFooter extends LinearLayout {
	public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;

	private Context mContext;

	private View mContentView;
	private TextView mHintView;

	private ImageView ivBeeDown;

	private AnimationDrawable downAnimationDrawable;
	
	public SmoothListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	
	public SmoothListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		mHintView.setVisibility(View.INVISIBLE);
		ivBeeDown.setVisibility(View.INVISIBLE);
		mHintView.setVisibility(View.INVISIBLE);
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.smoothlistview_footer_hint_ready);
		} else if (state == STATE_LOADING) {
			ivBeeDown.setVisibility(View.VISIBLE);
			downAnimationDrawable.start();
		} else {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText(R.string.smoothlistview_footer_hint_normal);
		}
	}
	
	public void setBottomMargin(int height) {
		if (height < 0) return ;
		LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	
	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		ivBeeDown.setVisibility(View.GONE);
		downAnimationDrawable.stop();
	}
	
	
	/**
	 * loading status 
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);

		ivBeeDown.setVisibility(View.VISIBLE);
		downAnimationDrawable.start();
	}
	
	/**
	 * hide footer when disable pull load more
	 */
	public void hide() {
		LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}
	
	/**
	 * show footer
	 */
	public void show() {
		LayoutParams lp = (LayoutParams)mContentView.getLayoutParams();
		lp.height = LayoutParams.WRAP_CONTENT;
		mContentView.setLayoutParams(lp);
	}
	
	private void initView(Context context) {
		mContext = context;
		LinearLayout moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.smoothlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		mContentView = moreView.findViewById(R.id.smoothlistview_footer_content);
		mHintView = (TextView)moreView.findViewById(R.id.smoothlistview_footer_hint_textview);
		ivBeeDown = (ImageView)findViewById(R.id.iv_bee_down);
		ivBeeDown.setImageResource(R.drawable.down_anim);
		downAnimationDrawable= (AnimationDrawable) ivBeeDown.getDrawable();
	}
	
	
}
