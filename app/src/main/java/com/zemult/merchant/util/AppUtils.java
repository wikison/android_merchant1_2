package com.zemult.merchant.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.zemult.merchant.bean.ContactDataBean;
import com.zemult.merchant.config.Constants;
import com.zemult.merchant.im.sample.LoginSampleHelper;
import com.zemult.merchant.im.sample.NotificationInitSampleHelper;
import com.zemult.merchant.im.sample.UserProfileSampleHelper;
import com.zemult.merchant.util.imagepicker.EditImageReciver;
import com.zemult.merchant.util.imagepicker.ImageBrowserActivity;
import com.zemult.merchant.util.imagepicker.ImageBrowserNewActivity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }

    final static String ATTRIBUTE_FILE_NAME = "att.property";

    public static void deleteAllFiles(File file) {
        if (file.exists() && file.isDirectory()) {
            boolean result = false;
            if (!(result = file.delete())) {
                File subs[] = file.listFiles();
                if (subs != null) {
                    for (File sub : subs) {
                        if (sub.isDirectory()) {
                            deleteAllFiles(sub);
                        } else {
                            result = sub.delete();
                        }
                    }

                }
                result = file.delete();
            }
        }
        if (file.exists() && !file.getName().equals(ATTRIBUTE_FILE_NAME)) {
            file.delete();
        }
    }

    public static String removeFileHeader(String fileName) {
        try {
            if (fileName.startsWith("file:///")) {
                fileName = fileName.substring(7);
            }
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    public static String getFileName(String fileName) {
        try {
            if (fileName.startsWith("file:///")) {
                fileName = fileName.substring(7);
            }
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return fileName;
        }
    }

    /*
  * 为照片命名
  */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    /**
     * 拍照
     *
     * @param imageName   文件名称,例如:"ier2222.jpg"
     * @param requestCode startActivityForResult的请求参数
     */
    public static void tackPic(Activity activity, String imageName,
                               int requestCode) {
        try {
            if (!AppUtils.isSDUseable()) {
                Toast.makeText(activity, "SD卡不可用,无法拍照", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            File file = new File(Constants.SAVE_IMAGE_PATH_IMGS, imageName);
            if (file.exists()) {
                file.delete();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            Toast.makeText(activity, "打开相机失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static boolean isSDUseable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    /**
     * 拍照返回处理
     *
     * @param imageName
     * @param handler
     */
    public static void tackPickResult(final String imageName,
                                      final Handler handler) {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 压缩照片
                        File file = new File(Constants.SAVE_IMAGE_PATH_IMGS, imageName);
                        ImageHelper.saveCompressBitmap(file);
                        Message message = new Message();
                        message.what = Constants.PHOTO_COMPASS_SUCCESS;
                        message.obj = Constants.SAVE_IMAGE_PATH_IMGS + imageName;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到图片详情
     *
     * @param activity
     * @param position       当前图片位置
     * @param pics          所有图片路径
     * @param //图片详情页面是否可以删除
     */
    public static void toImageDetial(Activity activity, int position,
                                     List<String> pics, List<Integer> picIds, boolean deleteable, boolean coverable,boolean unshowTitle,int merchantId, int userId) {
        try {
            Intent intent = new Intent(activity, ImageBrowserNewActivity.class);
            intent.putExtra(ImageBrowserNewActivity.INTENT_PICS, (Serializable) pics);
            intent.putExtra(ImageBrowserNewActivity.INTENT_PICIDS, (Serializable) picIds);
            intent.putExtra(ImageBrowserNewActivity.INTENT_POS, position);
            intent.putExtra(ImageBrowserNewActivity.INTENT_COVERABLE, coverable);
            intent.putExtra(ImageBrowserNewActivity.INTENT_DELABLE, deleteable);
            intent.putExtra(ImageBrowserNewActivity.INTENT_MERCHANTID, merchantId);
            intent.putExtra(ImageBrowserNewActivity.INTENT_USERID, userId);
            intent.putExtra(ImageBrowserNewActivity.INTENT_UNSHOWTITLE, unshowTitle);
            activity.startActivityForResult(intent, Constants.IMAGEDITAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 跳转到图片详情
     *
     * @param activity
     * @param position       当前图片位置
     * @param pics          所有图片路径
     * @param //图片详情页面是否可以删除
     */
    public static void toImageDetial(Activity activity, int position, List<String> pics, List<String> notes) {
        try {
            Intent intent = new Intent(activity, ImageBrowserNewActivity.class);
            intent.putExtra(ImageBrowserNewActivity.INTENT_PICS, (Serializable) pics);
            intent.putExtra(ImageBrowserNewActivity.INTENT_NOTES, (Serializable) notes);
            intent.putExtra(ImageBrowserNewActivity.INTENT_POS, position);
            activity.startActivityForResult(intent, Constants.IMAGEDITAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 跳转到图片详情
     *
     * @param activity
     * @param position       当前图片位置
     * @param paths          所有图片路径
     * @param //图片详情页面是否可以删除
     */
    public static void toImageDetial(Activity activity, int position,
                                     List<String> paths, boolean deleteable) {
        try {
            Intent intent = new Intent(activity, ImageBrowserActivity.class);
            intent.putExtra("paths", (Serializable) paths);
            intent.putExtra("position", position);
            intent.putExtra("deleteable", deleteable);
            activity.startActivityForResult(intent, Constants.IMAGEDITAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到图片详情
     *
     * @param activity
     * @param position       当前图片位置
     * @param paths          所有图片路径
     * @param //图片详情页面是否可以删除
     */
    public static void toImageDetial(Activity activity, int position,
                                     List<String> paths, EditImageReciver editImageReciver) {
        try {
            IntentFilter intentFilter = new IntentFilter(
                    Constants.EDIT_IMG_REQCODE);
            activity.registerReceiver(editImageReciver, intentFilter);

            Intent intent = new Intent(activity, ImageBrowserActivity.class);
            intent.putExtra("paths", (Serializable) paths);
            intent.putExtra("position", position);
            intent.putExtra("deleteable", true);
            activity.startActivityForResult(intent, Constants.IMAGEDITAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static long getFolderSize(File file) {

        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);

                } else {
                    size = size + fileList[i].length();

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return size;
    }


    public static String getFormatSize(long size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "KB";//Byte(s)
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    private static String pattern = "yyyy-MM-dd HH:mm:ss";
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        SimpleDateFormat format = new SimpleDateFormat(pattern);
                        String dateStr = json.getAsString();
                        if (!"".equals(dateStr)) {
                            return format.parse(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            })
            .setDateFormat(pattern)
            .registerTypeAdapter(Object.class, new JsonDeserializer<Double>() {
                public Double deserialize(JsonElement json, Type typeOfT,
                                          JsonDeserializationContext context)
                        throws JsonParseException {
                    try {
                        return json.getAsDouble();
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }
                    return 0d;
                }
            }).create();


    /**
     * 检查当前网络是否可用
     *
     * @param activity
     * @return
     */

    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            ToastUtil.showMessage("您的网络不可用");
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        ToastUtil.showMessage("您的网络不可用");
        return false;
    }

    //初始化聊天
    public static void initIm(String userId, String appKey) {
        //初始化imkit
        LoginSampleHelper.getInstance().initIMKit(userId, appKey);
        //自定义头像和昵称回调初始化(如果不需要自定义头像和昵称，则可以省去)
        UserProfileSampleHelper.initProfileCallback();
        //通知栏相关的初始化
        NotificationInitSampleHelper.init();

    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static String getPhoneNumbers(Context context) {
        ContactsDao util = new ContactsDao(context);
        List<ContactDataBean> listMembers = new ArrayList<ContactDataBean>();
        util.getAllContact(listMembers);
        List<ContactDataBean> cList2 = util.removeDuplicateData(listMembers);
        for (ContactDataBean contactDataBean : cList2) {
            String selfphone = SlashHelper.userManager().getUserinfo().getPhoneNum();
            if (selfphone.equals(contactDataBean.getPhone()) == true) {
                cList2.remove(contactDataBean);
                break;
            }
        }

        List<String> numberList1 = new ArrayList<String>();
        for (ContactDataBean bean : cList2) {
            numberList1.add(bean.getPhone());
        }

        String numbers = "";
        for (String num : numberList1) {
            numbers = numbers + "," + num;

        }
        if (numbers.indexOf(",") != -1) {
            return numbers.substring(1, numbers.length());
        }
        return "";
    }

    public static String getPhoneNumbersWithName(Context context) {
        ContactsDao util = new ContactsDao(context);
        List<ContactDataBean> listMembers = new ArrayList<ContactDataBean>();
        util.getAllContact(listMembers);
        List<ContactDataBean> cList2 = util.removeDuplicateData(listMembers);
        for (ContactDataBean contactDataBean : cList2) {
            String selfphone = SlashHelper.userManager().getUserinfo().getPhoneNum();
            if (selfphone.equals(contactDataBean.getPhone()) == true) {
                cList2.remove(contactDataBean);
                break;
            }
        }

        List<String> numberList1 = new ArrayList<String>();
        for (ContactDataBean bean : cList2) {
            numberList1.add(bean.getPhone()+"#"+bean.getName());
        }

        String numbers = "";
        for (String num : numberList1) {
            numbers = numbers + "," + num;

        }
        if (numbers.indexOf(",") != -1) {
            return numbers.substring(1, numbers.length());
        }
        return "";
    }


    public static String getConactName(List<ContactDataBean> listMembers, String phoneno) {
        String conactName = "";
        for (int i = 0; i < listMembers.size(); i++) {
            if (listMembers.get(i).getPhone().equals(phoneno)) {
                conactName = listMembers.get(i).getName();
                break;
            }
        }
        return conactName;
    }


    //发送短信
    public static void sendMsg(Context context, String num, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                + num));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }


    public static String getHiddenMobile(String mobile) {
        if (mobile != null && mobile.length() == 11) {
            return mobile.substring(0, 3) + "****"
                    + mobile.substring(7, mobile.length());
        }
        return mobile;
    }


    public static String giftDescription(String giftname){
            String desc="";
        if (giftname.contains("兰博基尼")) {
            desc="兰博基尼：电光火石，速度激情。";

        } else if (giftname.contains("钻戒")) {
            desc="通灵钻戒：一颗流传，伴生相随。";

        } else if (giftname.contains("钱包")) {
            desc="LV钱包：生命本身就是一场旅行。";

        } else if (giftname.contains("花")) {
            desc=" 爱の花：遇见最美的绽放……";
        }
        else if (giftname.contains("六六大顺")) {
            desc=" 六六大顺：三三不尽，六六无穷。";
        }
        
        return desc;
        
    }




    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }



}