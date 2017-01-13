package com.zemult.merchant.app.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.zemult.merchant.R;
import com.zemult.merchant.util.AppUtils;
import com.zemult.merchant.view.common.LoadingDialog;

public class MBaseFragmentActivity extends FragmentActivity implements OnClickListener {
    public LayoutParams layoutParams;
    protected RelativeLayout appTopView, appMainView;
    public LoadingDialog pd;
    protected Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.app_base_layout);
        appTopView = (RelativeLayout) this.findViewById(R.id.app_layout_top);
        appMainView = (RelativeLayout) this.findViewById(R.id.app_layout_main);
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        pd = new LoadingDialog(this);
//		pd.setMessage(getString(R.string.handle_ing));
        pd.setCancelable(false);
        AppUtils.isNetworkAvailable(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissPd();
    }

    protected void showPd() {
        try {
            if (pd != null && !pd.isShowing()) {
                pd.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void dismissPd() {
        try {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
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
        TextView titleTextView = (TextView) findViewById(R.id.top_center_tv);
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

        if (null == buttonText || "".equals(buttonText)) {
            return;
        }

        Button leftButton = (Button) findViewById(R.id.top_left_btn);
        leftButton.setOnClickListener(this);

        leftButton.setText(buttonText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
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

        Button rightButton = (Button) findViewById(R.id.top_right_btn);
        rightButton.setOnClickListener(this);
        rightButton.setText(buttonText);

        if (appTopView.getVisibility() != View.VISIBLE) {
            appTopView.setVisibility(View.VISIBLE);
        }

        rightButton.setVisibility(View.VISIBLE);

    }

    public void setTitleRightButton(int resId) {
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


    protected boolean isFromPush() {
        return getIntent().getBooleanExtra("isFromPush", false);
    }

    @Override
    public void onClick(View v) {
    }
}
