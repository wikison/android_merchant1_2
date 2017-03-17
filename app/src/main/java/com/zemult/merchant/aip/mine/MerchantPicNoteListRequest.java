package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.apimodel.APIM_PicList;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//获取商家相册的照片列表(非证件照)
public class MerchantPicNoteListRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int picId;    //照片id

        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("picId", picId+"")));
        }

    }

    public MerchantPicNoteListRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT_PIC_NOTE_LIST,input.ejson , new TypeToken<APIM_PicList>() {
        }.getType() , listener);

    }
}
