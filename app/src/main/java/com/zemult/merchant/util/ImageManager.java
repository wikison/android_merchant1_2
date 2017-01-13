package com.zemult.merchant.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zemult.merchant.R;
import com.zemult.merchant.app.AppApplication;
import com.zemult.merchant.view.GlideCircleBorderTransform;
import com.zemult.merchant.view.GlideCircleTransform;

/**
 * Created by admin on 2016/6/3.
 */
public class ImageManager {
    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FOREWARD_SLASH = "/";
    private Context mContext;
    private static ImageManager instance;

    public ImageManager(Context context) {
        this.mContext = context;
    }

    //设置获取阿里服务器图片大小
    public void setImageSize(String iamgesize) {
        SlashHelper.setSettingString(SlashHelper.APP.Key.IMAGESIZE, iamgesize);
    }

    public String getImageSize() {
        return SlashHelper.getSettingString(SlashHelper.APP.Key.IMAGESIZE, "@100p");
    }


    public void clearDiskImageCash() {
        Glide.get(mContext).clearDiskCache();
    }

    // 将资源ID转为Uri
    public Uri resourceIdToUri(int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + mContext.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    // 加载网络图片
    public void loadUrlImage(String url, ImageView imageView) {
        if (url.indexOf("xiegang.oss") != -1) {
            url = url.replace("xiegang.oss", "xiegang.img");
            Glide.with(AppApplication.instance().getApplicationContext())
                    .load(url + getImageSize())
                    .placeholder(R.color.font_black_e0)
                    .error(R.mipmap.tupiansilie_icon)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        } else {
            Glide.with(AppApplication.instance().getApplicationContext())
                    .load(url)
                    .placeholder(R.color.font_black_e0)
                    .error(R.mipmap.tupiansilie_icon)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
    }

    // 加载网络图片
    public void loadUrlImage(String url, ImageView imageView, String rule) {
        if (url.indexOf("xiegang.oss") != -1) {
            url = url.replace("xiegang.oss", "xiegang.img");
            Glide.with(AppApplication.instance().getApplicationContext())
                    .load(url + rule)
                    .placeholder(R.color.font_black_e0)
                    .error(R.mipmap.tupiansilie_icon)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        } else {
            Glide.with(AppApplication.instance().getApplicationContext())
                    .load(url)
                    .placeholder(R.color.font_black_e0)
                    .error(R.mipmap.tupiansilie_icon)
                    .crossFade()
                    .fitCenter()
                    .into(imageView);
        }
    }

    // 加载网络图片
    public void loadUrlImage2(String url, ImageView imageView) {
        Glide.with(AppApplication.instance().getApplicationContext())
                .load(url)
                .placeholder(R.color.font_black_e0)
                .error(R.mipmap.tupiansilie_icon)
                .crossFade()
                .fitCenter()
                .into(imageView);
    }

    // 加载drawable图片
    public void loadResImage(int resId, ImageView imageView) {
        Glide.with(mContext)
                .load(resourceIdToUri(resId))
                .placeholder(R.color.font_black_e0)
                .error(R.mipmap.tupiansilie_icon)
                .crossFade()
                .into(imageView);
    }

    // 加载本地图片
    public void loadLocalImage(String path, ImageView imageView) {
        Glide.with(mContext)
                .load("file://" + path)
                .placeholder(R.color.font_black_e0)
                .error(R.mipmap.tupiansilie_icon)
                .crossFade()
                .into(imageView);
    }

    // 加载网络圆型图片
    public void loadCircleImage(String url, ImageView imageView) {
        if (url.indexOf("xiegang.oss") != -1) {
            url = url.replace("xiegang.oss", "xiegang.img");
            Glide.with(mContext)
                    .load(url + getImageSize())
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(resourceIdToUri(R.mipmap.tupiansilie_icon))
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
        }
    }

    // 加载网络圆型图片
    public void loadCircleImage(String url, ImageView imageView, String rule) {
        if (url.indexOf("xiegang.oss") != -1) {
            url = url.replace("xiegang.oss", "xiegang.img");
            Glide.with(mContext)
                    .load(url + rule)
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(resourceIdToUri(R.mipmap.tupiansilie_icon))
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(imageView);
        }
    }


    // 加载网络圆型图片-带边框
    public void loadCircleHasBorderImage(String url, ImageView imageView, int bordercolor, int borderwidth) {
        if (url != null && url.indexOf("xiegang.oss") != -1) {
            url = url.replace("xiegang.oss", "xiegang.img");
            Glide.with(mContext)
                    .load(url + getImageSize())
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleBorderTransform(mContext, bordercolor, borderwidth))
                    .into(imageView);
        } else {
            //不是网络图片加载本地图片
            Glide.with(mContext)
                    .load(resourceIdToUri(R.mipmap.tupiansilie_icon))
                    .placeholder(R.drawable.xml_circle_grey_bg)
                    .error(R.mipmap.tupiansilie_circle_icon)
                    .crossFade()
                    .transform(new GlideCircleBorderTransform(mContext, bordercolor, borderwidth))
                    .into(imageView);
        }
    }

    // 加载drawable圆型图片
    public void loadCircleResImage(int resId, ImageView imageView) {
        Glide.with(mContext)
                .load(resourceIdToUri(resId))
                .placeholder(R.color.font_black_e0)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
    }

    // 加载本地圆型图片
    public void loadCircleLocalImage(String path, ImageView imageView) {
        Glide.with(mContext)
                .load("file://" + path)
                .placeholder(R.color.font_black_e0)
                .error(R.mipmap.tupiansilie_circle_icon)
                .crossFade()
                .transform(new GlideCircleTransform(mContext))
                .into(imageView);
    }


}
