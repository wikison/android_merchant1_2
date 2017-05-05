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

public class UserAddSaleUserRequest extends PostStringRequest<Type> {
    public static class Input {
        public int userId;       //  用户id
        public int merchantId;       //  商家(场景)的id
        public String tags;       //服务标签(多个用“,”分隔)
        public String position;       //服务职位
        public int isOnBook;       //  是否关联 通讯录(作为服务管家 0:否,1:是)
        public String bookPhones;       // 通讯录手机号(多个用","分隔)
        public String ejson;

    public void convertJosn(){
        if(StringUtils.isBlank(bookPhones)){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("position", position),
                    new Pair<String, String>("tags", tags)
            ));
        }else {
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("tags", tags),
                    new Pair<String, String>("isOnBook", isOnBook + ""),
                    new Pair<String, String>("bookPhones", bookPhones)

            ));
        }


    }
    }

    public UserAddSaleUserRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_ADD_SALEUSER_1_1, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}