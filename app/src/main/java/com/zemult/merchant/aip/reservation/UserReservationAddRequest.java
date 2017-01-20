package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户预约申请
public class UserReservationAddRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String ejson;
        public int  userId;//				是	用户id
        public int merchantId;//				是	商户id
        public int saleUserId;//				是	约客的用户id
        public String reservationTime;//				是	预约时间(格式为"yyyy-MM-dd HH:mm:ss")
        public String num;			//	是	人数
        public String userName;	//			是	联系人名称
        public String userPhone;//				是	联系人电话
        public String note;		//		否	备注




        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId+""),
                    new Pair<String, String>("merchantId", merchantId+""),
                    new Pair<String, String>("saleUserId", saleUserId+""),
                    new Pair<String, String>("reservationTime", reservationTime),
                    new Pair<String, String>("num", num+""),
                    new Pair<String, String>("userName", userName),
                    new Pair<String, String>("userPhone", userPhone),
                    new Pair<String, String>("note", note)));
        }

    }

    public UserReservationAddRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_RESERVATION_ADD,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
