package com.zemult.merchant.aip.task;

import android.util.Pair;

import com.google.gson.reflect.TypeToken;
import com.zemult.merchant.config.Urls;
import com.zemult.merchant.model.CommonResult;
import com.zemult.merchant.util.Convert;

import java.lang.reflect.Type;

import zema.volley.network.PostStringRequest;
import zema.volley.network.ResponseListener;

// 商家发布新的角色任务
public class TaskIndustryPushMerchantRequest extends PostStringRequest<Type> {

    public TaskIndustryPushMerchantRequest(Input input, ResponseListener listener) {
        super(Urls.BASIC_URL + Urls.TASK_INDUSTRY_PUSH_MERCHANT, input.ejson, new TypeToken<CommonResult>() {
        }.getType(), listener);

    }

    public static class Input {
        public int userId;    //	用户id
        public int merchantId;    //		是	商户id
        public int industryId;    //		是	角色id
        public String title;    //		是	标题
        public String note;    //	是	描述
        public String endTime;    //		是	截止时间(格式为:yyyy-MM-dd HH:mm:ss)
        public int type;    //	探索方式(0:普通任务,3:买单/消费)
        public int isVote;    //		是否 有投票插件(0:否,1:是)
        public String voteTitle;    //			否	投票主题(投票类必填)
        public int voteType;    //		否	投票方式(投票类必填)(0:单选，1:多选)
        public String voteNotes;    //			否	投票选项(投票类必填)(多个","分隔)
        public int isVoucher;    //			是否有优惠券奖励(0:否,1:是)
        public double voucherMoney;    //			否	代金券单张抵扣金额(代金券奖励必填)
        public int voucherNum;    //		否	代金券数量(代金券奖励必填)
        public double voucherMinMoney;    //			否	代金券最低消费额(代金券奖励必填)
        public String voucherNote;    //			否	代金券备注,使用规则(代金券奖励必填)
        public String voucherEndTime;    //			否	代金券有效截止时间(格式为:yyyy-MM-dd HH:mm:ss)(代金券奖励必填)
        public double discount;   //  否  折扣(type=3时有值)(0-10)
        public double commissionDiscount;  //否   佣金百分比(type=3时有值)(0-100)

        public String ejson;


        public void convertJosn() {
            ejson = Convert.securityJson(Convert.pairsToJson(
                    new Pair<String, String>("userId", userId + ""),
                    new Pair<String, String>("industryId", industryId + ""),
                    new Pair<String, String>("merchantId", merchantId + ""),
                    new Pair<String, String>("title", title),
                    new Pair<String, String>("note", note),
                    new Pair<String, String>("endTime", endTime),
                    new Pair<String, String>("type", type + ""),
                    new Pair<String, String>("isVote", isVote + ""),
                    new Pair<String, String>("voteTitle", voteTitle),
                    new Pair<String, String>("voteType", voteType + ""),
                    new Pair<String, String>("voteNotes", voteNotes),
                    new Pair<String, String>("isVoucher", isVoucher + ""),
                    new Pair<String, String>("voucherMoney", voucherMoney + ""),
                    new Pair<String, String>("voucherNum", voucherNum + ""),
                    new Pair<String, String>("voucherMinMoney", voucherMinMoney + ""),
                    new Pair<String, String>("voucherNote", voucherNote),
                    new Pair<String, String>("voucherEndTime", voucherEndTime),
                    new Pair<String, String>("discount", discount + ""),
                    new Pair<String, String>("commissionDiscount", commissionDiscount + "")
            ));
        }

    }
}
