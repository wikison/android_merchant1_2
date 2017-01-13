package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户的代金券列表
public class UserVoucherListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int userId;    //	用户id
        public int page;    //	获取第x页的数据
        public int rows;    //	每次获取的数据个数
        public int state;    //券包状态(0:可用,1:已失效--包含已用的)

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),  new Pair<String, String>("state", state+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserVoucherListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_VOUCHERLIST,input.ejson , new TypeToken<APIM_UserVoucherList>() {
        }.getType() , listener);

    }
}
