package com.zemult.merchant.aip.slash;//用户退出登录

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 管家保存服务数据到临时表
public class Merchant2SaveResOrderTmp extends PostStringRequest<Type> {
    public static class Input {
        public int planId;       //  方案id
        public String   merchantId	;		//	是	商家id
        public int  saleUserId	;		//	是	服务管家用户id
        public String  reservationTime	;		//	是	时间
        public String  num		;	//	是	人数
        public String  note	;	//是	包厢/房间号
        public String  reservationMoney		;//	Double		订金


        public int isRoom;// 是否填写了房间信息(0:否,1:是)
        public int       roomNum;//房间数
        public String       checkInTime;//入住时间
        public String checkOutTime;// 离开时间
        public String userName;//联系人名称
        public String       userPhone;//联系人电话


        public String ejson;

    public void convertJosn(){
        ejson= Convert.securityJson(Convert.pairsToJson(
                new Pair<String, String>("planId", planId + ""),
                new Pair<String, String>("merchantId", merchantId + ""),
                new Pair<String, String>("saleUserId", saleUserId + ""),
                new Pair<String, String>("reservationTime", reservationTime),
                new Pair<String, String>("num", num + ""),
                new Pair<String, String>("note", note + ""),
                new Pair<String, String>("reservationMoney", reservationMoney + ""),
                new Pair<String, String>("isRoom", isRoom+""),
                new Pair<String, String>("roomNum", roomNum+""),
                new Pair<String, String>("checkInTime", checkInTime),
                new Pair<String, String>("checkOutTime", checkOutTime),
                new Pair<String, String>("userName", userName),
                new Pair<String, String>("userPhone", userPhone),
                new Pair<String, String>("note", note)
        ));
    }
    }

    public Merchant2SaveResOrderTmp(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.MERCHANT2_SAVERESORDERTMP, input.ejson ,new TypeToken<CommonResult>() {
        }.getType(), listener);
    }
}