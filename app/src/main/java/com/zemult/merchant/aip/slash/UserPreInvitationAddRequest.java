package com.zemult.merchant.aip.slash;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户发起预邀单
public class UserPreInvitationAddRequest extends PostStringRequest<Type> {

    public static class Input {
        public int userId;    //	用户id
        public int titleId;    //	主题id
        public String invitationTime;    //	活动时间(格式为"yyyy-MM-dd HH:mm:ss")


        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("titleId", titleId + ""),
                    new Pair<String, String>("invitationTime", invitationTime)));

        }

    }

    public UserPreInvitationAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.USER_PRE_INVITATION_ADD, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }
}
