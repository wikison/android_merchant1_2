package com.zemult.merchant.model;

import com.google.gson.annotations.Expose;
import com.zemult.merchant.util.Convert;

import java.io.Serializable;
import java.util.List;


/**
 * Created by zhangkai on 2016/6/8.
 */

public class M_Bill implements Serializable {
    @Expose
    public int billId;    //	账单id
    @Expose
    public int userPayId;    //	账单id
    @Expose
    public int userId;    //	用户id
    @Expose
    public int merchantId;   //商户的id
    @Expose
    public String merchantName;//	商家名称
    @Expose
    public String merchantHead;//商家头像
    @Expose
    public double money;    //	金额
    @Expose
    public double allMoney;  //消费总额(type=0时有值)
    @Expose
    public double payMoney;//实付金额(type=0时有值)
    @Expose
    public int moneyType;    //	支付方式(0:账户余额,1:支付宝账户)
    @Expose
    public String payTime;    //付款时间
    @Expose
    public int inCome;    //	(0:收入,1:支出)
    @Expose
    public int type;    //	类型(0:支付买单,1:支付红包,2:支付绑定支付宝账户,3:取现,4:领取红包,5:红包退还)
    @Expose
    public String note;    //	描述内容
    @Expose
    public String createtime;    //	时间"yyyy-MM-dd HH:mm:ss"
    @Expose
    public String number;    //	订单号(类型0/1/2 时有值)
    @Expose
    public int state;  //订单状态(0:未付款,1:已付款,2:已失效(超时未支付))
    @Expose
    public int infoId;    //	任务ID
    @Expose
    public int withdrawState;   //提现状态(0:提现中,1:提现成功,2:提现失败)
    @Expose
    public String completeTime;//实际到账时间"yyyy-MM-dd HH:mm:ss"
    @Expose
    public String expectTime;//预计到账时间"yyyy-MM-dd HH:mm:ss"
    @Expose
    public double realMoney;//到账金额(提现金额)
    @Expose
    public double serviceMoney;//手续费
    @Expose
    public String bankCard;//商户银行账号

    @Expose
    public int isTaskIndustryRecord;//是否 通过营销经理分享的探索记录买单(0:否,1是)
    @Expose
    public String title;//探索名称
    @Expose
    public String userHead;//消费用户头像(type=0时有值)
    @Expose
    public String userName;//消费用户名称(type=0时有值)
    @Expose
    public int isVoucher;//是否使用了优惠券(0:否,1是)
    @Expose
    public double voucherMoney;//优惠券抵用金额(isVoucher=1时有值)
    @Expose
    public double discount;//折扣(isTaskIndustryRecord=1时有值)
    @Expose
    public double discountMoney;//折扣金额(isTaskIndustryRecord=1时有值)
    @Expose
    public int saleUserId;//营销经理id
    @Expose
    public String saleUserHead;//营销经理头像(isTaskIndustryRecord=1时有值)
    @Expose
    public String saleUserName;//营销经理名称(isTaskIndustryRecord=1时有值)
    @Expose
    public String managerHead;//营销经理头像(isTaskIndustryRecord=1时有值)
    @Expose
    public String managerName;//营销经理名称(isTaskIndustryRecord=1时有值)
    @Expose
    public double commissionMoney;//佣金金额(isTaskIndustryRecord=1时有值)
    @Expose
    public double merchantMoney;//商家入账金额(isTaskIndustryRecord=1时有值)
    @Expose
    public double saleUserMoney;//营销经理获得的佣金金额(为支付单金额*佣金比例)
    @Expose
    public int isComment; //是否评价(0:否,1:是)
    @Expose
    public int comment; //评分

    public String getCommissionMoney() {
        return Convert.getMoneyString(commissionMoney);
    }

    @Expose
    public String toUserHead;//赠送对象的用户头像
    @Expose
    public String toUserName;//赠送对象的用户名称
    @Expose
    public int toUserId;//接受用户id(type=3时有值)
    @Expose
    public String presentName;//礼物名称
    @Expose
    public String presentPic;//礼物图片
    @Expose
    public double allPrice;//兑换总金额
    @Expose
    public List<M_Present> presentList;//兑换礼物清单
    @Expose
    public String replayNote;//回复


}
