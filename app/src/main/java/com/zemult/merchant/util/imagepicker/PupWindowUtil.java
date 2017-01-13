package com.zemult.merchant.util.imagepicker;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * 弹出框pop 工具类
 *
 */
public class PupWindowUtil {
	private static PopupWindow pop;
	/**
	 * 显示pop 外部可以点击
	 * @param context
	 * @param resId 试图资源id
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithOutSideTouch(Context context , int 
			resId, View parent, int gravity, int x, int y ) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(LayoutInflater.from(context).inflate(resId,null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        pop.setBackgroundDrawable(new BitmapDrawable());
	        pop.setOutsideTouchable(true);
	        pop.showAtLocation(parent, gravity, x, y);
		}else{
			pop.dismiss();
		}
	}
	/**
	 * 显示pop 外部不可以点击
	 * @param context
	 * @param resId 试图资源id
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithoutOutSideTouch(Context context , int 
			resId, View parent, int gravity, int x, int y ) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(LayoutInflater.from(context).inflate(resId,null), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(false);
			pop.showAtLocation(parent, gravity, x, y);
		}else{
			pop.dismiss();
		}
	}
	/**
	 * 显示pop 外部可以点击
	 * @param showView 视图
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithOutSideTouch(View 
			showView, View parent, int gravity, int x, int y ) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(showView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(true);
			pop.showAsDropDown(parent, x, y);
		}else{
			pop.dismiss();
		}
	}
	/**
	 * 显示pop 外部可以点击
	 * @param showView 视图
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithoutOutSideTouchAsDropDown(View 
			showView, View parent, int x, int y ) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(showView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			pop.showAsDropDown(parent, x, y);
		}else{
			pop.dismiss();
		}
	}
	/**
	 * 显示pop 外部不可以点击
	 * @param showView 视图
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithoutOutSideTouch(View 
			showView, View parent, int gravity, int x, int y ) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(showView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			pop.showAtLocation(parent, gravity, x, y);
		}else{
			pop.dismiss();
		}
	}
	/**
	 * 显示pop 外部不可以点击
	 * @param showView 视图
	 * @param parent 显示依赖父视图
	 * @param gravity 显示位置
	 * @param x 横轴偏移
	 * @param y 纵轴偏移
	 */
	public static void showWithoutOutSideTouch(View 
			showView, View parent, int gravity, int x, int y ,int width , int height) {
		if(null == pop || !pop.isShowing()){
			pop = new PopupWindow(showView, width, height);
			pop.setBackgroundDrawable(new BitmapDrawable());
			pop.setOutsideTouchable(false);
			pop.showAtLocation(parent, gravity, x, y);
		}else{
			pop.dismiss();
		}
	}
	
	public static void dismiss() {
		if(null != pop && pop.isShowing())
			pop.dismiss();
	}
	
	
	
}
