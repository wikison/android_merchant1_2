package com.zemult.merchant.aip.mine;
import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

//用户角色任务--生成商户买单
public class UserTaskPayRequest extends PostStringRequest<Type>  {

    public static class Input {
        public int	userId				;	//	用户id
        public int	merchantId				;	//商户id
        public int	taskIndustryRecordId				;	//	用户角色任务领取 id
        public int	payType				;	//	支付类型(0:账户余额,1:支付宝)
        public int	buyType				;	//	买单类型(0:直接通过商户买单,1:通过营销经理的探索记录买单)
        public double	money				;	//	买单金额
        public double	commissionDiscount				;	//	佣金百分比(0-100)(通过营销经理的探索记录买单时必填)
        public int	isVoucher				;	//	是否使用代金券(0:否,1:是)
        public int	voucherUserId				;	//	代金券id(使用代金券时必填)
        public double	consumeMoney;//消费总金额

        public String ejson;

        public void convertJosn(){
            if(buyType==0){//买单类型(0:直接通过商户买单,1:通过营销经理的探索记录买单)
                ejson=Convert.securityJson(Convert.pairsToJson(
                        new Pair<String, String>("merchantId", merchantId+""),
                        new Pair<String, String>("payType", payType+""), new Pair<String, String>("money", money+""),
                        new Pair<String, String>("isVoucher", isVoucher+""),new Pair<String, String>("buyType", buyType+""),
                        new Pair<String, String>("voucherUserId", voucherUserId+""),new Pair<String, String>("consumeMoney", consumeMoney+""),
                        new Pair<String, String>("userId", userId+"")));
            }
            ejson=Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("merchantId", merchantId+""), new Pair<String, String>("taskIndustryRecordId", taskIndustryRecordId+""),
                    new Pair<String, String>("payType", payType+""), new Pair<String, String>("money", money+""),
                    new Pair<String, String>("buyType", buyType+""),
                    new Pair<String, String>("commissionDiscount", commissionDiscount+""), new Pair<String, String>("isVoucher", isVoucher+""),
                    new Pair<String, String>("voucherUserId", voucherUserId+""),new Pair<String, String>("consumeMoney", consumeMoney+""),
                    new Pair<String, String>("userId", userId+"")));

        }

    }

    public UserTaskPayRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL+Urls.USER_TASK_PAY,input.ejson , new TypeToken<CommonResult>() {
        }.getType() , listener);

    }
}
