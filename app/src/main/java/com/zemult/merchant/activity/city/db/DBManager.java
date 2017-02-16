package com.zemult.merchant.activity.city.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.zemult.merchant.activity.city.entity.City;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DBManager {
    private static final String ASSETS_NAME = "china_cities.db";
    private static final String DB_NAME = "china_cities.db";
    private static final String TABLE_NAME = "region";
    private static final String TABLE_NAME_RECENT = "recent";
    private static final String SHORT_NAME = "FShortName";
    private static final String NAME = "FName";
    private static final String FNO = "FNo";
    private static final String PINYIN = "FPinYin";
    private static final String CREATE_TIME = "FCreateTime";
    private static final int BUFFER_SIZE = 1024;
    private String DB_PATH;
    private Context mContext;

    public DBManager(Context context) {
        this.mContext = context;
        DB_PATH = File.separator + "data"
                + Environment.getDataDirectory().getAbsolutePath() + File.separator
                + context.getPackageName() + File.separator + "databases" + File.separator;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void copyDBFile() {
        File dir = new File(DB_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dbFile = new File(DB_PATH + DB_NAME);
        if (!dbFile.exists()) {
            InputStream is;
            OutputStream os;
            try {
                is = mContext.getResources().getAssets().open(ASSETS_NAME);
                os = new FileOutputStream(dbFile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int length;
                while ((length = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("Tag", "文件存在");
        }
    }

    /**
     * 读取所有城市
     *
     * @return
     */
    public List<City> getAllCities() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where FRank=2", null);
        List<City> result = new ArrayList<>();
        City city;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(SHORT_NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            String no = cursor.getString(cursor.getColumnIndex(FNO));
            city = new City(name, pinyin, no);
            result.add(city);
        }
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param keyword
     * @return
     */
    public List<City> searchCity(final String keyword) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where FName like \"%" + keyword
                + "%\" or FPinYin like \"%" + keyword + "%\"" + " and FRank=2", null);
        List<City> result = new ArrayList<>();
        City city;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(SHORT_NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            String no = cursor.getString(cursor.getColumnIndex(FNO));
            if (pinyin != null) {
                city = new City(name, pinyin, no);
                result.add(city);
            }
        }
        cursor.close();
        db.close();
        Collections.sort(result, new CityComparator());
        return result;
    }

    //插入最近选择的城市
    public void insertCity(City city) {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_RECENT + " where FName = '"
                + city.getName() + "'", null);
        if (cursor.getCount() > 0) { //
            db.delete(TABLE_NAME_RECENT, "FName = ?", new String[]{city.getName()});
        }
        db.execSQL("insert into recent(FName, FPinYin, FNo, FCreateTime) values('" + city.getName() + "', '" + city.getPinyin() + "', '" + city.getNo() + "',"
                + System.currentTimeMillis() + ")");
        db.close();
    }

    /**
     * 读取最近访问的城市
     *
     * @return
     */
    public List<City> getRecentCities() {
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_RECENT + " ORDER BY FCreateTime DESC LIMIT 3 ", null);
        List<City> result = new ArrayList<>();
        City city;
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String pinyin = cursor.getString(cursor.getColumnIndex(PINYIN));
            String no = cursor.getString(cursor.getColumnIndex(FNO));
            city = new City(name, pinyin, no);
            result.add(city);
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyin().substring(0, 1);
            String b = rhs.getPinyin().substring(0, 1);
            return a.compareTo(b);
        }
    }


}
