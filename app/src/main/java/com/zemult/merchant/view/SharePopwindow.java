package com.zemult.merchant.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.zemult.merchant.R;

/**
 * Created by admin on 2016/6/20.
 */
public class SharePopwindow extends PopupWindow implements View.OnClickListener {
    LinearLayout llSina;
    LinearLayout llShareWechat;
    LinearLayout llShareWechatFriend;
    LinearLayout llQQ;
    LinearLayout llShareResend;

    LinearLayout llShareYuefu;
    LinearLayout llShareLianxiren;
    RelativeLayout llCancle;
    View viewBlack;
    private View view;


    public static final int SINA = 1;
    public static final int WECHAT = 2;
    public static final int WECHAT_FRIEND = 3;
    public static final int QQ = 4;
    public static final int RESEND = 5;
    public static final int LXR = 6;
    public static final int YUEFU = 7;

    public SharePopwindow(Context context, OnItemClickListener onItemClickListener) {
        setOnItemClickListener(onItemClickListener);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_plandetail_share_layout, null);
        llSina = (LinearLayout) view.findViewById(R.id.ll_share_sina);
        llShareWechat = (LinearLayout) view.findViewById(R.id.ll_share_wechat);
        llShareWechatFriend = (LinearLayout) view.findViewById(R.id.ll_share_wechat_friend);
        llQQ = (LinearLayout) view.findViewById(R.id.ll_share_qq);
        llShareYuefu= (LinearLayout) view.findViewById(R.id.ll_share_yuefu);
        llShareLianxiren= (LinearLayout) view.findViewById(R.id.ll_share_lianxiren);
        llShareResend = (LinearLayout) view.findViewById(R.id.ll_share_resend);
        llCancle = (RelativeLayout) view.findViewById(R.id.ll_cancle);
        viewBlack = (View) view.findViewById(R.id.view);

        llSina.setOnClickListener(this);
        llShareWechat.setOnClickListener(this);
        llShareWechatFriend.setOnClickListener(this);
        llShareLianxiren.setOnClickListener(this);
        llShareYuefu.setOnClickListener(this);
        llQQ.setOnClickListener(this);
        llShareResend.setOnClickListener(this);
        llCancle.setOnClickListener(this);
        viewBlack.setOnClickListener(this);

        this.setContentView(view);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new BitmapDrawable());
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.umeng_socialize_shareboard_animation);
    }

    public void  showType(boolean weixin,boolean quan,boolean qq,boolean xinlang,boolean yuefu,boolean lxr){
            llSina.setVisibility(xinlang?View.VISIBLE:View.GONE);
            llShareWechat.setVisibility(weixin?View.VISIBLE:View.GONE);
            llShareWechatFriend.setVisibility(quan?View.VISIBLE:View.GONE);
            llQQ.setVisibility(qq?View.VISIBLE:View.GONE);
            llShareLianxiren.setVisibility(lxr?View.VISIBLE:View.GONE);
            llShareYuefu.setVisibility(yuefu?View.VISIBLE:View.GONE);

    }

    public void showResend(){
        llShareResend.setVisibility(View.VISIBLE);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_share_sina:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(SINA);
                break;
            case R.id.ll_share_wechat:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(WECHAT);
                break;
            case R.id.ll_share_wechat_friend:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(WECHAT_FRIEND);
                break;
            case R.id.ll_share_qq:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(QQ);
                break;
            case R.id.ll_share_resend:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(RESEND);
                break;

            case R.id.ll_share_yuefu:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(YUEFU);
                break;
            case R.id.ll_share_lianxiren:
                if(onItemClickListener != null)
                    onItemClickListener.onItemClick(LXR);
                break;
            case R.id.ll_cancle:
                //销毁弹出框
                this.dismiss();
                break;
            case R.id.view:
                //销毁弹出框
                this.dismiss();
                break;
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}

