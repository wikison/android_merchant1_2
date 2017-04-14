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
 * Created by admin on 2017/1/20.
 */
//用户的预约单列表
public class UserReservationListRequest extends PostStringRequest<Type> {

    public static class Input {

        public int userId;    //	用户id
        public int state;   //状态(-1:全部-不包含待确认的,1:已确定,2:已支付,6:已结束(3/4是已结束的分支))
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("state", state + ""),
                    new Pair<String, String>("page", page + ""),
                    new Pair<String, String>("rows", rows + ""))
            );
        }

    }

    public UserReservationListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RESERVATION_LIST, input.ejson, new TypeToken<APIM_UserReservationList>() {
        }.getType(), listener);

    }
}
