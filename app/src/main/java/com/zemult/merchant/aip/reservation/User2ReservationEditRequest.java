package com.zemult.merchant.aip.reservation;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//服务管家修改预约单(未确认的)
public class User2ReservationEditRequest extends PostStringRequest<Type>  {

    public static class Input {
        public String reservationId;       //预约单id
        public int num;       //是	人数
        public int planId;       //否 服务方案id
        public String note;       //是	包厢/房间号(备注)
        public String reservationTime;       //是	预约时间(格式为"yyyy-MM-dd HH:mm:ss")
        public String reservationMoney;       //否	定金
        public int isRoom;// 是否填写了房间信息(0:否,1:是)
        public int       roomNum;//房间数
        public String       checkInTime;//入住时间
        public String checkOutTime;// 离开时间
        public String userName;//联系人名称
        public String       userPhone;//联系人电话
        public String ejson;


        public void convertJosn(){
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("reservationId", reservationId),
                    new Pair<String, String>("num", num+""),
                    new Pair<String, String>("note", note),
                    new Pair<String, String>("planId", "0".equals(planId+"")?"":planId+""),
                    new Pair<String, String>("reservationMoney", reservationMoney),
                    new Pair<String, String>("isRoom", isRoom+""),
                    new Pair<String, String>("roomNum", roomNum+""),
                    new Pair<String, String>("checkInTime", checkInTime),
                    new Pair<String, String>("checkOutTime", checkOutTime),
                    new Pair<String, String>("userName", userName),
                    new Pair<String, String>("userPhone", userPhone),
                    new Pair<String, String>("reservationTime", reservationTime)));
        }

    }

    public User2ReservationEditRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER2_RESERVATION_EDIT,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);
    }
}
