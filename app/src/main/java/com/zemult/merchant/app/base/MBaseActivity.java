package com.zemult.merchant.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.bugtags.library.Bugtags;
import com.google.gson.Gson;
import com.zemult.merchant.R;
import com.zemult.merchant.activity.LoginNewActivity;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.util.ImageManager;
import com.zemult.merchant.util.SlashHelper;
import com.zemult.merchant.view.common.CommonDialog;
import com.zemult.merchant.view.common.LoadingDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import zema.volley.network.VolleyUtil;

public class MBaseActivity extends Activity implements OnClickListener {
    public LayoutParams layoutParams;
    protected RelativeLayout appTopView, appMainView;
    public LoadingDialog pd;
    protected Gson gson = new Gson();
    public TextView tv_empty;
    public RelativeLayout re_empty;
    protected ArrayList<WeakReference<Request>> listJsonRequest;
    protected ImageManager imageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_base_layout);
        appTopView = (RelativeLayout) this.findViewById(R.id.app_layout_top);
        appMainView = (RelativeLayout) this.findViewById(R.id.app_layout_main);
        re_empty = (RelativeLayout) this.findViewById(R.id.re_empty);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        pd = new LoadingDialog(this);
        // pd.setMessage(getString(R.string.handle_ing));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        imageManager=new ImageManager(this);
        AppUtils.isNetworkAvailable(this);
    }


    /**
     * 发送请求
     *
     * @param request
     */
    public void sendJsonRequest(Request request) {

        if (listJsonRequest == null) {
            listJsonRequest = new ArrayList<WeakReference<Request>>();
        }
        WeakReference<Request> ref = new WeakReference<Request>(request);
        listJsonRequest.add(ref);
        VolleyUtil.getRequestQueue().add(request) ;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listJsonRequest != null) { //遍历取消所有请求
            for (WeakReference<Request> ref : listJsonRequest) {
                Request req = ref.get();
                if (req != null) {
                    req.cancel();
                }
            }
        }
        dismissPd();
    }

    public void showPd() {
        try {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissPd() {
        try {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void clearFoucse() {
        try {
            findViewById(R.id.top_center_tv).setFocusable(true);
            findViewById(R.id.top_center_tv).setFocusableInTouchMode(true);
            findViewById(R.id.top_center_tv).requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void showTitleLeftButton() {
        if (null == appTopView) {
            return;
        }

        Button leftButton = (Button) appTopView.findViewById(R.id.top_left_btn_image);
        leftButton.setOnClickListener(this);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        leftButton.setVisibility(View.VISIBLE);

    }

    protected void setTitleText(String titleText) {
        if (null == appTopView) {
            return;
        }
        if (null == titleText || "".equals(titleText)) {
            return;
        }
        TextView titleTextView = (TextView)appTopView.findViewById(R.id.top_center_tv);
        titleTextView.setText(titleText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        titleTextView.setVisibility(View.VISIBLE);
    }

    protected void setTitleLeftButton(String buttonText) {
        if (null == appTopView) {
            return;
        }

        if (null == buttonText ) {
            return;
        }

        Button leftButton = (Button) appTopView.findViewById(R.id.top_left_btn);
        leftButton.setOnClickListener(this);

        leftButton.setText(buttonText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        leftButton.setVisibility(View.VISIBLE);

    }

    protected void setTitleLeftButton(int resId , String buttonText) {
        if (null == appTopView) {
            return;
        }

        if (null == buttonText || "".equals(buttonText)) {
            return;
        }

        Button leftButton = (Button) appTopView.findViewById(R.id.top_left_btn);
        leftButton.setOnClickListener(this);

        leftButton.setText(buttonText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }
        if (-1 == resId) {
        	leftButton.setCompoundDrawables(null, null, null, null);
		}else{
			Drawable drawable =
	        		getResources().getDrawable(resId);
	        drawable.setBounds(drawable.getBounds().width(), drawable.getBounds().height(), 0, 0);
	        leftButton.setCompoundDrawables(drawable, null, null, null);
		}
        leftButton.setVisibility(View.VISIBLE);

    }

    protected void setTitleRightButton(String buttonText) {
        if (null == appTopView) {
            return;
        }
        if (null == buttonText || "".equals(buttonText)) {
            return;
        }

        Button rightButton = (Button) appTopView.findViewById(R.id.top_right_btn);
        rightButton.setOnClickListener(this);
        rightButton.setText(buttonText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        rightButton.setVisibility(View.VISIBLE);

    }

    public void showTitleRightButton() {
        if (null == appTopView) {
            return;
        }

        Button rightButton = (Button) appTopView.findViewById(R.id.top_right_btn_image);
        rightButton.setOnClickListener(this);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        rightButton.setVisibility(View.VISIBLE);
    }

    public void showTitleRightButton(int resId) {
        if (null == appTopView) {
            return;
        }

        Button rightButton = (Button) appTopView.findViewById(R.id.top_right_btn_image);
        rightButton.setOnClickListener(this);
        rightButton.setBackgroundResource(resId);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        rightButton.setVisibility(View.VISIBLE);
    }
    /**
     * ImageButton 设置的资源文件将作为图片显示 便于对图片按钮处理
     * @param resId
     */
    public void showTitleRightImageButton(int resId , OnClickListener onClickListener) {
    	if (null == appTopView) {
    		return;
    	}
    	ImageButton rightButton = (ImageButton) appTopView.findViewById(R.id._ib_more);
    	rightButton.setImageResource(resId);
    	rightButton.setOnClickListener(onClickListener);
    	if (appTopView.getVisibility() != View.VISIBLE) {
    		appTopView.setVisibility(View.VISIBLE);
    	}
    	rightButton.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏右侧按钮
     */
    public void hideRightImageButton() {
        ImageButton rightButton = (ImageButton) appTopView.findViewById(R.id._ib_more);
        rightButton.setVisibility(View.GONE);
    }
    public void showTitleRightButton2(int resId) {
        if (null == appTopView) {
            return;
        }

        Button rightButton = (Button) appTopView.findViewById(R.id.top_right_btn_image_2);
        rightButton.setOnClickListener(this);
        rightButton.setBackgroundResource(resId);

        Button rightButton2 = (Button) appTopView.findViewById(R.id.top_right_btn_image);
        if (rightButton2.getVisibility() == View.GONE) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, AppUtils.dip2px(this, 17), 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            lp.width = AppUtils.dip2px(this, 20);
            lp.height = AppUtils.dip2px(this, 20);
            rightButton.setLayoutParams(lp);
        }

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        rightButton.setVisibility(View.VISIBLE);
    }

    protected void setTitleRightButtonGreen() {
        // Button rightButton = (Button) findViewById(R.id.top_right_btn);
        // rightButton.setBackgroundResource(R.drawable.title_rightbutton_green_selector);
    }

    protected void setTitleRightButtonDisable() {
        Button rightButton = (Button) findViewById(R.id.top_right_btn);
        rightButton.setTextColor(this.getResources().getColor(R.color.disable_color));
        rightButton.setBackgroundResource(0);
        rightButton.setClickable(false);
        rightButton.setEnabled(false);
    }

    protected void setTitleRightButtonEnable() {
        Button rightButton = (Button) findViewById(R.id.top_right_btn);
        rightButton.setTextColor(this.getResources().getColor(R.color.white));
        rightButton.setClickable(true);
        rightButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_left_btn:
                beforeFinish();
                MBaseActivity.this.finish();
                break;

            default:
                break;
        }
    }

    public void beforeFinish() {

    }

    /*
   * 返回
   */
    public void doBack(View view) {
        onBackPressed();
    }

    protected boolean isFromPush() {
        return getIntent().getBooleanExtra("isFromPush", false);
    }

	public void showEmpty(String content , int resId){
		re_empty.setVisibility(View.VISIBLE);
		tv_empty.setText(content);
//		Drawable drawable = getResources().getDrawable(R.drawable.teacher_empty);
		Drawable drawable = getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		tv_empty.setCompoundDrawables(null, drawable, null, null);
	}
	public void hideEmpty() {
		re_empty.setVisibility(View.GONE);
	}

    public boolean noLogin(final Context context) {
        // 没有登录跳转到登录界面
        if (SlashHelper.userManager().getUserinfo() == null) {
            CommonDialog.showDialogListener(context, null, "否", "是", getResources().getString(R.string.not_login_tip), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();

                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonDialog.DismissProgressDialog();
                    startActivity(new Intent(context, LoginNewActivity.class));
                }
            });
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注：回调 1
        Bugtags.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注：回调 2
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //注：回调 3
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }
}
