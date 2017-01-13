package com.zemult.merchant.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.zemult.merchant.app.base.MBaseActivity;
import com.zemult.merchant.util.imagepicker.PupWindowUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * 图片文件操作工具类
 */
public class PhotoFileUtil {
    public static MBaseActivity _context;

    public static void saveImg(MBaseActivity context, String path) {
        _context = context;
        new SaveImage().execute(path);
    }

    public static File file;

    /***
     * 功能：用线程保存图片
     */
    private static class SaveImage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String imgurl = params[0];
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                file = new File(sdcard + "/yogous/save");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf(".");
                String ext = imgurl.substring(idx);
                file = new File(sdcard + "/yogous/save" + new Date().getTime() + ext);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(_context, result, Toast.LENGTH_LONG).show();
            try {
                saveToMedia(file);
            } catch (Exception e) {
            }
            _context.dismissPd();
            PupWindowUtil.dismiss();
        }
    }

    private static void saveToMedia(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            _context.sendBroadcast(mediaScanIntent);
        } else {
            _context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }


    public static boolean saveMyBitmap(Bitmap mBitmap, String bitName) {
        FileOutputStream fOut = null;
        boolean saved = false;
        if (isFolderExists("/sdcard/xiegang/")) {
            File f = new File("/sdcard/xiegang/" + bitName + ".jpg");
            try {
                fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                saved = true;
            } catch (IOException e) {
                e.printStackTrace();
                saved = false;
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return saved;
    }

    static boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);

        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else
                return false;
        }
        return true;
    }

}
