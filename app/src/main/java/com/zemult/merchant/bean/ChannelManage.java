package com.zemult.merchant.bean;

import android.database.SQLException;
import android.util.Log;


import com.zemult.merchant.database.SQLHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChannelManage {

    public static ChannelManage channelManage;
    /**
     * 默认的用户选择频道列表
     */
    public static List<IndusPreferItem> defaultUserChannels = new ArrayList<IndusPreferItem>();
    ;
    /**
     * 默认的其他频道列表
     */
    public static List<IndusPreferItem> defaultOtherChannels = new ArrayList<IndusPreferItem>();
    private static IndusPreferDao indusPreferDao;
    /**
     * 判断数据库中是否存在用户数据
     */
    private boolean userExist = false;


//    //此处为网络获取数据
//    public static void setSjRequest(RequestQueue mQueue, final Handler handler) {//调用
//
//        defaultUserChannels = new ArrayList<ChannelItem>();
//        defaultOtherChannels = new ArrayList<ChannelItem>();
//        StringRequest sjRequest = new StringRequest(Request.Method.GET, "http://www.inroids.com/yongyou/inter_json/user_getindustryfathers.do", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                List<Industry.IndustrysEntity> datalist = new ArrayList<Industry.IndustrysEntity>();
//                try {
//
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray array = jsonObject.getJSONArray("industrys");
//                    for (int i = 0; i < array.length(); i++) {
//                        Industry.IndustrysEntity industrysEntity = new Industry.IndustrysEntity();
//                        industrysEntity.setName(array.getJSONObject(i).getString("name"));
//                        datalist.add(industrysEntity);
//                    }
//                    for (int j = 0; j <4; j++) {
//
//                        defaultUserChannels.add(new ChannelItem(j, datalist.get(j).getName().toString(), j, 1));//数组越界
//
//
//                    }
//
//
//                    for(int k=5 ;k<13;k++){
//
//                        defaultOtherChannels.add(new ChannelItem(k, "你好"+k, k, 0));}
////                    for (int k = 12; k < datalist.size(); k++) {
////
////                        defaultOtherChannels.add(new ChannelItem(k, datalist.get(k).getName().toString(), k, 0));
////
////                    }
////	             saveUserChannel(defaultUserChannels);
////	              saveOtherChannel(defaultOtherChannels);
//                    initDefaultChannel();
//
//
//                    handler.sendEmptyMessage(1);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        mQueue.add(sjRequest);
//
//    }

//	static {
//		defaultUserChannels = new ArrayList<ChannelItem>();
//		defaultOtherChannels = new ArrayList<ChannelItem>();
//
//		defaultUserChannels.add(new ChannelItem(1, "推荐", 1, 1));
//		defaultUserChannels.add(new ChannelItem(2, "餐饮", 2, 1));
//
//		defaultOtherChannels.add(new ChannelItem(8, "美妆", 1, 0));
//		defaultOtherChannels.add(new ChannelItem(9, "汽车", 2, 0));
//	}


    private ChannelManage(SQLHelper paramDBHelper) throws SQLException {
        if (indusPreferDao == null)
            indusPreferDao = new IndusPreferDao(paramDBHelper.getContext());
        // NavigateItemDao(paramDBHelper.getDao(NavigateItem.class));
        return;
    }

    /**
     * 初始化频道管理类
     *
     * @param
     * @throws SQLException
     */
    public static ChannelManage getManage(SQLHelper dbHelper) throws SQLException {
        if (channelManage == null)
            channelManage = new ChannelManage(dbHelper);
        return channelManage;
    }

    /**
     * 清除所有的频道
     */
    public static void deleteAllChannel() {
        indusPreferDao.clearFeedTable();
    }

    /**
     * 获取其他的频道
     *
     * @return 数据库存在用户配置 ? 数据库内的用户选择频道 : 默认用户选择频道 ;
     */
    public List<IndusPreferItem> getUserChannel() {
        Object cacheList = indusPreferDao.listCache(SQLHelper.SELECTED + "= ?", new String[]{"1"});
        if (cacheList != null && !((List) cacheList).isEmpty()) {
            userExist = true;
            List<Map<String, String>> maplist = (List) cacheList;
            int count = maplist.size();
            List<IndusPreferItem> list = new ArrayList<IndusPreferItem>();
            for (int i = 0; i < count; i++) {
                IndusPreferItem navigate = new IndusPreferItem();
                navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
                navigate.setName(maplist.get(i).get(SQLHelper.NAME));
                navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
                navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
                list.add(navigate);
            }
            return list;
        }
        initDefaultChannel();
        return defaultUserChannels;
    }

    /**
     * 获取其他的频道
     *
     * @return 数据库存在用户配置 ? 数据库内的其它频道 : 默认其它频道 ;
     */
    public List<IndusPreferItem> getOtherChannel() {
        Object cacheList = indusPreferDao.listCache(SQLHelper.SELECTED + "= ?", new String[]{"0"});
        List<IndusPreferItem> list = new ArrayList<IndusPreferItem>();
        if (cacheList != null && !((List) cacheList).isEmpty()) {
            List<Map<String, String>> maplist = (List) cacheList;
            int count = maplist.size();
            for (int i = 0; i < count; i++) {
                IndusPreferItem navigate = new IndusPreferItem();
                navigate.setId(Integer.valueOf(maplist.get(i).get(SQLHelper.ID)));
                navigate.setName(maplist.get(i).get(SQLHelper.NAME));
                navigate.setOrderId(Integer.valueOf(maplist.get(i).get(SQLHelper.ORDERID)));
                navigate.setSelected(Integer.valueOf(maplist.get(i).get(SQLHelper.SELECTED)));
                list.add(navigate);
            }
            return list;
        }
        if (userExist) {
            return list;
        }
        cacheList = defaultOtherChannels;
        return (List<IndusPreferItem>) cacheList;
    }

    /**
     * 保存用户频道到数据库
     *
     * @param userList
     */
    public static void saveUserChannel(List<IndusPreferItem> userList) {
        for (int i = 0; i < userList.size(); i++) {
            IndusPreferItem indusPreferItem = (IndusPreferItem) userList.get(i);
            indusPreferItem.setOrderId(i);
            indusPreferItem.setSelected(Integer.valueOf(1));
            indusPreferDao.addCache(indusPreferItem);
        }
    }

    /**
     * 保存其他频道到数据库
     *
     * @param otherList
     */
    public static void saveOtherChannel(List<IndusPreferItem> otherList) {
        for (int i = 0; i < otherList.size(); i++) {
            IndusPreferItem indusPreferItem = (IndusPreferItem) otherList.get(i);
            indusPreferItem.setOrderId(i);
            indusPreferItem.setSelected(Integer.valueOf(0));
            indusPreferDao.addCache(indusPreferItem);
        }
    }

    /**
     * 初始化数据库内的频道数据
     */
    public static void initDefaultChannel() {
        Log.i("deleteAll", "deleteAll");
        deleteAllChannel();
        Log.i("deleteAll", "deleteAll" + defaultUserChannels.size());

        saveUserChannel(defaultUserChannels);
        saveOtherChannel(defaultOtherChannels);
    }
}