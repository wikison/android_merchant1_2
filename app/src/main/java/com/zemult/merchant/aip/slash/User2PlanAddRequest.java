package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 发布服务方案
public class User2PlanAddRequest extends PostStringRequest<Type> {
    public static class Input {
        public int saleUserId;       //  服务管家的用户id
        public int merchantId;       //  商家id
        public int state;       //  状态(0:未启用,1:已启用)
        public String name;       //  标题
        public String note;       //  内容
        public String pics = "";       // 图片，多张","分隔
        public String ejson;

    public void convertJosn(){
//        JSONObject obj = new JSONObject();
//
//        try {
//            obj.put("saleUserId", saleUserId + "");
//            obj.put("merchantId", merchantId + "");
//            obj.put("state", state + "");
//            obj.put("name", name);
//            obj.put("note", note);
//            obj.put("pics", pics);
//
//            ejson= Convert.securityJson(obj.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("saleUserId", saleUserId + ""),
                new Pair<String, String>("merchantId", merchantId + ""),
                new Pair<String, String>("state", state + ""),
                new Pair<String, String>("name", name),
                new Pair<String, String>("note", note),
                new Pair<String, String>("pics", pics)
        ));


    }
    }

    public User2PlanAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_PLAN_ADD, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}