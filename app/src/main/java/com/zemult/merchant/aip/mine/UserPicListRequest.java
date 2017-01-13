package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.model.apimodel.APIM_UserVoucherList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取商家相册的照片列表(非证件照)
public class UserPicListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int page;    //	获取第x页的数据
        public int rows;    //	用户id
        public int userId;    //商家(场景)id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("page", page+""),new Pair<String, String>("rows", rows+"")));
        }

    }

    public UserPicListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_PIC_LIST,input.ejson , new TypeToken<APIM_PicList>() {
        }.getType() , listener);

    }
}
