package com.zemult.merchant.im.sample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMTribeAtPageUI;
import com.zemult.merchant.R;

/**
 * 全部@消息
 * Created by weiquanyun on 15/10/23.
 */
public class AtMsgListUISample extends IMTribeAtPageUI {

    public AtMsgListUISample(Pointcut pointcut){
        super(pointcut);
    }

    /**
     * 自定义title bar
     * @param activity
     * @param context
     * @param layoutInflater
     * @return
     */
    @Override
    public View getCustomTitle(final Activity activity, final Context context, LayoutInflater layoutInflater) {
        RelativeLayout customView = (RelativeLayout) layoutInflater
                .inflate(R.layout.demo_custom_at_msg_titlebar, null);
        customView.setBackgroundColor(Color.parseColor("#FFA726"));//context.getResources().getColor(R.color.aliwx_common_bg_blue_color)
        TextView title = (TextView) customView.findViewById(R.id.title_txt);
        title.setText(context.getResources().getString(R.string.aliwx_at_message_title));
//        title.setTextColor(Color.parseColor("#FFFFFF"));
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(context, "click ", Toast.LENGTH_SHORT).show();

            }
        });
        TextView backButton = (TextView) customView.findViewById(R.id.left_button);
//        backButton.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.demo_common_back_btn_white, 0, 0, 0);
        backButton.setText("");
//        backButton.setTextColor(Color.parseColor("#FFFFFF"));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.finish();
            }
        });
        backButton.setVisibility(View.VISIBLE);
        return customView;
    }

    public View getCustomTitle(Fragment fragment, Context context, LayoutInflater inflater) {
        return null;
    }


//    /**
//     * 设置自定义Tab Indicator的颜色值，请使R.color.xxx格式的颜色Id传值
//     * @return
//     *      颜色资源Id
//     */
//    @Override
//    public int getCustomAtMsgTabIndicatorColorId() {
//        return 0;
//    }
//
//    /**
//     * 设置自定义Tab Indicator的颜色值，请使R.color.xxx格式的颜色Id传值
//     * 如果想要设置选中和非选中文字效果，请使用selector类型的R.color.xxx格式
//     * @return
//     *      颜色资源Id
//     */
//    @Override
//    public int getCustomAtMsgTabTextColorId() {
//        return 0;
//    }
//
//    /**
//     * 设置自定义我收到的@消息Tab图标，请使用R.drawable.xxx格式的图片资源Id传值。
//     * 如果想要设置选中和非选中图片效果，请使用selector类型的drawable
//     * @return
//     *      图片资源Id
//     */
//    @Override
//    public int getCustomRecAtMsgTabIndicatorImageSrcId() {
//        return 0;
//    }
//
//    /**
//     * 设置自定义我发出的@消息Tab图标，请使用R.drawable.xxx格式的图片资源Id传值。
//     * 如果想要设置选中和非选中图片效果，请使用selector类型的drawable
//     * @return
//     *      图片资源Id
//     */
//    @Override
//    public int getCustomSendAtMsgTabIndicatorImageSrcId() {
//        return 0;
//    }
//
//    /**
//     * 自定义是否需要在Tab选中后展示选中标识线
//     * @return
//     */
//    @Override
//    public boolean isNeedDrawIndicatorLine() {
//        return false;
//    }
}
