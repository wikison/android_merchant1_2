package com.zemult.merchant.aip.mine;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserReservationList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

/**
 * Created by admin on 2017/3/16.
 */
//用户的邀请函列表
public class User2OrderInvitationListRequest extends PostStringRequest<Type> {

    public static class Input {
        public int page;    //	获取第x页的数据
        public int rows;    //
        public int userId;    //

        public String ejson;


        public void convertJosn(){
            ejson= Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public User2OrderInvitationListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_ORDERINVITATION_LIST,input.ejson , new TypeToken<APIM_UserReservationList>() {
        }.getType() , listener);

    }
}

