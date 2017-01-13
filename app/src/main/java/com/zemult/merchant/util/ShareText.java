package com.zemult.merchant.util;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by Wikison on 2016/10/31.
 */

public class ShareText {

    public static String shareMediaToCN(SHARE_MEDIA platform) {
        String platformText = "";
        switch (platform.toString()) {
            case "QQ":
                platformText = "QQ";
                break;
            case "WEIXIN":
                platformText = "微信";
                break;
            case "WEIXIN_CIRCLE":
                platformText = "微信朋友圈";
                break;
            case "SMS":
                platformText = "短信";
                break;
            case "SINA":
                platformText = "新浪微博";
                break;
            case "QZONE":
                platformText = "QQ空间";
                break;
            case "TENCENT":
                platformText = "腾讯微博";
                break;
        }

        return platformText;
    }
}
