package com.zemult.merchant.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zemult.merchant.R;

/**
 * Created by Wikison on 2017/4/10.
 */

public class RecordDialog {

    private AlertDialog.Builder builder;
    private ImageView mIcon;
    private TextView mLabel;

    private Context mContext;

    //用于取消AlertDialog.Builder
    private AlertDialog dialog;

    /**
     * 构造方法 传入上下文
     */
    public RecordDialog(Context context) {
        this.mContext = context;
    }

    // 显示录音的对话框
    public void showRecordingDialog() {

        builder = new AlertDialog.Builder(mContext, R.style.AudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_record, null);

        mIcon = (ImageView) view.findViewById(R.id.iv_dialog_record);
        mLabel = (TextView) view.findViewById(R.id.tv_dialog_hint);

        builder.setView(view);
        builder.create();
        dialog = builder.show();
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setImageResource(R.mipmap.dialog_recorder);
            mLabel.setText("手指上滑，取消发送");
            mLabel.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setImageResource(R.mipmap.dialog_recorder_cancel);
            mLabel.setText("松开手指，取消发送");
            mLabel.setBackgroundColor(mContext.getResources().getColor(R.color.font_busy));
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            mIcon.setImageResource(R.mipmap.dialog_recorder_short);
            mLabel.setText("录音时间过短, 至少3秒");
            mLabel.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
    }

    // 显示取消的对话框
    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) { //显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

}
