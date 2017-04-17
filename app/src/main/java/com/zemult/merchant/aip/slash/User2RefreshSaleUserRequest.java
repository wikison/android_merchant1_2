package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import cn.trinea.android.common.util.StringUtils;
import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 用户更新服务管家通讯录
public class User2RefreshSaleUserRequest extends PostStringRequest<Type> {
    public static class Input {
        public int userId;       //  用户id
        public String bookPhones;       // 通讯录手机号(多个用","分隔)
        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("userId", userId + ""),
                new Pair<String, String>("bookPhones", bookPhones)
        ));
    }
    }

    public User2RefreshSaleUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_REFRESH_SALEUSER, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}