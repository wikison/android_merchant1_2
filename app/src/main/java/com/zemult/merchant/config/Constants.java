package com.zemult.merchant.config;

import android.os.Environment;

import java.io.File;

/**
 * Created by Wikison on 2016/4/21.
 */
public class Constants {
    public static final String APPNAME = "yogous";
    public static final String APP_SHAREREFERENCE = "yogous";
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;// 拍照修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;// 本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;// 系统裁剪头像

    public static final int REQUESTCODE_CHANGENICKNAME = 4;// 修改昵称

    public static final int REQUESTCODE_CHANGESEX = 5;// 修改性别

    public static final int REQUESTCODE_MYINFO = 111;// 个人介绍
    // sd卡上文件夹的路径
    public final static String APP_ON_SD_PATH = APPNAME + File.separatorChar;
    // 缓存存储路径
    public final static String APP_FILE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separatorChar + APP_ON_SD_PATH;

    public final static String IMAGE_CACHE_DIR = APP_FILE_DIR + "imagecache"
            + File.separatorChar;

    public final static String ImageLoad_CACHE_DIR = "yogous/imagecache";

    // 图片缓存存储路径
    public final static String SAVE_IMAGE_PATH_IMGS = APP_FILE_DIR + "imgs"
            + File.separatorChar;
    // 声音缓存存储路径
    public final static String SOUND_CACHE_DIR = APP_FILE_DIR + "sounds"
            + File.separatorChar;

    // 广播oss上传头像图片
    public final static String BROCAST_OSS_UPLOADIMAGE = "BROCAST_OSS_UPLOADIMAGE";
    public final static String BROCAST_EDITUSERINFO = "BROCAST_EDITUSERINFO";
    public final static String BROCAST_OSS_UPLOADSOUND = "BROCAST_OSS_UPLOADSOUND";
    public final static String BROCAST_LOGIN = "BROCAST_LOGIN";
    public final static String BROCAST_FRESHTASKLIST = "BROCAST_FRESHTASKLIST";//刷新微任务和发任务列表
    public final static String BROCAST_MYORDERLIST = "BROCAST_MYORDERLIST";//刷新我的订单
    public final static String BROCAST_FRESHPARTYHOMELIST = "BROCAST_FRESHPARTYHOMELIST";//刷新聚会活动
    public final static String BROCAST_UPDATEMYINFO = "BROCAST_UPDATEMYINFO";
    public final static String BROCAST_UPDATESYSMESSAGE = "BROCAST_UPDATESYSMESSAGE";
    public final static String BROCAST_EDITMERCHANT = "BROCAST_EDITMERCHANT";
    public final static String BROCAST_FRESHSLASH = "BROCAST_FRESHSLASH";
    public final static String BROCAST_FRESHTASKCOMPLETE = "BROCAST_FRESHTASKCOMPLETE";
    public final static String BROCAST_CLOSESendLabelActivity = "BROCAST_CLOSESendLabelActivity";

    public static final String BROCAST_PUBLISH_TASK_ROLE = "BROCAST_PUBLISH_TASK_ROLE";
    public static final String BROCAST_PUBLISH_TASK_TYPE = "BROCAST_PUBLISH_TASK_TYPE";
    public static final String BROCAST_PUBLISH_TASK_VOTE = "BROCAST_PUBLISH_TASK_VOTE";
    public static final String BROCAST_PUBLISH_TASK_PERSON = "BROCAST_PUBLISH_TASK_PERSON";
    public static final String BROCAST_PUBLISH_TASK_VOICE = "BROCAST_PUBLISH_TASK_VOICE";
    public static final String BROCAST_PUBLISH_TASK_BONUSES = "BROCAST_PUBLISH_TASK_BONUSES";
    public static final String BROCAST_PUBLISH_TASK_COUPON = "BROCAST_PUBLISH_TASK_COUPON";
    public static final String BROCAST_WX_PAY_SUCCESS = "BROCAST_WX_PAY_SUCCESS";

    public static final String BROCAST_CLOSE_ACTIVITY_FORLABEL = "BROCAST_CLOSE_ACTIVITY_FORLABEL";
    public static final String BROCAST_SEARCH_RECENT_WORD = "BROCAST_SEARCH_RECENT_WORD";

    public static final String BROCAST_REFRESH_ORDER = "BROCAST_REFRESH_ORDER";
    public static final String BROCAST_BE_SERVER_MANAGER_SUCCESS = "BROCAST_BE_SERVER_MANAGER_SUCCESS";

    public static final String ENDPOINT = "http://oss-cn-shanghai.aliyuncs.com";
    public static final String OSSENDPOINT = "http://xiegang.oss-cn-shanghai.aliyuncs.com/";
    public static final String BUCKET = "xiegang";
    public static final String STSSERVER = "http://server.54xiegang.com/yongyou/inter_json/oss_ossToken.do";
    public static final String APP_DOWNLOAD_URL = Urls.MAIN_URL + "app_download/yogous/index.html";
    public static final String PROTOCOL_REGISTER = Urls.MAIN_URL + "html/dzyx_common/userProtocal.html";
    public static final String PROTOCOL_MERCHANT = Urls.MAIN_URL + "html/dzyx_common/merchantProtocal.html";
    public static final String URL_HELP = Urls.MAIN_URL + "html/help.html";
    public static final String PERINVITATIONFEEDBACKINFO = Urls.MAIN_URL + "dzyx/app/share_preInvitationfeedback_info.do?preId=";
    //服务管家等级
    public static String SERVICELEVEL = Urls.MAIN_URL + "html/dzyx_common/level.html";
    //服务管家协议
    public static String SERVICEXIEYI = Urls.MAIN_URL + "html/dzyx_sale/xieyi.html";
    //分享预邀单
    public static String PRE_SHARE_INVITATION = Urls.MAIN_URL + "dzyx/app/share_perInvitation_info.do?perId=";
    // APK
    public final static String APK_CACHE_DIR = APP_FILE_DIR + "apk"
            + File.separatorChar;
    // 图片选择
    public static final int TACKPHOTO = 1;// 拍照
    public static final int CHOOSE_PHOTOS = 2;// 相册选择
    public static final int CHOOSE_SINGLE_PHOTO = 3;// 单张图片选择
    public static final int PHOTO_COMPASS_SUCCESS = 4;// 图片压缩
    public static final int CHOOSE_SINGLE_PHOTO_SUCCESS = 5;// 单张图片选择成功
    public static final int EDIT_PHOTOS = 6;// 相册选择
    public static final int PHOTO_CROP = 7;// 图片裁剪
    // 添加图片时默认图片最大张数
    public final static int DEFAULT_IMAGE_MAX_SIZE = 9;
    // 图片详情
    public final static int IMAGEDITAL = 9909;
    // 修改图片
    public static final String EDIT_IMG_REQCODE = "EDIT_IMG_REQCODE";

    //每页显示条数
    public static final int ROWS = 20;
    //相册容量
    public static final int ABLUM_NUMS = 20;

    //热门搜索显示条数
    public static final int HOT_SEARCH_ROWS = 8;
    //热门搜索历史条数
    public static final int RECENT_SEARCH_ROWS = 8;

    public static final String SP_CITY = "city";
    public static final String SP_POI = "poi";
    public static final String SP_CENTER = "center";
    public static final double MIN_WITHDRAW = 100;
    public static final double MAX_WITHDRAW = 2000;
    public static final double MAX_PAY = 999999;
    public static String QR_PREFIX = "yogous";
    public static String QR_PAY_PREFIX = "pay://doScan?";
    public static String QR_USER_PREFIX = "userInfo://";
    public static String SCHEME_PREFIX = "zm://";

    // 定位失败默认值
    public static String CENTER = "119.971736,31.829737";
    public static String CITYID = "0519";
    public static String CITY_NAME = "常州";
    public static String CITY_PINYIN = "changzhou";

    //订单超时时间
    public static int ORDER_EXPIRE_MINUTE = 30;
    public static int ORDER_EXPIRE_SECOND = 1800;

    public static final String APP_ID = "wx0e6067b5bc878112";


}


